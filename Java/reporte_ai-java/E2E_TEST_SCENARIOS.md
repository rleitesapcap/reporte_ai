# Testes End-to-End (E2E) - ReporteAI

## Overview

Documentação completa dos cenários de teste end-to-end para validar o fluxo completo da aplicação, incluindo autenticação, persistência, rate limiting e autorização.

## Ambiente de Teste

### Pré-requisitos
- PostgreSQL rodando
- Aplicação iniciada: `mvn spring-boot:run`
- Ferramenta HTTP: curl, Postman, ou similar
- jq para parsing JSON (opcional)

### URLs
- Aplicação: `http://localhost:8080`
- API Base: `http://localhost:8080/api/v1`
- Health: `http://localhost:8080/api/v1/auth/health`

## Cenários de Teste

### 1. Health Check

**Objetivo:** Verificar se a aplicação está operacional

```bash
curl -X GET http://localhost:8080/api/v1/auth/health

# Resposta esperada:
# {
#   "status": "UP",
#   "message": "Servidor está operacional"
# }
```

### 2. Registro de Novo Usuário

**Objetivo:** Criar uma nova conta

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "novouser",
    "email": "novo@exemplo.com",
    "password": "SenhaSegura123",
    "passwordConfirm": "SenhaSegura123",
    "fullName": "Novo Usuário"
  }'

# Resposta esperada:
# {
#   "status": "SUCCESS",
#   "message": "Usuário registrado com sucesso",
#   "userId": "uuid-aqui",
#   "username": "novouser",
#   "email": "novo@exemplo.com"
# }
```

**Validações:**
- [ ] Username deve ser único
- [ ] Email deve ser único e válido
- [ ] Senhas devem corresponder
- [ ] Todas as validações devem ser retornadas em caso de erro

### 3. Login com Credenciais Válidas

**Objetivo:** Obter tokens JWT

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'

# Resposta esperada:
# {
#   "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
#   "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
#   "tokenType": "Bearer",
#   "expiresIn": 86400,
#   "username": "admin"
# }
```

**Validações:**
- [ ] Access token retornado
- [ ] Refresh token retornado
- [ ] Token type é "Bearer"
- [ ] Expiration é 86400 (24 horas)

### 4. Tentar Login com Credenciais Inválidas

**Objetivo:** Validar rejeição de credenciais erradas

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "senhaErrada"
  }'

# Resposta esperada: 401 UNAUTHORIZED
# {
#   "error": "AUTHENTICATION_FAILED",
#   "message": "Credenciais inválidas"
# }
```

**Validações:**
- [ ] HTTP 401
- [ ] Mensagem de erro apropriada
- [ ] Tentativa falhada registrada no banco (failed_login_attempts incrementado)

### 5. Bloquear Usuário Após 5 Tentativas de Login Falhas

**Objetivo:** Validar bloqueio automático após falhas

```bash
# Executar 5 vezes o cenário 4 (login inválido)
# Após 5 tentativas, o usuário deve estar bloqueado

# Tentar login - deve falhar
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"senhaErrada"}'

# Resposta esperada: 401 UNAUTHORIZED
```

**Validações:**
- [ ] Usuário bloqueado no banco (is_locked = true)
- [ ] locked_until está preenchido (30 minutos no futuro)
- [ ] Mensagem de erro apropriada

### 6. Acessar Endpoint Protegido com Token Válido

**Objetivo:** Verificar autorização com JWT

```bash
# Obter token primeiro
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# Usar token em requisição protegida
curl -X GET http://localhost:8080/api/v1/notifications/user/uuid-aqui \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada: 200 OK (ou 404 se recurso não existe)
```

**Validações:**
- [ ] Token aceito
- [ ] Usuário autenticado
- [ ] Requisição processada

### 7. Acessar Endpoint Protegido sem Token

**Objetivo:** Validar rejeição de requisições não autenticadas

```bash
curl -X GET http://localhost:8080/api/v1/notifications/user/uuid-aqui

# Resposta esperada: 401 UNAUTHORIZED
# {
#   "status": 401,
#   "error": "Unauthorized",
#   "message": "...",
#   "timestamp": "..."
# }
```

**Validações:**
- [ ] HTTP 401
- [ ] Acesso negado

### 8. Acessar Endpoint Protegido com Token Inválido

**Objetivo:** Validar rejeição de tokens inválidos

```bash
curl -X GET http://localhost:8080/api/v1/notifications/user/uuid-aqui \
  -H "Authorization: Bearer token-invalido"

# Resposta esperada: 401 UNAUTHORIZED
```

**Validações:**
- [ ] HTTP 401
- [ ] Mensagem de erro apropriada

### 9. Validar Token

**Objetivo:** Verificar se um token é válido

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

curl -X POST http://localhost:8080/api/v1/auth/validate \
  -H "Content-Type: application/json" \
  -d "{\"token\":\"$TOKEN\"}"

# Resposta esperada:
# {
#   "isValid": true,
#   "username": "admin",
#   "expiresAt": "2024-01-21T10:00:00Z"
# }
```

**Validações:**
- [ ] Token válido retorna isValid: true
- [ ] Username correto
- [ ] Data de expiração presente

### 10. Refresh Token

**Objetivo:** Renovar access token

```bash
REFRESH=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.refreshToken')

curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH\"}"

# Resposta esperada:
# {
#   "accessToken": "novoToken...",
#   "tokenType": "Bearer",
#   "expiresIn": 86400
# }
```

**Validações:**
- [ ] Novo access token gerado
- [ ] Token válido para requisições

### 11. Logout e Revogação de Token

**Objetivo:** Verificar revogação de token

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# Fazer logout
curl -X POST http://localhost:8080/api/v1/auth/logout \
  -H "Authorization: Bearer $TOKEN"

# Tentar usar o mesmo token - deve falhar
curl -X GET http://localhost:8080/api/v1/notifications/user/uuid-aqui \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada: 401 UNAUTHORIZED
```

**Validações:**
- [ ] Logout retorna status SUCCESS
- [ ] Token adicionado ao blacklist
- [ ] Token rejeitado em requisições subsequentes

### 12. Rate Limiting

**Objetivo:** Validar limite de requisições

```bash
# Fazer muitas requisições rapidamente
for i in {1..150}; do
  curl -s http://localhost:8080/api/v1/auth/health -w "Status: %{http_code}\n"
done

# Após 100 requisições, respostas devem retornar 429 TOO_MANY_REQUESTS
```

**Validações:**
- [ ] Primeiras 100 requisições: 200 OK
- [ ] Requisições 101+: 429 TOO_MANY_REQUESTS
- [ ] Headers de rate limit presentes

### 13. Acesso por Role - Admin

**Objetivo:** Verificar acesso exclusivo ADMIN

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# Acesso a endpoint ADMIN
curl -X GET http://localhost:8080/api/v1/system-settings \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada: 200 OK (lista de configurações)
```

**Validações:**
- [ ] Admin tem acesso

### 14. Acesso por Role - User Comum

**Objetivo:** Verificar rejeição de acesso ADMIN para usuário comum

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' \
  | jq -r '.accessToken')

# Tentar acesso a endpoint ADMIN
curl -X GET http://localhost:8080/api/v1/system-settings \
  -H "Authorization: Bearer $TOKEN"

# Resposta esperada: 403 FORBIDDEN
# {
#   "status": 403,
#   "error": "Forbidden",
#   "message": "..."
# }
```

**Validações:**
- [ ] HTTP 403 Forbidden
- [ ] Acesso negado

### 15. Criar Notificação (com permissão)

**Objetivo:** Criar recurso com autenticação

```bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

curl -X POST http://localhost:8080/api/v1/notifications \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "uuid-usuario",
    "notificationType": "EMAIL",
    "title": "Teste",
    "message": "Mensagem de teste"
  }'

# Resposta esperada: 201 CREATED
```

**Validações:**
- [ ] Notificação criada
- [ ] ID retornado
- [ ] Timestamp preenchido

### 16. Persistência de Dados

**Objetivo:** Verificar que dados são salvos no banco

```bash
# 1. Registrar novo usuário
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "persisttest",
    "email": "persist@test.com",
    "password": "Test123",
    "passwordConfirm": "Test123",
    "fullName": "Persist Test"
  }'

# 2. Consultar banco diretamente
psql -U reporteai -d reporteai_db -c "SELECT * FROM auth_users WHERE username = 'persisttest';"

# Resposta esperada: 1 linha com os dados do usuário

# 3. Fazer login com credenciais
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"persisttest","password":"Test123"}'

# Resposta esperada: 200 OK com tokens
```

**Validações:**
- [ ] Usuário persisted no banco
- [ ] Senha armazenada com hash
- [ ] Login funciona após restart

## Checklist de Validação

### Autenticação
- [ ] Registrar usuário funciona
- [ ] Login com credenciais válidas funciona
- [ ] Login com credenciais inválidas falha
- [ ] Usuário bloqueado após 5 tentativas falhas
- [ ] Validação de token funciona
- [ ] Refresh token funciona
- [ ] Logout revoga token

### Autorização
- [ ] Admin acessa endpoints ADMIN
- [ ] User comum não acessa endpoints ADMIN
- [ ] Analyst acessa endpoints de análise
- [ ] Validator acessa endpoints de validação

### Persistência
- [ ] Dados de usuário salvos no banco
- [ ] Senhas com hash (não texto plano)
- [ ] Roles associadas corretamente
- [ ] Failed login attempts registrados
- [ ] Last login atualizado

### Segurança
- [ ] Rate limiting ativo
- [ ] Tokens JWT válidos
- [ ] Tokens revogados no logout
- [ ] CORS configurado
- [ ] Headers de segurança presentes

### Performance
- [ ] Requisições responsivas (< 1s)
- [ ] Conexões de banco pooled
- [ ] Compressão ativada
- [ ] Cache HTTP configurado

## Testes Automatizados (Opcional com Postman)

Importar `postman-collection.json` no Postman:

```json
{
  "info": {
    "name": "ReporteAI E2E Tests",
    "description": "End-to-end test suite"
  },
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "url": "{{base_url}}/api/v1/auth/health"
      }
    },
    {
      "name": "Register User",
      "request": {
        "method": "POST",
        "url": "{{base_url}}/api/v1/auth/register",
        "body": {
          "mode": "raw",
          "raw": "{...}"
        }
      }
    }
  ]
}
```

## Troubleshooting

### Token expirado
- Usar refresh token para obter novo access token
- Ou fazer login novamente

### Usuário bloqueado
- Aguardar 30 minutos
- Ou contatar admin para desbloquear

### Rate limit excedido
- Aguardar 1 minuto para reset
- Usar endpoint diferente

### Banco de dados não conecta
- Verificar se PostgreSQL está rodando
- Verificar credenciais em application.yml
- Verificar porta 5432

## Conclusão

Se todos os cenários passarem com sucesso, a aplicação está pronta para produção!
