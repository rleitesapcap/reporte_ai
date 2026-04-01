-- ============================================================================
-- REPORTE AI - V2 - DADOS MESTRES (SEED DATA)
-- Tabelas de referência: categories, sub_categories, system_settings
-- Executado automaticamente pelo Flyway após V1
-- ============================================================================

-- ============================================================================
-- CATEGORIAS PRINCIPAIS
-- ============================================================================

INSERT INTO categories (id, name, description, color, is_active) VALUES
    (uuid_generate_v4(), 'Limpeza Urbana',      'Problemas relacionados a limpeza de ruas e espaços públicos', '#FF6B6B', TRUE),
    (uuid_generate_v4(), 'Iluminação Pública',  'Pontos com iluminação insuficiente ou danificada',            '#FFD93D', TRUE),
    (uuid_generate_v4(), 'Vias e Acessos',      'Problemas em ruas, calçadas e acessos',                      '#6BCB77', TRUE),
    (uuid_generate_v4(), 'Limpeza de Lotes',    'Lotes com problemas de limpeza e manutenção',                '#4D96FF', TRUE),
    (uuid_generate_v4(), 'Estradas Rurais',     'Problemas em estradas e vias rurais',                        '#8B4513', TRUE),
    (uuid_generate_v4(), 'Manutenção Rural',    'Manutenção de pontes, mata-burros e infraestrutura rural',   '#FF6B9D', TRUE),
    (uuid_generate_v4(), 'Saneamento',          'Problemas com esgoto, drenagem e abastecimento de água',     '#45B7D1', TRUE),
    (uuid_generate_v4(), 'Meio Ambiente',        'Desmatamento, queimadas e degradação ambiental',             '#2ECC71', TRUE),
    (uuid_generate_v4(), 'Segurança Pública',   'Pontos de risco e insegurança na cidade',                    '#E74C3C', TRUE),
    (uuid_generate_v4(), 'Outros',              'Ocorrências diversas não categorizadas',                      '#95A5A6', TRUE)
ON CONFLICT (name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Limpeza Urbana
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Lixo na via pública',       'Acúmulo de lixo em logradouros e calçadas'),
         ('Entulho irregular',          'Descarte irregular de entulho e resíduos de construção'),
         ('Animais mortos',             'Animal morto em via pública'),
         ('Pichação',                   'Grafite ou pichação em bem público'),
         ('Bueiro entupido',            'Bueiro obstruído causando alagamento')
     ) AS sc(name, description)
WHERE c.name = 'Limpeza Urbana'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Iluminação Pública
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Poste apagado',              'Poste de iluminação sem funcionamento'),
         ('Lâmpada quebrada',           'Lâmpada danificada ou piscando'),
         ('Poste danificado',           'Poste com dano físico ou risco de queda'),
         ('Ausência de iluminação',     'Trecho de via sem nenhuma iluminação')
     ) AS sc(name, description)
WHERE c.name = 'Iluminação Pública'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Vias e Acessos
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Buraco na via',              'Buraco ou cova na pista de rolamento'),
         ('Calçada danificada',         'Calçada quebrada, levantada ou irregular'),
         ('Sinalização ausente',        'Placa de trânsito ausente ou danificada'),
         ('Semáforo com defeito',       'Semáforo apagado ou com mau funcionamento'),
         ('Acesso bloqueado',           'Via ou acesso interditado irregularmente'),
         ('Valeta obstruída',           'Valeta ou sarjeta entupida')
     ) AS sc(name, description)
WHERE c.name = 'Vias e Acessos'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Limpeza de Lotes
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Mato alto',                  'Vegetação excessiva em lote particular ou público'),
         ('Lote com entulho',           'Lote com acúmulo de entulho ou resíduos'),
         ('Foco de dengue',             'Possível criadouro de mosquito Aedes aegypti'),
         ('Lote abandonado',            'Lote em estado de abandono e deterioração')
     ) AS sc(name, description)
WHERE c.name = 'Limpeza de Lotes'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Estradas Rurais
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Estrada esburacada',         'Estrada rural com buracos impedindo o tráfego'),
         ('Erosão na via',              'Voçoroca ou erosão comprometendo a estrada'),
         ('Atoleiro',                   'Trecho com lama ou atoleiro em vias rurais'),
         ('Estrada interditada',        'Estrada bloqueada por árvore ou deslizamento')
     ) AS sc(name, description)
WHERE c.name = 'Estradas Rurais'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Manutenção Rural
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Ponte danificada',           'Ponte com estrutura comprometida ou interditada'),
         ('Mata-burro quebrado',        'Mata-burro danificado em estrada rural'),
         ('Placa rural ausente',        'Sinalização de fazenda ou propriedade ausente'),
         ('Cerca danificada',           'Cerca pública danificada permitindo entrada de animais')
     ) AS sc(name, description)
WHERE c.name = 'Manutenção Rural'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Saneamento
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Esgoto a céu aberto',        'Esgoto correndo em via pública'),
         ('Falta de água',              'Interrupção no abastecimento de água'),
         ('Vazamento de água',          'Vazamento em tubulação de água potável'),
         ('Alagamento',                 'Ponto de alagamento recorrente')
     ) AS sc(name, description)
WHERE c.name = 'Saneamento'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- SUB-CATEGORIAS: Meio Ambiente
-- ============================================================================

INSERT INTO sub_categories (id, category_id, name, description, is_active)
SELECT uuid_generate_v4(), c.id, sc.name, sc.description, TRUE
FROM categories c,
     (VALUES
         ('Queimada',                   'Foco de incêndio ou queimada em área natural'),
         ('Desmatamento',               'Corte irregular de árvores nativas'),
         ('Poluição de rio ou nascente','Poluição em curso d''água natural'),
         ('Descarte irregular',         'Descarte de resíduos em área de preservação')
     ) AS sc(name, description)
WHERE c.name = 'Meio Ambiente'
ON CONFLICT (category_id, name) DO NOTHING;

-- ============================================================================
-- CONFIGURAÇÕES DO SISTEMA
-- ============================================================================

INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
    ('max_occurrences_per_day',       '10',        'integer', 'Limite máximo de ocorrências por usuário por dia'),
    ('max_occurrences_per_hour',      '3',         'integer', 'Limite máximo de ocorrências por usuário por hora'),
    ('radius_dedup_meters',           '100',       'integer', 'Raio em metros para verificação de duplicatas geográficas'),
    ('time_window_dedup_minutes',     '60',        'integer', 'Janela de tempo em minutos para verificação de duplicatas'),
    ('text_similarity_threshold',     '0.85',      'decimal', 'Limiar de similaridade textual para deduplicação (0-1)'),
    ('trust_score_initial',           '1.0',       'decimal', 'Score de confiança inicial para novos usuários'),
    ('trust_score_max',               '5.0',       'decimal', 'Score de confiança máximo'),
    ('trust_score_increment',         '0.1',       'decimal', 'Incremento no score por ocorrência validada'),
    ('trust_score_decrement',         '0.3',       'decimal', 'Decremento no score por ocorrência descartada'),
    ('photo_bonus_priority',          '1.0',       'decimal', 'Bônus no priority_score para ocorrências com foto'),
    ('severity_weight',               '3.0',       'decimal', 'Peso da severidade no cálculo do priority_score'),
    ('frequency_weight',              '2.0',       'decimal', 'Peso da frequência no cálculo do priority_score'),
    ('whatsapp_bot_enabled',          'true',      'boolean', 'Habilita integração com bot WhatsApp'),
    ('ai_validation_enabled',         'false',     'boolean', 'Habilita validação automática por IA'),
    ('s3_bucket_name',                'reporte-ai-images', 'string', 'Nome do bucket S3 para armazenamento de imagens'),
    ('s3_region',                     'sa-east-1', 'string', 'Região AWS do bucket S3'),
    ('map_default_lat',               '-20.6259',  'decimal', 'Latitude padrão do mapa (Capitólio-MG)'),
    ('map_default_lng',               '-46.0443',  'decimal', 'Longitude padrão do mapa (Capitólio-MG)'),
    ('map_default_zoom',              '13',        'integer', 'Zoom padrão do mapa')
ON CONFLICT (setting_key) DO NOTHING;
