# Documentação Detalhada do Schema - Reporte AI

## Visão Geral

O banco de dados do Reporte AI é uma plataforma inteligente de mapeamento de problemas urbanos e rurais para Capitólio-MG. O schema utiliza PostgreSQL com extensões PostGIS para análise geoespacial.

---

## 📋 ÍNDICE DAS ENTIDADES

1. [Tabelas Base - Usuários](#tabelas-base)
2. [Categorização](#categorização)
3. [Ocorrências (Principal)](#ocorrências)
4. [Evidências e Imagens](#evidências)
5. [Deduplicação](#deduplicação)
6. [Validação](#validação)
7. [Rastreamento](#rastreamento)
8. [Indicadores e Relatórios](#indicadores)
9. [Análise Espacial](#análise-espacial)
10. [Controle e Notificações](#controle)
11. [Configurações](#configurações)

---

## <a id="tabelas-base"></a>1. TABELAS BASE - USUÁRIOS E CONSENTIMENTO

### 📱 **Tabela: `users`**

Armazena informações dos usuários que reportam problemas na plataforma.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único do usuário (chave primária) |
| `phone_number` | VARCHAR(20) | **Único** - Número de telefone do usuário (campo de identificação principal) |
| `name` | VARCHAR(255) | Nome completo do usuário |
| `email` | VARCHAR(255) | E-mail do usuário |
| `created_at` | TIMESTAMP | Data/hora de criação da conta |
| `updated_at` | TIMESTAMP | Data/hora da última atualização |
| `last_interaction` | TIMESTAMP | Data/hora da última interação com o sistema |
| `trust_score` | DECIMAL(4,2) | **Score de confiabilidade** (0.0 a 5.0). Indica a qualidade dos reports do usuário |
| `total_occurrences` | INT | Contagem total de ocorrências reportadas pelo usuário |
| `occurrences_with_photo` | INT | Contagem de ocorrências com evidência fotográfica |
| `is_active` | BOOLEAN | Indica se o usuário está ativo na plataforma |
| `anonymized` | BOOLEAN | Flag LGPD - Indica se os dados do usuário foram anonimizados |
| `anonymized_at` | TIMESTAMP | Data/hora da anonimização (se aplicável) |

**Propósito:** Gerenciar identidade e histórico de confiabilidade dos usuários reportadores.

**Índices:** `phone_number`, `created_at`, `is_active`

---

### 📋 **Tabela: `user_consent`**

Armazena o consentimento do usuário para diferentes tipos de processamento de dados (LGPD).

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único do registro de consentimento |
| `user_id` | UUID | **Foreign Key** → `users.id` |
| `consent_type` | VARCHAR(100) | Tipo de consentimento: `data_processing`, `photo_storage`, `location`, `contact` |
| `accepted` | BOOLEAN | Se o consentimento foi aceito (TRUE) ou rejeitado (FALSE) |
| `consent_date` | TIMESTAMP | Data/hora da decisão de consentimento |
| `document_version` | VARCHAR(50) | Versão do documento de consentimento aceito |
| `ip_address` | VARCHAR(50) | IP do dispositivo na aceitação (rastreamento) |

**Propósito:** Garantir conformidade LGPD rastreando quais dados o usuário autoriza processar.

**Índices:** `user_id`, `consent_type`

---

## <a id="categorização"></a>2. CATEGORIZAÇÃO

### 🏷️ **Tabela: `categories`**

Categorias principais de problemas urbanos/rurais.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único da categoria |
| `name` | VARCHAR(255) | **Único** - Nome da categoria (ex: "Limpeza Urbana", "Iluminação Pública") |
| `description` | TEXT | Descrição detalhada do tipo de problema |
| `color` | VARCHAR(7) | Cor em formato hexadecimal para visualização em mapas/UI |
| `icon_url` | VARCHAR(500) | URL do ícone representativo |
| `is_active` | BOOLEAN | Se a categoria está disponível para novos reports |
| `created_at` | TIMESTAMP | Data de criação |
| `updated_at` | TIMESTAMP | Data da última atualização |

**Propósito:** Classificar os problemas em tipos principais.

**Seed Data (Exemplos):**
- Limpeza Urbana
- Iluminação Pública
- Vias e Acessos
- Limpeza de Lotes
- Estradas Rurais
- Manutenção Rural

**Índices:** `is_active`

---

### 🔖 **Tabela: `sub_categories`**

Subcategorias para refinamento dos tipos de problema.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único da subcategoria |
| `category_id` | UUID | **Foreign Key** → `categories.id` |
| `name` | VARCHAR(255) | Nome da subcategoria (ex: "Buraco em rua", "Lixo acumulado") |
| `description` | TEXT | Descrição específica |
| `is_active` | BOOLEAN | Disponibilidade para uso |
| `created_at` | TIMESTAMP | Data de criação |
| `updated_at` | TIMESTAMP | Data de última atualização |

**Propósito:** Permitir classificação mais precisa dos problemas.

**Restrições:** Combinação (category_id, name) deve ser única.

**Índices:** `category_id`, `is_active`

---

## <a id="ocorrências"></a>3. OCORRÊNCIAS (TABELA PRINCIPAL)

### 🚨 **Tabela: `occurrences`**

A tabela mais crítica do sistema. Armazena cada report de problema feito por um usuário.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único da ocorrência |
| `user_id` | UUID | **Foreign Key** → `users.id` (quem reportou) |
| `category_id` | UUID | **Foreign Key** → `categories.id` (classificação principal) |
| `sub_category_id` | UUID | **Foreign Key** → `sub_categories.id` (classificação refinada) |
| `protocol_id` | VARCHAR(50) | **Único** - ID gerado automaticamente (ex: "CAP-2024-000001") |
| `description` | TEXT | Descrição detalhada do problema |
| `additional_notes` | TEXT | Notas adicionais do usuário |
| `neighborhood` | VARCHAR(255) | Bairro/região onde o problema está localizado |
| `reference_point` | VARCHAR(500) | Ponto de referência (próximo a quê?) |
| `latitude` | DECIMAL(10,8) | Coordenada de latitude |
| `longitude` | DECIMAL(11,8) | Coordenada de longitude |
| `geom` | GEOMETRY(Point, 4326) | **PostGIS** - Geometria geoespacial para queries de proximidade |
| `severity` | INT | **Escala 1-5** - Gravidade do problema (5 = crítico) |
| `frequency` | INT | **Escala 1-5** - Frequência de ocorrência (5 = muito frequente) |
| `priority_score` | DECIMAL(8,2) | **Score calculado** - Priorização automática baseada em severity, frequency, densidade, etc |
| `recurrence_count` | INT | Número de vezes que problemas similares foram reportados |
| `has_photo` | BOOLEAN | Se há evidência fotográfica anexada |
| `photo_count` | INT | Número de fotos anexadas |
| `status` | VARCHAR(50) | Estado atual: `received`, `validating`, `validated`, `discarded`, `resolved` |
| `confidence_level` | DECIMAL(4,2) | Nível de confiança (0.0 a 1.0) - Usa IA para validação automática |
| `created_at` | TIMESTAMP | Data/hora do report |
| `updated_at` | TIMESTAMP | Última atualização |
| `resolved_at` | TIMESTAMP | Data quando o problema foi resolvido |
| `is_duplicate` | BOOLEAN | Se este report é duplicado |
| `duplicate_main_occurrence_id` | UUID | **Foreign Key** → `occurrences.id` (referencia ao report original se for duplicado) |

**Estados Possíveis:**
- `received` - Recém-chegado, aguardando validação
- `validating` - Em processo de validação
- `validated` - Validado e confirmado
- `discarded` - Descartado (spam, não pertinente, etc)
- `resolved` - Problema já foi solucionado

**Propósito:** Registrar cada problema reportado com todas as informações necessárias para análise e ação.

**Índices Otimizados:**
- `user_id`, `category_id`, `sub_category_id` - Filtros comuns
- `status`, `created_at` - Queries temporais
- `priority_score DESC` - Ranking de prioridade
- `neighborhood`, `geom` (GIST) - Análise espacial
- `protocol_id` - Busca rápida por ID

---

## <a id="evidências"></a>4. EVIDÊNCIAS E IMAGENS

### 📸 **Tabela: `occurrence_images`**

Armazena metadados das imagens que comprovam os problemas reportados.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único da imagem |
| `occurrence_id` | UUID | **Foreign Key** → `occurrences.id` |
| `s3_url` | VARCHAR(500) | URL pública da imagem no S3 (Amazon) |
| `s3_key` | VARCHAR(500) | **Único** - Chave/caminho da imagem no S3 |
| `image_size` | INT | Tamanho do arquivo em bytes |
| `image_format` | VARCHAR(10) | Formato: `jpg`, `jpeg`, `png`, `webp` |
| `uploaded_at` | TIMESTAMP | Data/hora do upload |
| `processed` | BOOLEAN | Se a imagem foi processada por IA (detecção de objetos, análise, etc) |

**Propósito:** Gerenciar evidências fotográficas dos problemas.

**Índices:** `occurrence_id`, `uploaded_at`

---

## <a id="deduplicação"></a>5. DEDUPLICAÇÃO

### 🔄 **Tabela: `deduplication_records`**

Registra quando dois ou mais reports são identificados como referindo-se ao mesmo problema.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único do registro de dedup |
| `main_occurrence_id` | UUID | **Foreign Key** → `occurrences.id` (o report "principal") |
| `duplicate_occurrence_id` | UUID | **Foreign Key** → `occurrences.id` (report identificado como duplicado) |
| `similarity_score` | DECIMAL(5,2) | **0-100** - Percentual de similaridade detectada |
| `geographic_distance_meters` | DECIMAL(10,2) | Distância entre os dois pontos (se aplicável) |
| `time_difference_minutes` | INT | Diferença de tempo entre os reports |
| `dedup_reason` | VARCHAR(255) | Descrição da razão (ex: "Mesmo local, mesmo horário") |
| `dedup_method` | VARCHAR(50) | Método usado: `text_similarity`, `geographic_proximity`, `temporal_proximity`, `combined` |
| `created_at` | TIMESTAMP | Data da detecção |

**Propósito:** Consolidar múltiplos reports do mesmo problema para evitar duplicação de esforços.

**Índices:** `main_occurrence_id`, `duplicate_occurrence_id`, `dedup_method`

---

## <a id="validação"></a>6. VALIDAÇÃO

### ✅ **Tabela: `validations`**

Registra o resultado da validação de um report.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `occurrence_id` | UUID | **Foreign Key** → `occurrences.id` |
| `validator_user_id` | UUID | **Foreign Key** → `users.id` (quem validou, pode ser IA ou usuário) |
| `validation_type` | VARCHAR(50) | Tipo: `manual` (validador humano), `automatic` (IA), `community` (votação) |
| `result` | VARCHAR(50) | Resultado: `validated`, `rejected`, `suspicious`, `pending` |
| `reason` | TEXT | Explicação da decisão |
| `confidence` | DECIMAL(4,2) | Nível de confiança da validação (0.0 a 1.0) |
| `multiple_reports_count` | INT | Quantas pessoas também reportaram o mesmo problema |
| `validated_at` | TIMESTAMP | Data/hora da validação |

**Propósito:** Rastrear e validar a qualidade dos reports.

**Índices:** `occurrence_id`, `validator_user_id`, `result`, `validated_at`

---

## <a id="rastreamento"></a>7. RASTREAMENTO (AUDIT TRAIL)

### 📝 **Tabela: `occurrence_history`**

Histórico de todas as mudanças em uma ocorrência (auditoria).

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único do registro histórico |
| `occurrence_id` | UUID | **Foreign Key** → `occurrences.id` |
| `changed_by_user_id` | UUID | **Foreign Key** → `users.id` (quem fez a mudança) |
| `action` | VARCHAR(50) | Tipo de ação: `created`, `updated`, `validated`, `duplicated`, `resolved`, `status_changed` |
| `old_status` | VARCHAR(50) | Status anterior |
| `new_status` | VARCHAR(50) | Novo status |
| `old_priority_score` | DECIMAL(8,2) | Priority score anterior |
| `new_priority_score` | DECIMAL(8,2) | Novo priority score |
| `change_reason` | TEXT | Razão da mudança |
| `created_at` | TIMESTAMP | Data/hora da mudança |

**Propósito:** Manter histórico completo de alterações para auditoria e análise.

**Exemplo:** Um admin marcou um report como "duplicado" com motivo "Mesmo problema na Rua X"

**Índices:** `occurrence_id`, `created_at`, `action`

---

## <a id="indicadores"></a>8. INDICADORES E RELATÓRIOS

### 📊 **Tabela: `indicators`**

Métricas calculadas para análise de tendências.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `indicator_name` | VARCHAR(255) | Nome do indicador (ex: "Taxa de Resolução", "Problemas por Bairro") |
| `indicator_type` | VARCHAR(50) | Tipo: `operational`, `analytical`, `quality`, `impact` |
| `description` | TEXT | Descrição detalhada |
| `value` | DECIMAL(15,2) | Valor numérico calculado |
| `unit` | VARCHAR(50) | Unidade (%, número, horas, etc) |
| `category_id` | UUID | **Foreign Key** → `categories.id` (opcional, para indicadores por categoria) |
| `neighborhood` | VARCHAR(255) | Bairro (opcional, para indicadores regionais) |
| `period_start` | DATE | Início do período de análise |
| `period_end` | DATE | Fim do período de análise |
| `calculated_at` | TIMESTAMP | Data do cálculo |

**Tipos:**
- `operational` - Métricas de operação (velocidade de resposta, etc)
- `analytical` - Dados analíticos (distribuição, tendências)
- `quality` - Qualidade dos reports (% validados, etc)
- `impact` - Impacto (problemas resolvidos, economia de tempo, etc)

**Propósito:** Armazenar cálculos agregados para dashboards e relatórios.

**Índices:** `indicator_type`, `category_id`, `period_start`, `period_end`, `calculated_at`

---

### 📋 **Tabela: `reports`**

Relatórios gerados automaticamente ou sob demanda.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `title` | VARCHAR(500) | Título do relatório |
| `report_type` | VARCHAR(50) | Tipo: `monthly`, `executive`, `regional`, `categorical`, `custom` |
| `description` | TEXT | Descrição/resumo |
| `file_path` | VARCHAR(500) | Caminho do arquivo gerado (PDF, Excel, etc) |
| `file_size` | INT | Tamanho em bytes |
| `period_start` | DATE | Período coberto (início) |
| `period_end` | DATE | Período coberto (fim) |
| `generated_by_user_id` | UUID | **Foreign Key** → `users.id` (quem solicitou) |
| `generated_at` | TIMESTAMP | Data/hora da geração |
| `status` | VARCHAR(50) | Estado: `pending`, `processing`, `completed`, `failed` |

**Propósito:** Gerenciar geração e armazenamento de relatórios.

**Índices:** `report_type`, `generated_at`, `period_start`, `period_end`, `status`

---

## <a id="análise-espacial"></a>9. ANÁLISE ESPACIAL (CLUSTERING)

### 🗺️ **Tabela: `spatial_clusters`**

Agrupa ocorrências geograficamente próximas para identificar "pontos quentes".

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único do cluster |
| `cluster_name` | VARCHAR(255) | Nome do cluster (ex: "Zona Crítica Downtown") |
| `neighborhood` | VARCHAR(255) | Bairro predominante |
| `center_latitude` | DECIMAL(10,8) | Latitude do centroide do cluster |
| `center_longitude` | DECIMAL(11,8) | Longitude do centroide |
| `center_geom` | GEOMETRY(Point, 4326) | Geometria PostGIS do centroide |
| `radius_meters` | DECIMAL(10,2) | Raio de abrangência do cluster em metros |
| `occurrence_count` | INT | Número de ocorrências no cluster |
| `density_score` | DECIMAL(8,2) | Score de densidade (ocorrências por área) |
| `severity_avg` | DECIMAL(5,2) | Severidade média dos problemas |
| `priority_score_avg` | DECIMAL(8,2) | Priority score médio |
| `created_at` | TIMESTAMP | Data de criação do cluster |
| `updated_at` | TIMESTAMP | Data da última atualização |

**Propósito:** Identificar "hot spots" - áreas com alta concentração de problemas que requerem atenção prioritária.

**Índices:** `neighborhood`, `center_geom` (GIST), `density_score DESC`

---

## <a id="controle"></a>10. CONTROLE E NOTIFICAÇÕES

### ⏱️ **Tabela: `user_rate_limit`**

Controla spam e abuso limitando quantidade de reports por usuário.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `user_id` | UUID | **Foreign Key Única** → `users.id` |
| `daily_limit` | INT | Limite máximo de reports por dia (padrão: 10) |
| `hourly_limit` | INT | Limite máximo por hora (padrão: 3) |
| `occurrences_today` | INT | Reports já feitos hoje |
| `occurrences_this_hour` | INT | Reports na última hora |
| `last_reset_date` | DATE | Última data de reset diário |
| `last_reset_hour` | INT | Última hora de reset |
| `is_blocked` | BOOLEAN | Se o usuário está bloqueado temporariamente |
| `blocked_until` | TIMESTAMP | Data/hora de liberação (se bloqueado) |
| `updated_at` | TIMESTAMP | Última atualização |

**Propósito:** Proteger contra abuso e spam.

**Índices:** `user_id`, `is_blocked`

---

### 🔔 **Tabela: `notifications`**

Notificações enviadas aos usuários sobre eventos relevantes.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `user_id` | UUID | **Foreign Key** → `users.id` (quem recebe) |
| `occurrence_id` | UUID | **Foreign Key** → `occurrences.id` (sobre qual ocorrência) |
| `notification_type` | VARCHAR(50) | Tipo: `protocol_generated`, `status_updated`, `duplicate_detected`, `resolved`, `feedback` |
| `title` | VARCHAR(255) | Título da notificação |
| `message` | TEXT | Mensagem da notificação |
| `sent_at` | TIMESTAMP | Data/hora de envio |
| `read_at` | TIMESTAMP | Data/hora de leitura (NULL se não lida) |
| `is_read` | BOOLEAN | Se a notificação foi lida |

**Exemplos:**
- `protocol_generated`: "Seu report foi recebido! Protocolo: CAP-2024-000123"
- `status_updated`: "Seu report foi validado e está na fila de ação"
- `duplicate_detected`: "Detectamos que este é um problema similar ao ID XYZ"
- `resolved`: "O problema que você reportou foi resolvido!"

**Propósito:** Comunicação com usuários sobre progresso de seus reports.

**Índices:** `user_id`, `is_read`, `sent_at`

---

## <a id="configurações"></a>11. CONFIGURAÇÕES

### ⚙️ **Tabela: `system_settings`**

Configurações globais do sistema.

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `id` | UUID | Identificador único |
| `setting_key` | VARCHAR(255) | **Única** - Chave da configuração (ex: "MAX_DAILY_REPORTS", "MIN_CONFIDENCE_THRESHOLD") |
| `setting_value` | TEXT | Valor da configuração |
| `setting_type` | VARCHAR(50) | Tipo: `string`, `integer`, `decimal`, `boolean` |
| `description` | TEXT | Descrição da configuração |
| `updated_at` | TIMESTAMP | Data da última alteração |

**Exemplos:**
- `MAX_DAILY_REPORTS` = 10
- `MIN_CONFIDENCE_THRESHOLD` = 0.7
- `ENABLE_AUTOMATIC_VALIDATION` = true
- `ADMIN_EMAIL` = admin@capitolio.mg.gov.br

**Propósito:** Centralizar configurações do sistema sem necessidade de redeploy.

**Índices:** `setting_key`

---

## 📊 VIEWS ANALÍTICAS

Estas são views pré-construídas para facilitar dashboards e relatórios:

### 🔍 **View: `v_occurrences_by_category`**

Agrupa ocorrências por categoria.

```
category_id, category_name, total_occurrences, validated_count, 
with_photo_count, avg_priority_score, avg_severity
```

---

### 🔍 **View: `v_occurrences_by_neighborhood`**

Agrupa ocorrências por bairro.

```
neighborhood, total_occurrences, validated_count, with_photo_count, 
avg_priority_score, last_occurrence_date
```

---

### 🔍 **View: `v_user_statistics`**

Estatísticas por usuário.

```
user_id, phone_number, name, trust_score, total_occurrences, 
occurrences_with_photo, validated_occurrences, created_at, last_interaction
```

---

### 🔍 **View: `v_daily_statistics`**

Estatísticas diárias agregadas.

```
occurrence_date, total_occurrences, unique_users, with_photo, 
validated, avg_priority_score
```

---

## 🔧 FUNÇÕES (Functions)

### `generate_protocol_id()`
Gera um ID de protocolo único no formato: **CAP-YYYY-XXXXXX**
- Exemplo: `CAP-2024-000001`
- Usa uma sequence para garantir unicidade

### `calculate_priority_score(severity, frequency, density, recurrence, has_photo)`
Calcula o score de prioridade automaticamente:
```
score = (severity × 3.0) + (frequency × 2.0) + density + recurrence + (has_photo ? 1.0 : 0)
```

---

## ⚡ TRIGGERS (Automações)

### `occurrence_update_timestamp`
Atualiza automaticamente `updated_at` sempre que uma ocorrência é modificada.

### `update_user_stats`
Após inserir uma nova ocorrência:
- Incrementa `total_occurrences` do usuário
- Incrementa `occurrences_with_photo` se houver foto
- Atualiza `updated_at` do usuário

---

## 📐 RELACIONAMENTOS PRINCIPAIS

```
users (1) ─────→ (N) occurrences
              └→ (N) user_consent
              └→ (N) validations
              └→ (N) notifications
              └→ (N) reports

categories (1) ─→ (N) occurrences
             └─→ (N) sub_categories
             └─→ (N) indicators

sub_categories (1) ─→ (N) occurrences

occurrences (1) ─→ (N) occurrence_images
            ├─→ (N) deduplication_records
            ├─→ (N) validations
            ├─→ (N) occurrence_history
            └─→ (N) notifications
```

---

## 🔐 EXTENSÕES UTILIZADAS

- **uuid-ossp** - Geração de UUIDs
- **postgis** - Análise geoespacial (geometria, distância, clustering)

---

## 📈 ESTRATÉGIA DE INDEXAÇÃO

Índices foram cuidadosamente selecionados para:
1. **Buscas por filtro** - `user_id`, `category_id`, `status`, `is_active`
2. **Queries temporais** - `created_at`, `validated_at`, `generated_at`
3. **Ranking** - `priority_score DESC`, `density_score DESC`
4. **Análise espacial** - `neighborhood`, `geom (GIST)`
5. **Chaves únicas** - `phone_number`, `protocol_id`, `email`

---

## 🎯 CONFORMIDADE

- **LGPD** - Campos `anonymized`, `user_consent` para rastreamento de consentimento
- **Auditoria** - `occurrence_history` para rastreabilidade completa
- **Segurança** - Rate limiting com `user_rate_limit`
- **Integridade** - Constraints e Foreign Keys garantem consistência

---

*Última atualização: 2024*
