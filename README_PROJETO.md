# Reporte AI - Capitólio em Dados

## Visão Geral

**Reporte AI** é uma plataforma inteligente de mapeamento e análise de problemas urbanos e rurais para a cidade de Capitólio-MG. A solução integra coleta digital via WhatsApp, processamento com Ciência de Dados e Inteligência Artificial, análise geoespacial e visualização interativa para transformar percepções da comunidade em inteligência acionável.

---

## Tema Central

Usar Ciência de Dados para identificar e priorizar problemas da comunidade de Capitólio, com foco em impactos no dia a dia dos moradores.

---

## Problema da Comunidade

Em cidades com forte fluxo turístico e características de áreas urbanas/rurais, problemas como:
- Descarte irregular de lixo
- Pontos com iluminação insuficiente
- Problemas em vias e acessos
- Problemas de limpeza de lotes
- Problemas com estradas rurais
- Necessidade de manutenção em pontes e mata-burros rurais
- Demanda maior por limpeza e manutenção em épocas de movimento
- Dificuldade em transformar reclamações em informação organizada

**Recorte do Projeto:** Falta de dados organizados sobre limpeza urbana, descarte de lixo, manutenção de estradas rurais e pontos críticos de manutenção em áreas com circulação de moradores e turistas.

---

## Objetivos

### Objetivo Geral

Desenvolver uma plataforma social baseada em Ciência de Dados e Inteligência Artificial para coletar, estruturar, analisar e visualizar informações sobre problemas de limpeza urbana e manutenção comunitária em Capitólio-MG, permitindo identificação de padrões, priorização de demandas e geração de inteligência acionável.

### Objetivos Específicos

1. **Canal Digital (WhatsApp)** - Implementar coleta via máquina de estados com suporte a texto, fotos e localização
2. **Base de Dados** - Estruturar banco relacional preparado para análise e dados geoespaciais
3. **Qualidade de Dados** - Padronização, deduplicação, validação e controle de confiabilidade
4. **IA e Ciência de Dados** - Deduplicação inteligente, classificação automática e detecção de padrões
5. **Geolocalização** - Incorporar latitude/longitude para mapas de calor e análise espacial
6. **Priorização** - Desenvolver modelo de score inteligente baseado em gravidade, frequência, densidade e evidências
7. **Dashboards** - Construir visualizações interativas com indicadores e análises por região
8. **Devolutiva ao Cidadão** - Protocolo único, consulta de status e comunicação de atualizações
9. **Apoio à Decisão** - Fornecer dados estruturados para comunidade e poder público
10. **Governança de Dados** - Garantir conformidade com LGPD e boas práticas de proteção de dados

---

## Solução Proposta

### Pilares Principais

1. **Coleta Estruturada** - WhatsApp com fluxo conversacional baseado em máquina de estados
2. **Processamento Inteligente** - Pipeline de tratamento, validação e classificação de dados
3. **Análise com IA** - Deduplicação, classificação automática e cálculo de score de prioridade
4. **Georreferenciamento** - Análise espacial com mapas de calor e clusters
5. **Visualização** - Dashboards interativos com filtros e relatórios analíticos

### Estrutura de Registro de Ocorrência

- **Categoria e Subcategoria** - Escolha estruturada com fallback para texto livre
- **Localização** - Bairro, referência, envio via GPS (latitude/longitude)
- **Evidência (Foto)** - Opcional (incentivada), obrigatória para casos críticos
- **Gravidade e Frequência** - Variáveis principais para priorização
- **Observações** - Campo livre com análise NLP para extração de palavras-chave

---

## Modelo de Inteligência Aplicada

### 1. Deduplicação Inteligente

Identifica automaticamente registros duplicados baseado em:
- Similaridade textual entre descrições
- Proximidade geográfica
- Intervalo temporal

Ações: vincular a caso existente, aumentar reincidência, evitar duplicação.

### 2. Classificação Automática

Classifica relatos em linguagem natural para categorias padronizadas.

**Exemplos:**
- "rua cheia de lixo" → Limpeza Urbana
- "poste apagado" → Iluminação Pública

### 3. Score Inteligente de Prioridade

```
Score = (gravidade × 3) + (frequência × 2) + densidade_região + reincidência + presença_foto
```

Permite identificar problemas críticos e priorizar ações com base em dados.

---

## Qualidade e Confiabilidade dos Dados

### Controle de Spam
- Limite de envios por usuário
- Validação de padrões de uso
- Score de confiança do usuário

### Score de Confiança Baseado em
- Histórico de uso
- Qualidade dos registros
- Presença de evidências

### Status da Ocorrência
- Recebida
- Em validação
- Validada
- Descartada

---

## Pipeline de Processamento

1. **Coleta** - Recebimento via WhatsApp e envio para API
2. **Persistência** - Armazenamento em banco estruturado
3. **Tratamento** - Limpeza, padronização, deduplicação, classificação
4. **Análise** - Ranking de problemas, análise por região, tendências, mapas de calor
5. **Visualização** - Dashboard com indicadores, filtros e análises geográficas

---

## Arquitetura da Plataforma

### Camada de Ingestão (WhatsApp)
Máquina de estados para coleta estruturada de dados com validação inicial.

### Camada Core (API - Spring Boot)
Registro de ocorrências, aplicação de regras de negócio, deduplicação, classificação e cálculo de score.

### Camada de Dados (PostgreSQL)
Persistência estruturada com suporte a geolocalização e histórico completo.

### Camada Analítica (Python/SQL)
Tratamento de dados, análise exploratória, identificação de padrões e cálculo de indicadores.

### Camada de Visualização (Dashboard)
Indicadores, mapas, filtros dinâmicos, relatórios e insights.

---

## Indicadores da Plataforma

### Operacionais
- Total de ocorrências
- Ocorrências por categoria
- Distribuição geográfica
- Volume por período

### Analíticos
- Índice de reincidência
- Densidade por região
- Tendência de crescimento/redução
- Score médio por categoria

### Qualidade
- Percentual com foto
- Taxa de duplicidade
- Nível de validação
- Confiabilidade dos dados

### Impacto (Evolução Futura)
- Tempo médio de resolução
- Redução de reincidência

---

## Público-Alvo

- **Moradores** - Usuários principais
- **Lideranças comunitárias**
- **Associações de moradores**
- **Instituições educacionais**
- **Gestores públicos**
- **Secretarias municipais** (infraestrutura, meio ambiente, turismo, desenvolvimento social)

---

## Estrutura do Dataset

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | int | Identificador único |
| data | timestamp | Data e hora do registro |
| bairro | varchar | Localização por bairro |
| categoria | varchar | Categoria padronizada |
| subcategoria | varchar | Subcategoria específica |
| gravidade | int | Nível de gravidade |
| frequência | int | Frequência de ocorrência |
| latitude | decimal | Coordenada geográfica |
| longitude | decimal | Coordenada geográfica |
| foto | varchar | URL da imagem (armazenada em S3) |
| descrição | text | Descrição detalhada |
| score | decimal | Score de prioridade calculado |
| status | varchar | Status da ocorrência |

---

## Governança de Dados (LGPD)

### Coleta Minimizada
- CPF não obrigatório
- Anonimização sempre que possível
- Uso de hash para identificação

### Consentimento
- Explícito do usuário
- Transparência sobre uso de dados
- Direito de acesso e exclusão

---

## Hipótese da Plataforma

Os problemas urbanos e rurais reportados pela comunidade apresentam padrões espaciais e temporais concentrados, especialmente em áreas de maior circulação e atividade turística. Esses padrões podem ser identificados e priorizados por meio de uma plataforma inteligente baseada em Ciência de Dados, Inteligência Artificial e análise geoespacial.

---

## Escopo Incluído (MVP)

### Implementado
- Canal de coleta via WhatsApp com máquina de estados
- API backend em Java Spring Boot
- Banco de dados PostgreSQL com modelo estruturado
- Pipeline de processamento e classificação
- Dashboard interativo com filtros dinâmicos
- Visualização geográfica (mapas de calor)
- Cálculo de indicadores e score de prioridade
- Protocolo único por ocorrência
- Devolutiva ao usuário

### Fora do Escopo Inicial
- Integração direta com sistemas da prefeitura
- Visão computacional para validação automática de imagens
- Automação completa de workflows
- Modelos preditivos avançados

---

## Resultados Esperados

- Aumento da participação da comunidade
- Transformação de dados informais em informação estruturada
- Identificação precisa de pontos críticos
- Melhoria na priorização de ações
- Apoio à gestão baseada em dados
- Fortalecimento da cultura de dados no município
- Base contínua para análises futuras

---

## Diferencial da Plataforma

A plataforma se diferencia por integrar:
- Coleta digital estruturada via WhatsApp
- Processamento inteligente de dados
- Aplicação de Inteligência Artificial
- Análise geoespacial avançada
- Visualização interativa
- Suporte à tomada de decisão baseada em evidências

Diferente de iniciativas pontuais, trata-se de uma solução contínua e evolutiva com potencial de integração institucional e expansão para outros municípios.

---

## Stack Tecnológico

- **Backend:** Java Spring Boot
- **Banco de Dados:** PostgreSQL
- **Armazenamento de Imagens:** AWS S3
- **Análise de Dados:** Python/SQL
- **Dashboard:** (a definir conforme projeto)
- **Integração WhatsApp:** API WhatsApp Business

---

## Contato e Informações

**Projeto:** Reporte AI - Capitólio em Dados  
**Localidade:** Capitólio-MG  
**Tema:** Observatório Comunitário Inteligente de Problemas Urbanos e Rurais  
**Foco:** Limpeza urbana, descarte de lixo e manutenção comunitária
