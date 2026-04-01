# 📊 MODELO DE DADOS - REPORTE AI

## 1. VISÃO GERAL DO MODELO

O banco de dados foi estruturado com 13 tabelas principais + 3 views analíticas, garantindo:
- ✅ Integridade referencial
- ✅ Auditoria completa
- ✅ Performance otimizada
- ✅ Suporte a análises geoespaciais
- ✅ Conformidade com LGPD

---

## 2. TABELAS PRINCIPAIS

### 📋 USERS (Usuários)
```
┌─────────────────────────────────────────────┐
│               USERS                         │
├─────────────────────────────────────────────┤
│ id (UUID) [PK]                              │
│ phone_number (VARCHAR) [UK]                 │
│ name (VARCHAR)                              │
│ email (VARCHAR)                             │
│ trust_score (DECIMAL 0-5)                   │
│ total_occurrences (INT)                     │
│ occurrences_with_photo (INT)                │
│ is_active (BOOLEAN)                         │
│ anonymized (BOOLEAN)                        │
│ created_at, updated_at, last_interaction    │
└─────────────────────────────────────────────┘
```

**Índices:**
- `idx_users_phone` - Busca por telefone
- `idx_users_created_at` - Filtro por data
- `idx_users_is_active` - Filtro de usuários ativos

**Constraints:**
- trust_score entre 0 e 5
- phone_number único e obrigatório

---

### 🎫 USER_CONSENT (Consentimento)
```
┌──────────────────────────────────────────────┐
│           USER_CONSENT                       │
├──────────────────────────────────────────────┤
│ id (UUID) [PK]                               │
│ user_id (UUID) [FK → USERS]                  │
│ consent_type (VARCHAR)                       │
│   - data_processing                          │
│   - photo_storage                            │
│   - location                                 │
│   - contact                                  │
│ accepted (BOOLEAN)                           │
│ consent_date (TIMESTAMP)                     │
│ document_version (VARCHAR)                   │
│ ip_address (VARCHAR)                         │
└──────────────────────────────────────────────┘
```

**LGPD Compliance:**
- Rastreamento de consentimento por tipo
- Versão de documento para auditoria
- IP de aceitação registrado

---

### 🏷️ CATEGORIES (Categorias)
```
┌──────────────────────────────────────────────┐
│           CATEGORIES                         │
├──────────────────────────────────────────────┤
│ id (UUID) [PK]                               │
│ name (VARCHAR) [UK]                          │
│ description (TEXT)                           │
│ color (VARCHAR) - Ex: #FF6B6B                │
│ icon_url (VARCHAR)                           │
│ is_active (BOOLEAN)                          │
│ created_at, updated_at                       │
└──────────────────────────────────────────────┘

Categorias Padrão:
  • Limpeza Urbana (#FF6B6B)
  • Iluminação Pública (#FFD93D)
  • Vias e Acessos (#6BCB77)
  • Limpeza de Lotes (#4D96FF)
  • Estradas Rurais (#8B4513)
  • Manutenção Rural (#FF6B9D)
```

---

### 📌 SUB_CATEGORIES (Subcategorias)
```
┌──────────────────────────────────────────────┐
│        SUB_CATEGORIES                        │
├──────────────────────────────────────────────┤
│ id (UUID) [PK]                               │
│ category_id (UUID) [FK → CATEGORIES]         │
│ name (VARCHAR)                               │
│ description (TEXT)                           │
│ is_active (BOOLEAN)                          │
│ created_at, updated_at                       │
└──────────────────────────────────────────────┘

Exemplo:
  CATEGORIES: "Limpeza Urbana"
    ├── SUB_CATEGORIES: "Lixo em via pública"
    ├── SUB_CATEGORIES: "Entulho/Resíduo"
    └── SUB_CATEGORIES: "Bueiro entupido"
```

---

### 🎯 OCCURRENCES (Ocorrências)
**TABELA PRINCIPAL - Núcleo do sistema**

```
┌─────────────────────────────────────────────────────────┐
│                   OCCURRENCES                           │
├─────────────────────────────────────────────────────────┤
│ IDENTIFICAÇÃO                                           │
│  id (UUID) [PK]                                         │
│  protocol_id (VARCHAR) [UK] - CAP-2026-000123           │
│                                                         │
│ RELACIONAMENTOS                                         │
│  user_id (UUID) [FK → USERS]                            │
│  category_id (UUID) [FK → CATEGORIES]                   │
│  sub_category_id (UUID) [FK → SUB_CATEGORIES]           │
│                                                         │
│ INFORMAÇÕES BÁSICAS                                     │
│  description (TEXT) - Descrição completa                │
│  additional_notes (TEXT) - Observações adicionais       │
│                                                         │
│ LOCALIZAÇÃO                                             │
│  neighborhood (VARCHAR) - Ex: "Centro"                  │
│  reference_point (VARCHAR) - Ex: "Próximo à praça"      │
│  latitude (DECIMAL 10,8)                                │
│  longitude (DECIMAL 11,8)                               │
│  geom (GEOMETRY Point 4326) - PostGIS                   │
│                                                         │
│ PRIORIZAÇÃO                                             │
│  severity (INT 1-5) - Gravidade                         │
│  frequency (INT 1-5) - Frequência                       │
│  priority_score (DECIMAL) - Score calculado 0-100       │
│  recurrence_count (INT) - Vezes recorrente              │
│                                                         │
│ EVIDÊNCIA                                               │
│  has_photo (BOOLEAN)                                    │
│  photo_count (INT)                                      │
│                                                         │
│ STATUS                                                  │
│  status (VARCHAR)                                       │
│    - received (Recebida)                                │
│    - validating (Em validação)                          │
│    - validated (Validada)                               │
│    - discarded (Descartada)                             │
│    - resolved (Resolvida)                               │
│  confidence_level (DECIMAL 0-1)                         │
│                                                         │
│ DEDUPLICAÇÃO                                            │
│  is_duplicate (BOOLEAN)                                 │
│  duplicate_main_occurrence_id (UUID) [FK]               │
│                                                         │
│ TIMESTAMPS                                              │
│  created_at, updated_at, resolved_at                    │
└─────────────────────────────────────────────────────────┘
```

**Índices de Performance:**
```
idx_occurrences_user_id              - Busca por usuário
idx_occurrences_category_id          - Filtro por categoria
idx_occurrences_status               - Filtro por status
idx_occurrences_created_at           - Ordenação por data
idx_occurrences_priority_score DESC  - Top problemas
idx_occurrences_protocol_id          - Busca por protocolo
idx_occurrences_neighborhood         - Filtro por bairro
idx_occurrences_geom USING GIST      - Queries espaciais
idx_occurrences_is_duplicate         - Filtro duplicatas
```

---

### 📸 OCCURRENCE_IMAGES (Imagens/Evidências)
```
┌──────────────────────────────────────────────┐
│       OCCURRENCE_IMAGES                      │
├──────────────────────────────────────────────┤
│ id (UUID) [PK]                               │
│ occurrence_id (UUID) [FK → OCCURRENCES]      │
│ s3_url (VARCHAR) - URL completa S3           │
│ s3_key (VARCHAR) [UK] - Chave S3             │
│ image_size (INT) - Tamanho em bytes          │
│ image_format (VARCHAR)                       │
│   - jpg, jpeg, png, webp                     │
│ uploaded_at (TIMESTAMP)                      │
│ processed (BOOLEAN) - Processada por IA?     │
└──────────────────────────────────────────────┘

Política S3:
  • Pasta: /occurrences/{year}/{month}/{occurrence_id}/
  • Política de retenção: 2 anos
  • Encriptação: AES-256
  • Acesso público: NO
```

---

### 🔍 DEDUPLICATION_RECORDS (Deduplicação)
```
┌─────────────────────────────────────────────────┐
│      DEDUPLICATION_RECORDS                      │
├─────────────────────────────────────────────────┤
│ id (UUID) [PK]                                  │
│ main_occurrence_id (UUID) [FK → OCCURRENCES]    │
│ duplicate_occurrence_id (UUID) [FK → OCCURRENCES]
│                                                 │
│ CRITÉRIOS DE DEDUP                              │
│ similarity_score (DECIMAL 0-100)                │
│   - Score de similaridade textual               │
│ geographic_distance_meters (DECIMAL)            │
│   - Distância em metros                         │
│ time_difference_minutes (INT)                   │
│   - Diferença temporal                          │
│                                                 │
│ CONTEXTO                                        │
│ dedup_reason (VARCHAR)                          │
│ dedup_method (VARCHAR)                          │
│   - text_similarity                             │
│   - geographic_proximity                        │
│   - temporal_proximity                          │
│   - combined                                    │
│ created_at (TIMESTAMP)                          │
└─────────────────────────────────────────────────┘

Exemplo:
  main_occurrence_id: 12345 (Lixo na Rua A)
  duplicate_occurrence_id: 12346 (Lixo na Rua A)
  similarity_score: 95%
  geographic_distance: 15 metros
  dedup_method: combined
```

---

### ✅ VALIDATIONS (Validação)
```
┌─────────────────────────────────────────────┐
│          VALIDATIONS                        │
├─────────────────────────────────────────────┤
│ id (UUID) [PK]                              │
│ occurrence_id (UUID) [FK → OCCURRENCES]     │
│ validator_user_id (UUID) [FK → USERS]       │
│                                             │
│ TIPO DE VALIDAÇÃO                           │
│ validation_type (VARCHAR)                   │
│   - manual (Humano)                         │
│   - automatic (Sistema)                     │
│   - community (Comunidade)                  │
│                                             │
│ RESULTADO                                   │
│ result (VARCHAR)                            │
│   - validated (✅ Válida)                    │
│   - rejected (❌ Rejeitada)                  │
│   - suspicious (⚠️ Suspeita)                 │
│   - pending (⏳ Pendente)                    │
│ reason (TEXT)                               │
│ confidence (DECIMAL 0-1)                    │
│ multiple_reports_count (INT)                │
│ validated_at (TIMESTAMP)                    │
└─────────────────────────────────────────────┘
```

---

### 📝 OCCURRENCE_HISTORY (Auditoria)
```
┌─────────────────────────────────────────────┐
│      OCCURRENCE_HISTORY                     │
├─────────────────────────────────────────────┤
│ id (UUID) [PK]                              │
│ occurrence_id (UUID) [FK → OCCURRENCES]     │
│ changed_by_user_id (UUID) [FK → USERS]      │
│                                             │
│ AÇÃO                                        │
│ action (VARCHAR)                            │
│   - created (Criada)                        │
│   - updated (Atualizada)                    │
│   - validated (Validada)                    │
│   - duplicated (Marcada duplicata)          │
│   - resolved (Resolvida)                    │
│   - status_changed (Status alterado)        │
│                                             │
│ MUDANÇAS                                    │
│ old_status / new_status                     │
│ old_priority_score / new_priority_score     │
│ change_reason (TEXT)                        │
│ created_at (TIMESTAMP)                      │
└─────────────────────────────────────────────┘

Propósito: Auditoria completa de mudanças
```

---

### 📊 INDICATORS (Indicadores)
```
┌─────────────────────────────────────────────┐
│          INDICATORS                         │
├─────────────────────────────────────────────┤
│ id (UUID) [PK]                              │
│ indicator_name (VARCHAR)                    │
│ indicator_type (VARCHAR)                    │
│   - operational (Operacional)               │
│   - analytical (Analítico)                  │
│   - quality (Qualidade)                     │
│   - impact (Impacto)                        │
│                                             │
│ value (DECIMAL)                             │
│ unit (VARCHAR) - Ex: "quantidade", "%"      │
│ category_id (UUID) [FK → CATEGORIES]        │
│ neighborhood (VARCHAR)                      │
│                                             │
│ period_start, period_end (DATE)             │
│ calculated_at (TIMESTAMP)                   │
└─────────────────────────────────────────────┘

Exemplos de Indicadores:
  • Total de ocorrências por mês
  • % com fotografia
  • Taxa de deduplicação
  • Score médio por categoria
  • Densidade por região
```

---

### 📄 REPORTS (Relatórios)
```
┌──────────────────────────────────────────────┐
│            REPORTS                           │
├──────────────────────────────────────────────┤
│ id (UUID) [PK]                               │
│ title (VARCHAR)                              │
│ report_type (VARCHAR)                        │
│   - monthly (Mensal)                         │
│   - executive (Executivo)                    │
│   - regional (Regional)                      │
│   - categorical (Por categoria)              │
│   - custom (Customizado)                     │
│                                              │
│ file_path (VARCHAR) - Caminho do arquivo    │
│ file_size (INT)                              │
│ period_start, period_end (DATE)              │
│ generated_by_user_id (UUID) [FK → USERS]     │
│ generated_at (TIMESTAMP)                     │
│ status (VARCHAR)                             │
│   - pending, processing, completed, failed   │
└──────────────────────────────────────────────┘
```

---

### 🗺️ SPATIAL_CLUSTERS (Clusters Espaciais)
```
┌───────────────────────────────────────────────────┐
│          SPATIAL_CLUSTERS                         │
├───────────────────────────────────────────────────┤
│ id (UUID) [PK]                                    │
│ cluster_name (VARCHAR) - Ex: "Cluster Centro"     │
│ neighborhood (VARCHAR)                            │
│                                                   │
│ CENTER (Ponto central do cluster)                 │
│ center_latitude (DECIMAL 10,8)                    │
│ center_longitude (DECIMAL 11,8)                   │
│ center_geom (GEOMETRY Point 4326)                 │
│ radius_meters (DECIMAL)                           │
│                                                   │
│ ANÁLISE                                           │
│ occurrence_count (INT) - Qtd ocorrências          │
│ density_score (DECIMAL) - Score de densidade      │
│ severity_avg (DECIMAL) - Gravidade média          │
│ priority_score_avg (DECIMAL) - Prioridade média   │
│                                                   │
│ created_at, updated_at                            │
└───────────────────────────────────────────────────┘

Uso: Mapa de calor, identificação de hotspots
```

---

### ⛔ USER_RATE_LIMIT (Controle de Spam)
```
┌─────────────────────────────────────────┐
│       USER_RATE_LIMIT                   │
├─────────────────────────────────────────┤
│ id (UUID) [PK]                          │
│ user_id (UUID) [UK] [FK → USERS]        │
│                                         │
│ daily_limit (INT) - Default: 10         │
│ hourly_limit (INT) - Default: 3         │
│ occurrences_today (INT)                 │
│ occurrences_this_hour (INT)             │
│                                         │
│ last_reset_date (DATE)                  │
│ last_reset_hour (INT)                   │
│                                         │
│ is_blocked (BOOLEAN)                    │
│ blocked_until (TIMESTAMP)                │
│ updated_at (TIMESTAMP)                  │
└─────────────────────────────────────────┘

Estratégia: Sliding window
```

---

### 📧 NOTIFICATIONS (Notificações)
```
┌──────────────────────────────────────────┐
│          NOTIFICATIONS                   │
├──────────────────────────────────────────┤
│ id (UUID) [PK]                           │
│ user_id (UUID) [FK → USERS]              │
│ occurrence_id (UUID) [FK → OCCURRENCES]  │
│                                          │
│ notification_type (VARCHAR)              │
│   - protocol_generated (Protocolo)       │
│   - status_updated (Status atualizado)   │
│   - duplicate_detected (Duplicata)       │
│   - resolved (Resolvida)                 │
│   - feedback (Feedback)                  │
│                                          │
│ title (VARCHAR)                          │
│ message (TEXT)                           │
│ sent_at (TIMESTAMP)                      │
│ read_at (TIMESTAMP)                      │
│ is_read (BOOLEAN)                        │
└──────────────────────────────────────────┘
```

---

### ⚙️ SYSTEM_SETTINGS (Configurações)
```
┌──────────────────────────────────────────┐
│        SYSTEM_SETTINGS                   │
├──────────────────────────────────────────┤
│ id (UUID) [PK]                           │
│ setting_key (VARCHAR) [UK]               │
│ setting_value (TEXT)                     │
│ setting_type (VARCHAR)                   │
│ description (TEXT)                       │
│ updated_at (TIMESTAMP)                   │
└──────────────────────────────────────────┘

Exemplos:
  • max_daily_uploads_per_user: 10
  • max_image_size_mb: 5
  • dedup_similarity_threshold: 0.85
  • api_rate_limit: 100/hour
```

---

## 3. VIEWS ANALÍTICAS (Para Dashboard)

### 📊 v_occurrences_by_category
```sql
SELECT 
  category_name,
  total_occurrences,
  validated_count,
  with_photo_count,
  avg_priority_score,
  avg_severity
FROM v_occurrences_by_category
ORDER BY total_occurrences DESC;
```
**Uso:** Gráfico de pizza por categoria, ranking

---

### 🗺️ v_occurrences_by_neighborhood
```sql
SELECT 
  neighborhood,
  total_occurrences,
  validated_count,
  avg_priority_score
FROM v_occurrences_by_neighborhood
ORDER BY total_occurrences DESC;
```
**Uso:** Mapa de bairros, análise regional

---

### 👤 v_user_statistics
```sql
SELECT 
  phone_number,
  total_occurrences,
  validated_occurrences,
  occurrences_with_photo,
  trust_score
FROM v_user_statistics
WHERE is_active = TRUE;
```
**Uso:** Ranking de usuários, análise de participação

---

### 📈 v_daily_statistics
```sql
SELECT 
  occurrence_date,
  total_occurrences,
  unique_users,
  with_photo,
  validated
FROM v_daily_statistics
ORDER BY occurrence_date DESC;
```
**Uso:** Gráfico de tendência temporal

---

## 4. FÓRMULA DO PRIORITY SCORE

```
Score = (gravidade × 3) + (frequência × 2) + densidade_região + reincidência + bonus_foto

Componentes:
  • gravidade (1-5): Seriedad do problema
  • frequência (1-5): Recorrência do problema  
  • densidade_região: Quantidade de ocorrências no bairro
  • reincidência: Vezes que o mesmo problema aparece
  • bonus_foto: +1 ponto se houver foto/evidência

Resultado: Score de 0 a ~100

Classificação:
  🟢 0-20:   Baixa Prioridade
  🟡 20-50:  Média Prioridade
  🟠 50-80:  Alta Prioridade
  🔴 80-100: Crítica
```

---

## 5. RELACIONAMENTOS

```
USERS (1) ──→ (N) OCCURRENCES
USERS (1) ──→ (N) USER_CONSENT
USERS (1) ──→ (N) VALIDATIONS
USERS (1) ──→ (N) NOTIFICATIONS
USERS (1) ──→ (N) USER_RATE_LIMIT

CATEGORIES (1) ──→ (N) SUB_CATEGORIES
CATEGORIES (1) ──→ (N) OCCURRENCES

SUB_CATEGORIES (1) ──→ (N) OCCURRENCES

OCCURRENCES (1) ──→ (N) OCCURRENCE_IMAGES
OCCURRENCES (1) ──→ (N) VALIDATIONS
OCCURRENCES (1) ──→ (N) OCCURRENCE_HISTORY
OCCURRENCES (1) ──→ (N) DEDUPLICATION_RECORDS
OCCURRENCES (1) ──→ (N) NOTIFICATIONS

DEDUPLICATION_RECORDS (N:N) ──→ OCCURRENCES
  - main_occurrence_id
  - duplicate_occurrence_id

SPATIAL_CLUSTERS (1) ──→ (N) OCCURRENCES (implícito via geom)
```

---

## 6. PARTICIONAMENTO (Evolução Futura)

Para melhorar performance com grandes volumes:

```sql
-- Particionar OCCURRENCES por data
ALTER TABLE occurrences PARTITION BY RANGE (YEAR(created_at));

-- Particionar por bairro
CREATE INDEX idx_occurrences_neighborhood_partitioned
ON occurrences(neighborhood, created_at DESC);
```

---

## 7. CONSTRAINTS E VALIDAÇÕES

```
✅ Primary Keys - Todas as tabelas têm PK UUID
✅ Foreign Keys - Relacionamentos mantêm integridade
✅ Unique Constraints - protocol_id, phone_number, etc
✅ Check Constraints - Ranges numéricos validados
✅ Not Null - Campos obrigatórios protegidos
✅ Timestamps - Auditoria com created_at, updated_at
✅ Soft Deletes - Ocorrências marcadas como descartadas
✅ Geoespacial - PostGIS para validação de coordenadas
```

---

## 8. ÍNDICES DE OTIMIZAÇÃO

```
ÍNDICES CRÍTICOS (MUST-HAVE):
  idx_occurrences_user_id
  idx_occurrences_category_id
  idx_occurrences_status
  idx_occurrences_priority_score DESC
  idx_occurrences_created_at DESC
  idx_users_phone_number

ÍNDICES DE DASHBOARD:
  idx_occurrences_neighborhood
  idx_dedup_method
  idx_validations_result

ÍNDICES GEOESPACIAIS:
  idx_occurrences_geom USING GIST
  idx_spatial_clusters_geom USING GIST

ÍNDICES TEMPORAIS:
  idx_occurrence_images_uploaded_at
  idx_notifications_sent_at
  idx_reports_generated_at
```

---

## 9. TRATAMENTO DE DADOS PESSOAIS (LGPD)

```
COLETA MÍNIMA:
  ✅ phone_number (ID)
  ✅ location (necessário)
  ✅ timestamp (auditoria)
  ❌ name (opcional, pode ser anônimo)
  ❌ email (opcional)
  ❌ CPF (nunca)

ANONIMIZAÇÃO:
  • Trigger automático após 2 anos
  • Função: anonymize_user() 
  • Campos anonymized:
    - name → NULL
    - email → NULL
    - phone_number → hash
    - ip_address → NULL

CONSENTIMENTO:
  • Solicitado na primeira interação
  • Versão controlada
  • Aceito/Rejeitado registrado
  • Pode revogar a qualquer momento
```

---

