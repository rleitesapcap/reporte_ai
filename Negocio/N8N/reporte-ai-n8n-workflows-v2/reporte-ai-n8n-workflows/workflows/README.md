# Reporte AI — Workflows n8n

## Arquivos

| Arquivo | Descrição | Quando Usar |
|---|---|---|
| `01-reporte-ai-evolution-bot.json` | Bot principal via Evolution API | **RECOMENDADO** — Evolution API como middleware |
| `02-reporte-ai-meta-direto.json` | Bot principal direto com Meta Graph API | Sem Evolution API, chamando Meta direto |
| `03-reporte-ai-notificacoes.json` | Automações: notificações, alertas, health check | Usar sempre (independe do cenário) |

---

## Como Importar

1. Acesse seu n8n (http://localhost:5678)
2. Vá em **Workflows** → **Import from file**
3. Selecione o arquivo JSON
4. Configure as credenciais (veja abaixo)
5. Ative o workflow

---

## Variáveis de Ambiente Necessárias

Configure em **Settings > Variables** no n8n:

### Cenário 1: Evolution API (workflow 01 + 03)

```
EVOLUTION_BASE_URL = http://evolution-api:8080
EVOLUTION_API_KEY = sua-chave-evolution
EVOLUTION_INSTANCE = reporteai
SPRING_BOOT_URL = http://reporte-api:8081
CIDADE_PADRAO_ID = 1
SCORE_ALERTA_THRESHOLD = 15.0
GESTOR_PHONE = 5534999999999
ADMIN_PHONE = 5534999999999
```

### Cenário 2: Meta Direto (workflow 02 + 03)

```
META_ACCESS_TOKEN = seu-token-permanente-meta
META_PHONE_NUMBER_ID = seu-phone-number-id
SPRING_BOOT_URL = http://reporte-api:8081
CIDADE_PADRAO_ID = 1
SCORE_ALERTA_THRESHOLD = 15.0
GESTOR_PHONE = 5534999999999
ADMIN_PHONE = 5534999999999
```

---

## Credenciais Necessárias

### Workflow 01 (Evolution API)
- Nenhuma credencial n8n — autenticação via header apikey nos HTTP Request nodes

### Workflow 02 (Meta Direto)  
- **OpenAI API** — para classificação automática

### Workflow 03 (Notificações)
- **PostgreSQL** — conexão com banco do Reporte AI (para refresh de materialized views)

---

## Workflow 01: Bot com Evolution API

### Fluxo dos nós:

```
Webhook Evolution
  → Respond 200 (imediato)
    → Filtrar (event=messages.upsert, fromMe=false)
      → Extrair Dados (Code: normaliza payload)
        → Reagir ✅ (feedback instantâneo)
          → Switch Modo
            ├─ rapido       → OpenAI → Criar Ocorrência → Enviar Confirmação
            ├─ protocolo    → Consultar API → Formatar → Enviar Status
            ├─ numero (1-12)→ Enviar Menu Categorias
            ├─ sim          → Confirmar Sim
            └─ texto/outros → Menu Principal
```

### Detecção de modo automática:
- **rapido**: imagem com caption ou localização
- **consulta_protocolo**: texto que match `RPT-XXXX-XXXXX`
- **selecao_numero**: texto é "1" a "12"
- **confirmacao_sim**: texto começa com "sim" ou "s"
- **texto**: qualquer outro texto → mostra menu principal

---

## Workflow 02: Bot com Meta Direto

### Diferenças do workflow 01:

1. **Handshake de verificação**: O Code node trata GET (verificação) e POST (mensagens)
2. **Envio via Graph API**: Usa `https://graph.facebook.com/v21.0/{phoneNumberId}/messages`
3. **Autenticação**: Bearer token no header Authorization
4. **Botões e listas**: Usa formato `interactive` nativo da Meta (funciona!)
5. **Imagens**: Recebe `image.id` (precisa baixar via Graph API em vez de base64)

### Handshake da Meta:
O Code node "Processar Payload" trata automaticamente:
- GET com `hub.verify_token` → retorna `hub.challenge`
- POST com mensagem → extrai e normaliza dados

---

## Workflow 03: Notificações

### 4 triggers independentes:

| Trigger | Frequência | Função |
|---|---|---|
| Webhook `/notificacao-status` | Sob demanda | Spring Boot chama quando status muda |
| Cron 1h | A cada hora | Verifica top 5 prioridade, alerta gestor se score > threshold |
| Cron 15min | A cada 15 min | Refresh materialized views no PostgreSQL |
| Cron 5min | A cada 5 min | Health check da Evolution API, alerta se desconectou |

### Como o Spring Boot notifica mudança de status:
No `OcorrenciaService.atualizarStatus()`, adicionar chamada HTTP:

```java
// Após mudar status, notificar n8n
webClient.post()
    .uri("http://n8n:5678/webhook/notificacao-status")
    .bodyValue(Map.of(
        "protocolo", ocorrencia.getProtocolo(),
        "statusAnterior", statusAnterior.name(),
        "statusNovo", novoStatus.name(),
        "phone", ocorrencia.getUsuario().getHashTelefone(), // resolver para telefone
        "categoriaNome", ocorrencia.getCategoria().getNome()
    ))
    .retrieve()
    .bodyToMono(String.class)
    .subscribe();
```

---

## Ajustes Pós-Importação

1. **Credencial OpenAI**: Nos nodes "OpenAI Classificar", atualize o credential ID
2. **Credencial PostgreSQL**: No workflow 03, configure a conexão com o banco `reporteai`
3. **Variáveis**: Configure todas as variáveis listadas acima
4. **Ativar**: Ative os workflows na ordem: 03 (notificações) → 01 ou 02 (bot)
5. **Testar**: Mande uma mensagem pro número do bot e verifique a execução no n8n

---

## Troubleshooting

| Problema | Causa | Solução |
|---|---|---|
| Webhook não dispara | URL errada no Evolution/Meta | Verificar URL de produção do n8n |
| "Filtrar" descarta tudo | Event name diferente | Verificar se Evolution envia `messages.upsert` |
| OpenAI timeout | Rate limit ou key inválida | Verificar credencial, aumentar timeout |
| Spring Boot 500 | Banco sem dados iniciais | Rodar migration Flyway primeiro |
| Reação não aparece | Baileys limitação | Normal no Baileys; funciona na Meta Cloud |
