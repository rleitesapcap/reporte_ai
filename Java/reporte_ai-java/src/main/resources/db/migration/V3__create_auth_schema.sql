-- ============================================================================
-- REPORTE AI - V3 - SCHEMA DE AUTENTICAÇÃO
-- Tabelas para usuários do sistema e roles
-- ============================================================================

-- ============================================================================
-- 1. TABELA DE ROLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS auth_roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    role_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    
    CONSTRAINT role_name_not_empty CHECK (length(role_name) > 0)
);

CREATE INDEX IF NOT EXISTS idx_auth_roles_role_name ON auth_roles(role_name);
CREATE INDEX IF NOT EXISTS idx_auth_roles_is_active ON auth_roles(is_active);

-- ============================================================================
-- 2. TABELA DE USUÁRIOS DE AUTENTICAÇÃO
-- ============================================================================

CREATE TABLE IF NOT EXISTS auth_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    full_name VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_locked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    
    CONSTRAINT username_not_empty CHECK (length(username) > 0),
    CONSTRAINT email_not_empty CHECK (length(email) > 0),
    CONSTRAINT failed_attempts_non_negative CHECK (failed_login_attempts >= 0)
);

CREATE INDEX IF NOT EXISTS idx_auth_users_username ON auth_users(username);
CREATE INDEX IF NOT EXISTS idx_auth_users_email ON auth_users(email);
CREATE INDEX IF NOT EXISTS idx_auth_users_is_active ON auth_users(is_active);
CREATE INDEX IF NOT EXISTS idx_auth_users_is_locked ON auth_users(is_locked);

-- ============================================================================
-- 3. TABELA DE ASSOCIAÇÃO - USUÁRIOS E ROLES
-- ============================================================================

CREATE TABLE IF NOT EXISTS auth_user_roles (
    user_id UUID NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES auth_roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX IF NOT EXISTS idx_auth_user_roles_user_id ON auth_user_roles(user_id);
CREATE INDEX IF NOT EXISTS idx_auth_user_roles_role_id ON auth_user_roles(role_id);

-- ============================================================================
-- 4. TABELA DE TOKEN BLACKLIST (para revogação de tokens)
-- ============================================================================

CREATE TABLE IF NOT EXISTS token_blacklist (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    token_jti VARCHAR(500) NOT NULL UNIQUE,
    user_id UUID NOT NULL REFERENCES auth_users(id) ON DELETE CASCADE,
    token_type VARCHAR(50) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    blacklisted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(500)
);

CREATE INDEX IF NOT EXISTS idx_token_blacklist_user_id ON token_blacklist(user_id);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_expires_at ON token_blacklist(expires_at);
CREATE INDEX IF NOT EXISTS idx_token_blacklist_token_jti ON token_blacklist(token_jti);

-- ============================================================================
-- 5. SEEDING - ROLES PADRÃO
-- ============================================================================

INSERT INTO auth_roles (id, role_name, description, is_active) VALUES
    (uuid_generate_v4(), 'ADMIN', 'Administrador do sistema com acesso total', TRUE),
    (uuid_generate_v4(), 'ANALYST', 'Analista de dados', TRUE),
    (uuid_generate_v4(), 'VALIDATOR', 'Validador de ocorrências', TRUE),
    (uuid_generate_v4(), 'USER', 'Usuário padrão', TRUE),
    (uuid_generate_v4(), 'NOTIFICATION_SENDER', 'Pode enviar notificações', TRUE),
    (uuid_generate_v4(), 'REPORT_CREATOR', 'Pode criar relatórios', TRUE)
ON CONFLICT (role_name) DO NOTHING;

-- ============================================================================
-- 6. SEEDING - USUÁRIOS DE TESTE
-- ============================================================================
-- Senhas em hash (bcrypt):
-- admin123 -> $2a$10$SlVZQkVEQkVEQkVEQkVEQkU... (gerado dinamicamente na primeira execução)
-- 
-- Nota: Em produção, remova estes usuários de teste

-- Inserindo usuários de teste (as senhas serão atualizadas na primeira execução da aplicação)
INSERT INTO auth_users (id, username, email, password_hash, full_name, is_active, is_locked, failed_login_attempts)
VALUES
    (uuid_generate_v4(), 'admin', 'admin@reporteai.local', '$2a$10$SlVZQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVELi', 'Administrador', TRUE, FALSE, 0),
    (uuid_generate_v4(), 'analyst', 'analyst@reporteai.local', '$2a$10$SlVZQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVELi', 'Analista', TRUE, FALSE, 0),
    (uuid_generate_v4(), 'user', 'user@reporteai.local', '$2a$10$SlVZQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVELi', 'Usuário Padrão', TRUE, FALSE, 0),
    (uuid_generate_v4(), 'validator', 'validator@reporteai.local', '$2a$10$SlVZQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVEQkVELi', 'Validador', TRUE, FALSE, 0)
ON CONFLICT (username) DO NOTHING;

-- ============================================================================
-- 7. ASSOCIAR ROLES AOS USUÁRIOS DE TESTE
-- ============================================================================

-- Admin user gets ADMIN and USER roles
INSERT INTO auth_user_roles (user_id, role_id)
SELECT au.id, ar.id FROM auth_users au, auth_roles ar 
WHERE au.username = 'admin' AND ar.role_name IN ('ADMIN', 'USER')
ON CONFLICT DO NOTHING;

-- Analyst user gets ANALYST and USER roles
INSERT INTO auth_user_roles (user_id, role_id)
SELECT au.id, ar.id FROM auth_users au, auth_roles ar 
WHERE au.username = 'analyst' AND ar.role_name IN ('ANALYST', 'USER')
ON CONFLICT DO NOTHING;

-- User gets USER role
INSERT INTO auth_user_roles (user_id, role_id)
SELECT au.id, ar.id FROM auth_users au, auth_roles ar 
WHERE au.username = 'user' AND ar.role_name = 'USER'
ON CONFLICT DO NOTHING;

-- Validator user gets VALIDATOR and USER roles
INSERT INTO auth_user_roles (user_id, role_id)
SELECT au.id, ar.id FROM auth_users au, auth_roles ar 
WHERE au.username = 'validator' AND ar.role_name IN ('VALIDATOR', 'USER')
ON CONFLICT DO NOTHING;
