# Guia de Autenticação e Autorização - ReporteAI

## Overview

Este documento descreve como a autenticação e autorização funcionam no ReporteAI usando JWT (JSON Web Tokens) com Spring Security.

## Arquitetura de Segurança

### Componentes Principais

1. **SecurityConfig** - Configuração principal de segurança
   - Define a cadeia de filtros de segurança
   - Configura CORS
   - Habilita autenticação por método

2. **JwtTokenProvider** - Gerador e validador de tokens JWT
   - Gera access tokens com validade de 24 horas
   - Gera refresh tokens com validade de 7 dias
   - Valida e extrai informações dos tokens

3. **JwtAuthenticationFilter** - Filtro de autenticação
   - Intercepta requisições HTTP
   - Valida tokens JWT
   - Configura o contexto de segurança

4. **CustomUserDetailsService** - Carregador de detalhes do usuário
   - Carrega informações do usuário do banco de dados
   - Define roles e permissões

5. **AuthService** - Serviço de autorização
   - Verifica permissões do usuário
   - Valida acesso a recursos específicos

6. **AuthController** - Endpoints de autenticação
   - `/api/v1/auth/login` - Login
   - `/api/v1/auth/refresh` - Renovar token
   - `/api/v1/auth/validate` - Validar token
   - `/api/v1/auth/health` - Health check

## Roles e Permissões

### Roles Disponíveis

```
ROLE_ADMIN
  - Acesso total ao sistema
  - Gerenciamento de usuários e configurações
  - Acesso a todos os endpoints

ROLE_USER
  - Acesso a funcionalidades básicas
  - Criar e visualizar ocorrências
  - Visualizar notificações pessoais

ROLE_ANALYST
  - Acesso a análises e relatórios
  - Criar indicadores
  - Acessar funcionalidades avançadas

ROLE_VALIDATOR
  - Validar ocorrências
  - Registrar validações
  - Avaliar qualidade dos dados

ROLE_NOTIFICATION_SENDER
  - Enviar notificações
  - Gerenciar alertas

ROLE_REPORT_CREATOR
  - Criar relatórios
  - Gerar análises
```

## Como Testar

### 1. Health Check (Sem Autenticação)
```bash
curl -X GET http://localhost:8080/api/v1/auth/health
```

Resposta esperada:
```json
{
  "status": "UP",
  "message": "Servidor está operacional"
}
```

### 2. Login e Obter Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**Usuários pré-configurados para teste:**

| Username | Password | Roles |
|----------|----------|-------|
| admin | admin123 | ADMIN, USER |
| analyst | analyst123 | ANALYST, USER |
| user | user123 | USER |
| validator | validator123 | VALIDATOR, USER |

Resposta esperada:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "refreshToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400,
  "username": "admin"
}
```

### 3. Usar Token em Requisições Autenticadas

```bash
curl -X GET http://localhost:8080/api/v1/notifications/user/{userId} \
  -H "Authorization: Bearer {accessToken}"
```

### 4. Renovar Token (Refresh)
```bash
curl -X POST http://localhost:8080/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{
    "refreshToken": "{refreshToken}"
  }'
```

Resposta esperada:
```json
{
  "accessToken": "eyJhbGciOiJIUzUxMiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

### 5. Validar Token
```bash
curl -X POST http://localhost:8080/api/v1/auth/validate \
  -H "Content-Type: application/json" \
  -d '{
    "token": "{accessToken}"
  }'
```

Resposta esperada:
```json
{
  "isValid": true,
  "username": "admin",
  "expiresAt": "2024-01-21T10:00:00Z"
}
```

## Controle de Acesso por Endpoint

### Endpoints Públicos (Sem Autenticação)
- `GET /api/v1/auth/health` - Health check
- `POST /api/v1/auth/login` - Login
- `GET /swagger-ui.html` - Swagger UI
- `GET /v3/api-docs/**` - OpenAPI docs

### Endpoints Protegidos - USER
Requerem pelo menos role `USER`:
- `GET /api/v1/notifications/user/{userId}`
- `GET /api/v1/occurrences`
- `GET /api/v1/reports/type/{type}`
- Etc.

### Endpoints Protegidos - ADMIN
Requerem role `ADMIN`:
- `POST /api/v1/system-settings` - Criar configurações
- `GET /api/v1/system-settings` - Listar todas configurações
- `DELETE /api/v1/users/{id}` - Deletar usuário
- `GET /api/v1/analytics/quality-metrics` - Métricas de qualidade
- Etc.

### Endpoints Protegidos - ANALYST/VALIDATOR
Requerem roles específicas:
- `POST /api/v1/indicators` - Criar indicadores (ANALYST)
- `POST /api/v1/validations` - Criar validações (VALIDATOR)
- `GET /api/v1/analytics/trends-forecast` - Ver tendências (ANALYST)
- Etc.

## Estrutura do JWT Token

### Header
```json
{
  "alg": "HS512",
  "typ": "JWT"
}
```

### Payload
```json
{
  "sub": "admin",
  "authorities": [
    {
      "authority": "ROLE_ADMIN"
    },
    {
      "authority": "ROLE_USER"
    }
  ],
  "iat": 1705854000,
  "exp": 1705940400
}
```

## Configuração

### application.yml
```yaml
app:
  jwtSecret: mySecureSecretKeyForJWTTokenGenerationAndValidation123456789SuperSecure
  jwtExpirationInMs: 86400000  # 24 horas
  jwtRefreshExpirationInMs: 604800000  # 7 dias
```

**Em produção:**
- Mude `jwtSecret` para uma chave segura e complexa
- Use variáveis de ambiente para secrets
- Configure HTTPS obrigatório
- Ative rate limiting
- Implemente token blacklisting para logout

## Fluxo de Autenticação

```
1. Cliente envia credenciais
   POST /api/v1/auth/login
   
2. Servidor valida credenciais
   
3. Servidor gera JWT token
   - Access Token (24h)
   - Refresh Token (7 dias)
   
4. Cliente recebe tokens
   
5. Cliente inclui token em requisições
   Authorization: Bearer {accessToken}
   
6. Servidor valida token em JwtAuthenticationFilter
   
7. Se válido, requisição é processada
   Se inválido, retorna 401 Unauthorized
   
8. Quando access token expira, cliente usa refresh token
   POST /api/v1/auth/refresh
   
9. Servidor gera novo access token
```

## Testes Automáticos com Postman

### Variáveis de Ambiente (Postman)
```
{{base_url}} = http://localhost:8080
{{access_token}} = (capturado do login)
{{refresh_token}} = (capturado do login)
```

### Collection de Testes
1. **Health Check** - Verificar servidor
2. **Login Admin** - Obter access token
3. **Get Notifications** - Usar access token
4. **Refresh Token** - Renovar token
5. **Validate Token** - Validar token
6. **Access Denied** - Testar erro 403

## Segurança - Best Practices

1. **Em Desenvolvimento**
   - Use as credenciais de teste
   - Ative Swagger UI
   - Use HTTP local

2. **Em Produção**
   - HTTPS obrigatório
   - Tokens em HttpOnly cookies (se possível)
   - Implementar token revocation
   - Rate limiting por IP
   - Logging de autenticação
   - Monitorar tentativas falhas
   - Usar OAuth2 com provider externo (Google, GitHub, etc)
   - Implementar 2FA
   - CORS restritivo

## Troubleshooting

### "401 Unauthorized"
- Verifique se o token foi incluído no header
- Verifique o formato: `Authorization: Bearer {token}`
- Verifique se o token não expirou
- Valide o token com `/api/v1/auth/validate`

### "403 Forbidden"
- Usuário não tem a role necessária
- Verifique as roles do usuário no login
- Use um usuário com role apropriada

### "Chave de assinatura JWT inválida"
- Verifique que `app.jwtSecret` é consistente
- Regenere o token após mudança de secret

### Token sempre inválido
- Verifique se o relógio do servidor está sincronizado
- Verifique se a chave JWT foi alterada

## Próximas Melhorias

- [ ] Implementar persistência de usuários no banco de dados
- [ ] Adicionar recuperação de senha
- [ ] Implementar 2FA/MFA
- [ ] OAuth2 com provedores externos
- [ ] Token blacklisting/revocation
- [ ] Audit log de autenticação
- [ ] API keys para acesso programático
- [ ] Rate limiting por usuário
- [ ] Implementar permission-based access control (PBAC)

## Referências

- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io)
- [OWASP Authentication Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Authentication_Cheat_Sheet.html)
