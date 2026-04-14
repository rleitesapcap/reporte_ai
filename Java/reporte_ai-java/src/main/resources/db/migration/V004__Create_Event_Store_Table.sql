-- Migration V004: Criar tabela para Event Sourcing
-- Data: 2026-04-14
-- Event Sourcing Pattern - Armazena todos os eventos de domínio

CREATE TABLE IF NOT EXISTS event_store (
    event_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    event_data TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_event_store_aggregate_id ON event_store(aggregate_id);
CREATE INDEX IF NOT EXISTS idx_event_store_event_type ON event_store(event_type);
CREATE INDEX IF NOT EXISTS idx_event_store_created_at ON event_store(created_at);

-- Comentários
COMMENT ON TABLE event_store IS 'Armazena todos os eventos de domínio de forma imutável para Event Sourcing';
COMMENT ON COLUMN event_store.event_id IS 'ID único do evento';
COMMENT ON COLUMN event_store.aggregate_id IS 'ID do agregado (ex: user_id) que gerou o evento';
COMMENT ON COLUMN event_store.event_type IS 'Tipo do evento (ex: UserRegisteredEvent)';
COMMENT ON COLUMN event_store.event_data IS 'Dados completos do evento em formato JSON';
COMMENT ON COLUMN event_store.created_at IS 'Timestamp de criação do evento (imutável)';
