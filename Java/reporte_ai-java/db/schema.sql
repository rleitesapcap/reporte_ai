-- ============================================================================
-- REPORTE AI - DATABASE SCHEMA (PostgreSQL)
-- Plataforma Inteligente de Mapeamento de Problemas Urbanos e Rurais
-- Capitólio-MG
-- ============================================================================

-- Extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "postgis";

-- ============================================================================
-- 1. TABELAS BASE - USUÁRIOS E CONSENTIMENTO
-- ============================================================================

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone_number VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_interaction TIMESTAMP,
    
    -- Confiabilidade e Controle
    trust_score DECIMAL(4,2) DEFAULT 1.0,
    total_occurrences INT DEFAULT 0,
    occurrences_with_photo INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- LGPD - Governança de Dados
    anonymized BOOLEAN DEFAULT FALSE,
    anonymized_at TIMESTAMP,
    
    CONSTRAINT trust_score_range CHECK (trust_score >= 0 AND trust_score <= 5)
);

CREATE INDEX idx_users_phone ON users(phone_number);
CREATE INDEX idx_users_created_at ON users(created_at);
CREATE INDEX idx_users_is_active ON users(is_active);

-- ============================================================================

CREATE TABLE user_consent (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    consent_type VARCHAR(100) NOT NULL,
    accepted BOOLEAN NOT NULL,
    consent_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    document_version VARCHAR(50),
    ip_address VARCHAR(50),
    
    CONSTRAINT consent_type_check CHECK (consent_type IN ('data_processing', 'photo_storage', 'location', 'contact'))
);

CREATE INDEX idx_user_consent_user_id ON user_consent(user_id);
CREATE INDEX idx_user_consent_type ON user_consent(consent_type);

-- ============================================================================
-- 2. TABELAS DE CATEGORIZAÇÃO
-- ============================================================================

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL UNIQUE,
    description TEXT,
    color VARCHAR(7),
    icon_url VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    ORDER BY name
);

CREATE INDEX idx_categories_is_active ON categories(is_active);

-- ============================================================================

CREATE TABLE sub_categories (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    category_id UUID NOT NULL REFERENCES categories(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE(category_id, name),
    CONSTRAINT valid_name CHECK (length(name) > 0)
);

CREATE INDEX idx_sub_categories_category_id ON sub_categories(category_id);
CREATE INDEX idx_sub_categories_is_active ON sub_categories(is_active);

-- ============================================================================
-- 3. TABELA PRINCIPAL - OCORRÊNCIAS
-- ============================================================================

CREATE TABLE occurrences (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE SET NULL,
    category_id UUID NOT NULL REFERENCES categories(id),
    sub_category_id UUID REFERENCES sub_categories(id),
    
    -- Identificação Única
    protocol_id VARCHAR(50) NOT NULL UNIQUE,
    
    -- Informações Básicas
    description TEXT NOT NULL,
    additional_notes TEXT,
    
    -- Localização
    neighborhood VARCHAR(255),
    reference_point VARCHAR(500),
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    geom GEOMETRY(Point, 4326),
    
    -- Priorização e Análise
    severity INT DEFAULT 1,
    frequency INT DEFAULT 1,
    priority_score DECIMAL(8, 2),
    recurrence_count INT DEFAULT 0,
    
    -- Evidência (Foto)
    has_photo BOOLEAN DEFAULT FALSE,
    photo_count INT DEFAULT 0,
    
    -- Status da Ocorrência
    status VARCHAR(50) DEFAULT 'received',
    confidence_level DECIMAL(4, 2),
    
    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    resolved_at TIMESTAMP,
    
    -- Controle
    is_duplicate BOOLEAN DEFAULT FALSE,
    duplicate_main_occurrence_id UUID REFERENCES occurrences(id),
    
    CONSTRAINT valid_severity CHECK (severity >= 1 AND severity <= 5),
    CONSTRAINT valid_frequency CHECK (frequency >= 1 AND frequency <= 5),
    CONSTRAINT valid_status CHECK (status IN ('received', 'validating', 'validated', 'discarded', 'resolved')),
    CONSTRAINT valid_confidence CHECK (confidence_level >= 0 AND confidence_level <= 1),
    CONSTRAINT lat_lon_pair CHECK (
        (latitude IS NULL AND longitude IS NULL) OR 
        (latitude IS NOT NULL AND longitude IS NOT NULL)
    )
);

-- Índices para Otimização de Queries
CREATE INDEX idx_occurrences_user_id ON occurrences(user_id);
CREATE INDEX idx_occurrences_category_id ON occurrences(category_id);
CREATE INDEX idx_occurrences_sub_category_id ON occurrences(sub_category_id);
CREATE INDEX idx_occurrences_status ON occurrences(status);
CREATE INDEX idx_occurrences_created_at ON occurrences(created_at);
CREATE INDEX idx_occurrences_priority_score ON occurrences(priority_score DESC);
CREATE INDEX idx_occurrences_protocol_id ON occurrences(protocol_id);
CREATE INDEX idx_occurrences_neighborhood ON occurrences(neighborhood);
CREATE INDEX idx_occurrences_geom ON occurrences USING GIST(geom);
CREATE INDEX idx_occurrences_is_duplicate ON occurrences(is_duplicate);
CREATE INDEX idx_occurrences_created_neighborhood ON occurrences(created_at, neighborhood);

-- ============================================================================
-- 4. TABELA DE IMAGENS/EVIDÊNCIAS
-- ============================================================================

CREATE TABLE occurrence_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    occurrence_id UUID NOT NULL REFERENCES occurrences(id) ON DELETE CASCADE,
    s3_url VARCHAR(500) NOT NULL,
    s3_key VARCHAR(500) NOT NULL UNIQUE,
    image_size INT,
    image_format VARCHAR(10),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    processed BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT valid_format CHECK (image_format IN ('jpg', 'jpeg', 'png', 'webp'))
);

CREATE INDEX idx_occurrence_images_occurrence_id ON occurrence_images(occurrence_id);
CREATE INDEX idx_occurrence_images_uploaded_at ON occurrence_images(uploaded_at);

-- ============================================================================
-- 5. TABELA DE DEDUPLICAÇÃO
-- ============================================================================

CREATE TABLE deduplication_records (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    main_occurrence_id UUID NOT NULL REFERENCES occurrences(id) ON DELETE CASCADE,
    duplicate_occurrence_id UUID NOT NULL REFERENCES occurrences(id) ON DELETE CASCADE,
    
    -- Critérios de Deduplicação
    similarity_score DECIMAL(5, 2),
    geographic_distance_meters DECIMAL(10, 2),
    time_difference_minutes INT,
    
    -- Razão da Deduplicação
    dedup_reason VARCHAR(255),
    dedup_method VARCHAR(50),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT different_occurrences CHECK (main_occurrence_id != duplicate_occurrence_id),
    CONSTRAINT valid_similarity CHECK (similarity_score >= 0 AND similarity_score <= 100),
    CONSTRAINT valid_method CHECK (dedup_method IN ('text_similarity', 'geographic_proximity', 'temporal_proximity', 'combined'))
);

CREATE INDEX idx_dedup_main_occurrence ON deduplication_records(main_occurrence_id);
CREATE INDEX idx_dedup_duplicate_occurrence ON deduplication_records(duplicate_occurrence_id);
CREATE INDEX idx_dedup_method ON deduplication_records(dedup_method);

-- ============================================================================
-- 6. TABELA DE VALIDAÇÃO
-- ============================================================================

CREATE TABLE validations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    occurrence_id UUID NOT NULL REFERENCES occurrences(id) ON DELETE CASCADE,
    validator_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    
    -- Tipo e Resultado de Validação
    validation_type VARCHAR(50),
    result VARCHAR(50) NOT NULL,
    reason TEXT,
    confidence DECIMAL(4, 2),
    
    -- Validadores
    multiple_reports_count INT,
    
    -- Timestamps
    validated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT valid_type CHECK (validation_type IN ('manual', 'automatic', 'community')),
    CONSTRAINT valid_result CHECK (result IN ('validated', 'rejected', 'suspicious', 'pending')),
    CONSTRAINT valid_confidence CHECK (confidence >= 0 AND confidence <= 1)
);

CREATE INDEX idx_validations_occurrence_id ON validations(occurrence_id);
CREATE INDEX idx_validations_validator_user_id ON validations(validator_user_id);
CREATE INDEX idx_validations_result ON validations(result);
CREATE INDEX idx_validations_validated_at ON validations(validated_at);

-- ============================================================================
-- 7. TABELA DE RASTREAMENTO (AUDIT TRAIL)
-- ============================================================================

CREATE TABLE occurrence_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    occurrence_id UUID NOT NULL REFERENCES occurrences(id) ON DELETE CASCADE,
    
    changed_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(50),
    
    old_status VARCHAR(50),
    new_status VARCHAR(50),
    
    old_priority_score DECIMAL(8, 2),
    new_priority_score DECIMAL(8, 2),
    
    change_reason TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT valid_action CHECK (action IN ('created', 'updated', 'validated', 'duplicated', 'resolved', 'status_changed'))
);

CREATE INDEX idx_occurrence_history_occurrence_id ON occurrence_history(occurrence_id);
CREATE INDEX idx_occurrence_history_created_at ON occurrence_history(created_at);
CREATE INDEX idx_occurrence_history_action ON occurrence_history(action);

-- ============================================================================
-- 8. TABELA DE INDICADORES
-- ============================================================================

CREATE TABLE indicators (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    indicator_name VARCHAR(255) NOT NULL,
    indicator_type VARCHAR(50) NOT NULL,
    description TEXT,
    
    -- Valores
    value DECIMAL(15, 2),
    unit VARCHAR(50),
    
    -- Dimensões
    category_id UUID REFERENCES categories(id),
    neighborhood VARCHAR(255),
    
    -- Período
    period_start DATE,
    period_end DATE,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT valid_type CHECK (indicator_type IN ('operational', 'analytical', 'quality', 'impact'))
);

CREATE INDEX idx_indicators_indicator_type ON indicators(indicator_type);
CREATE INDEX idx_indicators_category_id ON indicators(category_id);
CREATE INDEX idx_indicators_period ON indicators(period_start, period_end);
CREATE INDEX idx_indicators_calculated_at ON indicators(calculated_at);

-- ============================================================================
-- 9. TABELA DE RELATÓRIOS
-- ============================================================================

CREATE TABLE reports (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(500) NOT NULL,
    report_type VARCHAR(50),
    description TEXT,
    
    -- Arquivo
    file_path VARCHAR(500),
    file_size INT,
    
    -- Período
    period_start DATE,
    period_end DATE,
    
    -- Geração
    generated_by_user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    generated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Status
    status VARCHAR(50) DEFAULT 'pending',
    
    CONSTRAINT valid_report_type CHECK (report_type IN ('monthly', 'executive', 'regional', 'categorical', 'custom')),
    CONSTRAINT valid_status CHECK (status IN ('pending', 'processing', 'completed', 'failed'))
);

CREATE INDEX idx_reports_report_type ON reports(report_type);
CREATE INDEX idx_reports_generated_at ON reports(generated_at);
CREATE INDEX idx_reports_period ON reports(period_start, period_end);
CREATE INDEX idx_reports_status ON reports(status);

-- ============================================================================
-- 10. TABELA DE ANÁLISE ESPACIAL (CLUSTERING)
-- ============================================================================

CREATE TABLE spatial_clusters (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    cluster_name VARCHAR(255),
    neighborhood VARCHAR(255),
    
    center_latitude DECIMAL(10, 8),
    center_longitude DECIMAL(11, 8),
    center_geom GEOMETRY(Point, 4326),
    
    radius_meters DECIMAL(10, 2),
    occurrence_count INT,
    
    density_score DECIMAL(8, 2),
    severity_avg DECIMAL(5, 2),
    priority_score_avg DECIMAL(8, 2),
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_spatial_clusters_neighborhood ON spatial_clusters(neighborhood);
CREATE INDEX idx_spatial_clusters_geom ON spatial_clusters USING GIST(center_geom);
CREATE INDEX idx_spatial_clusters_density_score ON spatial_clusters(density_score DESC);

-- ============================================================================
-- 11. TABELA DE RATE LIMITING (CONTROLE DE SPAM)
-- ============================================================================

CREATE TABLE user_rate_limit (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    
    daily_limit INT DEFAULT 10,
    hourly_limit INT DEFAULT 3,
    
    occurrences_today INT DEFAULT 0,
    occurrences_this_hour INT DEFAULT 0,
    
    last_reset_date DATE,
    last_reset_hour INT,
    
    is_blocked BOOLEAN DEFAULT FALSE,
    blocked_until TIMESTAMP,
    
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_rate_limit_user_id ON user_rate_limit(user_id);
CREATE INDEX idx_user_rate_limit_is_blocked ON user_rate_limit(is_blocked);

-- ============================================================================
-- 12. TABELA DE NOTIFICAÇÕES
-- ============================================================================

CREATE TABLE notifications (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    occurrence_id UUID REFERENCES occurrences(id) ON DELETE CASCADE,
    
    notification_type VARCHAR(50),
    title VARCHAR(255),
    message TEXT,
    
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP,
    is_read BOOLEAN DEFAULT FALSE,
    
    CONSTRAINT valid_notification_type CHECK (notification_type IN ('protocol_generated', 'status_updated', 'duplicate_detected', 'resolved', 'feedback'))
);

CREATE INDEX idx_notifications_user_id ON notifications(user_id);
CREATE INDEX idx_notifications_is_read ON notifications(is_read);
CREATE INDEX idx_notifications_sent_at ON notifications(sent_at);

-- ============================================================================
-- 13. TABELA DE CONFIGURAÇÕES DO SISTEMA
-- ============================================================================

CREATE TABLE system_settings (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    setting_key VARCHAR(255) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type VARCHAR(50),
    description TEXT,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_system_settings_key ON system_settings(setting_key);

-- ============================================================================
-- VIEWS ANALÍTICAS (Facilitar Dashboards e Relatórios)
-- ============================================================================

CREATE VIEW v_occurrences_by_category AS
SELECT 
    c.id,
    c.name as category_name,
    COUNT(o.id) as total_occurrences,
    COUNT(CASE WHEN o.status = 'validated' THEN 1 END) as validated_count,
    COUNT(CASE WHEN o.has_photo THEN 1 END) as with_photo_count,
    AVG(o.priority_score) as avg_priority_score,
    AVG(o.severity) as avg_severity
FROM categories c
LEFT JOIN occurrences o ON c.id = o.category_id
WHERE c.is_active = TRUE
GROUP BY c.id, c.name;

-- ============================================================================

CREATE VIEW v_occurrences_by_neighborhood AS
SELECT 
    neighborhood,
    COUNT(*) as total_occurrences,
    COUNT(CASE WHEN status = 'validated' THEN 1 END) as validated_count,
    COUNT(CASE WHEN has_photo THEN 1 END) as with_photo_count,
    AVG(priority_score) as avg_priority_score,
    MAX(created_at) as last_occurrence_date
FROM occurrences
WHERE neighborhood IS NOT NULL AND is_duplicate = FALSE
GROUP BY neighborhood
ORDER BY total_occurrences DESC;

-- ============================================================================

CREATE VIEW v_user_statistics AS
SELECT 
    u.id,
    u.phone_number,
    u.name,
    u.trust_score,
    COUNT(o.id) as total_occurrences,
    COUNT(CASE WHEN o.has_photo THEN 1 END) as occurrences_with_photo,
    COUNT(CASE WHEN o.status = 'validated' THEN 1 END) as validated_occurrences,
    u.created_at,
    u.last_interaction
FROM users u
LEFT JOIN occurrences o ON u.id = o.user_id
WHERE u.is_active = TRUE AND u.anonymized = FALSE
GROUP BY u.id;

-- ============================================================================

CREATE VIEW v_daily_statistics AS
SELECT 
    DATE(created_at) as occurrence_date,
    COUNT(*) as total_occurrences,
    COUNT(DISTINCT user_id) as unique_users,
    COUNT(CASE WHEN has_photo THEN 1 END) as with_photo,
    COUNT(CASE WHEN status = 'validated' THEN 1 END) as validated,
    AVG(priority_score) as avg_priority_score
FROM occurrences
WHERE is_duplicate = FALSE
GROUP BY DATE(created_at)
ORDER BY occurrence_date DESC;

-- ============================================================================
-- PROCEDURES E FUNCTIONS (Opcional - Para Lógica Complexa)
-- ============================================================================

-- Função para gerar Protocol ID único
CREATE OR REPLACE FUNCTION generate_protocol_id()
RETURNS VARCHAR AS $$
DECLARE
    protocol_id VARCHAR;
BEGIN
    protocol_id := 'CAP-' || TO_CHAR(NOW(), 'YYYY') || '-' || LPAD(CAST(nextval('protocol_sequence') AS VARCHAR), 6, '0');
    RETURN protocol_id;
END;
$$ LANGUAGE plpgsql;

-- Criar sequência para protocol_id
CREATE SEQUENCE protocol_sequence START 1;

-- ============================================================================
-- FUNÇÃO: Calcular Priority Score
-- ============================================================================

CREATE OR REPLACE FUNCTION calculate_priority_score(
    p_severity INT,
    p_frequency INT,
    p_density DECIMAL,
    p_recurrence INT,
    p_has_photo BOOLEAN
) RETURNS DECIMAL AS $$
DECLARE
    score DECIMAL;
BEGIN
    score := (p_severity * 3.0) + 
             (p_frequency * 2.0) + 
             COALESCE(p_density, 0) +
             p_recurrence +
             (CASE WHEN p_has_photo THEN 1.0 ELSE 0 END);
    RETURN ROUND(score, 2);
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- TRIGGERS
-- ============================================================================

-- Trigger para atualizar updated_at em occurrences
CREATE OR REPLACE FUNCTION update_occurrence_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER occurrence_update_timestamp
BEFORE UPDATE ON occurrences
FOR EACH ROW
EXECUTE FUNCTION update_occurrence_timestamp();

-- ============================================================================

-- Trigger para atualizar estatísticas do usuário
CREATE OR REPLACE FUNCTION update_user_statistics()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE users 
    SET total_occurrences = total_occurrences + 1,
        occurrences_with_photo = CASE WHEN NEW.has_photo THEN occurrences_with_photo + 1 ELSE occurrences_with_photo END,
        updated_at = CURRENT_TIMESTAMP
    WHERE id = NEW.user_id;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_user_stats
AFTER INSERT ON occurrences
FOR EACH ROW
EXECUTE FUNCTION update_user_statistics();

-- ============================================================================
-- DADOS INICIAIS (SEED DATA)
-- ============================================================================

-- Categorias Principais
INSERT INTO categories (name, description, color, is_active) VALUES
('Limpeza Urbana', 'Problemas relacionados a limpeza de ruas e espaços públicos', '#FF6B6B', TRUE),
('Iluminação Pública', 'Pontos com iluminação insuficiente ou danificada', '#FFD93D', TRUE),
('Vias e Acessos', 'Problemas em ruas, calçadas e acessos', '#6BCB77', TRUE),
('Limpeza de Lotes', 'Lotes com problemas de limpeza e manutenção', '#4D96FF', TRUE),
('Estradas Rurais', 'Problemas em estradas e vias rurais', '#8B4513', TRUE),
('Manutenção Rural', 'Manutenção de pontes, mata-burros e infraestrutura rural', '#FF6B9D', TRUE)
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- FIM DO SCRIPT
-- ============================================================================
