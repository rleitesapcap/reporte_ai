# ReporteAI

**Sistema inteligente de mapeamento e reporte de problemas urbanos e rurais.**

> Versão 1.0.0 — API REST desenvolvida com **Spring Boot 3.2.5**, arquitetura **Hexagonal (Ports & Adapters)** e **Java 21**.

---

## Índice

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura Hexagonal](#arquitetura-hexagonal)
- [Estrutura de Pastas](#estrutura-de-pastas)
- [Pré-requisitos](#pré-requisitos)
- [Como Executar](#como-executar)
- [Segurança e Autenticação](#segurança-e-autenticação)
- [Endpoints da API REST](#endpoints-da-api-rest)
- [Documentação Swagger / OpenAPI](#documentação-swagger--openapi)
- [Testes](#testes)
- [Padrões e Princípios](#padrões-e-princípios)
- [Licença](#licença)

---

## Visão Geral

O **ReporteAI** é uma plataforma para registro, acompanhamento e análise de ocorrências urbanas e rurais. Suas funcionalidades incluem:

- Cadastro e gestão de usuários com autenticação JWT
- Registro de ocorrências com imagens, geolocalização e histórico
- Categorização hierárquica (categoria / subcategoria)
- Relatórios e indicadores analíticos
- Clusterização espacial de ocorrências
- Sistema de notificações
- Validação de ocorrências com fluxo de aprovação
- Deduplicação automática de registros
- Controle de consentimento do usuário (LGPD)
- Rate limiting por usuário

---

## Stack Tecnológica

| Componente | Tecnologia | Versão |
|---|---|---|
| Linguagem | Java | 21 |
| Framework | Spring Boot | 3.2.5 |
| Persistência | Spring Data JPA + Hibernate | 6.4.4.Final |
| Banco de Dados | PostgreSQL + PostGIS | 15.4 + 3.3 |
| Migrações | Flyway | 9.22.3 |
| Segurança | Spring Security + JWT (jjwt) | 0.12.3 |
| OAuth2 | Spring OAuth2 Resource Server + Client | — |
| Documentação API | Springdoc OpenAPI | 2.2.0 |
| Rate Limiting | Bucket4j | 7.6.0 |
| Cache | Guava | 32.1.3-jre |
| Boilerplate | Lombok | — |
| Build | Maven | 3.9+ |
| Testes | JUnit 5, Mockito, H2 (memória) | — |
| Containers | Docker Compose (PostgreSQL + pgAdmin) | — |

---

## Arquitetura Hexagonal

```
┌─────────────────────────────────────────────────────────────┐
│                     ADAPTERS (Entrada)                      │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │ 18 REST Controllers (Auth, User, Employee,           │   │
│  │ Occurrence, Category, Report, Notification,           │   │
│  │ Validation, Indicator, Analytics, ...)                │   │
│  └──────────────────────────────────────────────────────┘   │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ GlobalExcep  │  │ Exception    │  │ Security     │     │
│  │ tionHandler  │  │ Strategies(8)│  │ Filters      │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│              APPLICATION LAYER (Casos de Uso)               │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  18 Application Services + 25 DTOs                   │   │
│  │  AuthService, UserService, OccurrenceService,        │   │
│  │  ReportService, NotificationService, ...             │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER (Core)                      │
│                                                              │
│  ┌────────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ 25 Entities    │  │ Value        │  │ Exceptions   │   │
│  │ (User, Occur-  │  │ Objects      │  │ de Domínio   │   │
│  │  rence, Report)│  │              │  │              │   │
│  └────────────────┘  └──────────────┘  └──────────────┘   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │  16 Repository Ports (Interfaces)                    │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                              ▲
┌─────────────────────────────────────────────────────────────┐
│              ADAPTERS (Saída) — Infrastructure              │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ 16 Repository │  │ 19 JPA       │  │ 19 JPA       │     │
│  │ Adapters      │  │ Entities     │  │ Repositories │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│            EXTERNAL SERVICES                                │
│       PostgreSQL 15 + PostGIS  ·  Docker Compose            │
└─────────────────────────────────────────────────────────────┘
```

---

## Estrutura de Pastas

```
reporte_ai-java/
├── pom.xml
├── docker-compose.yml
├── db/
│   ├── schema.sql
│   └── V2__seed_master_data.sql
├── docs/
│   ├── architecture-diagram.md
│   ├── data-model.md
│   ├── database-schema-detailed.md
│   └── DESIGN_PARTNERS.md
├── src/
│   ├── main/
│   │   ├── java/opus/social/app/reporteai/
│   │   │   ├── ReporteAiApplication.java
│   │   │   │
│   │   │   ├── domain/                                # DOMAIN LAYER
│   │   │   │   ├── entity/                            # 25 entidades de domínio
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── Occurrence.java
│   │   │   │   │   ├── Category.java / SubCategory.java
│   │   │   │   │   ├── Report.java
│   │   │   │   │   ├── Notification.java
│   │   │   │   │   ├── Validation.java
│   │   │   │   │   ├── Indicator.java / SpatialCluster.java
│   │   │   │   │   └── ... (Employee, OccurrenceImage, etc.)
│   │   │   │   ├── port/                              # 16 ports (interfaces)
│   │   │   │   │   ├── UserRepositoryPort.java
│   │   │   │   │   ├── OccurrenceRepositoryPort.java
│   │   │   │   │   └── ...
│   │   │   │   └── exception/
│   │   │   │       ├── DomainException.java
│   │   │   │       ├── BusinessException.java
│   │   │   │       └── ...
│   │   │   │
│   │   │   ├── application/                           # APPLICATION LAYER
│   │   │   │   ├── service/                           # 18 application services
│   │   │   │   │   ├── AuthService.java
│   │   │   │   │   ├── UserApplicationService.java
│   │   │   │   │   ├── OccurrenceApplicationService.java
│   │   │   │   │   ├── ReportApplicationService.java
│   │   │   │   │   └── ...
│   │   │   │   └── dto/                               # 25 DTOs
│   │   │   │       ├── auth/ (LoginRequest, RegisterRequest, ...)
│   │   │   │       ├── CreateOccurrenceRequest.java
│   │   │   │       └── ...
│   │   │   │
│   │   │   ├── infrastructure/                        # INFRASTRUCTURE LAYER
│   │   │   │   ├── persistence/
│   │   │   │   │   ├── entity/                        # 19 JPA entities
│   │   │   │   │   ├── repository/                    # 19 JPA repositories
│   │   │   │   │   └── adapter/                       # 16 repository adapters
│   │   │   │   └── config/
│   │   │   │       ├── DatabaseInitializerConfig.java
│   │   │   │       └── OpenApiConfig.java
│   │   │   │
│   │   │   └── adapters/                              # ADAPTERS LAYER
│   │   │       └── http/
│   │   │           ├── controller/                    # 18 REST controllers
│   │   │           │   ├── AuthController.java
│   │   │           │   ├── UserController.java
│   │   │           │   ├── OccurrenceController.java
│   │   │           │   ├── ReportController.java
│   │   │           │   ├── AnalyticsController.java
│   │   │           │   └── ...
│   │   │           ├── exception/
│   │   │           │   ├── GlobalExceptionHandler.java
│   │   │           │   └── handler/                   # 8 exception strategies
│   │   │           └── security/
│   │   │               ├── SecurityConfig.java
│   │   │               ├── JwtTokenProvider.java
│   │   │               ├── JwtAuthenticationFilter.java
│   │   │               ├── JwtAuthenticationEntryPoint.java
│   │   │               ├── CustomUserDetailsService.java
│   │   │               └── RateLimitingFilter.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-test.yml
│   │       └── db/migration/
│   │           └── V3__create_auth_tables.sql
│   │
│   └── test/java/opus/social/app/reporteai/
│       ├── adapters/http/controller/
│       │   ├── AuthControllerIntegrationTest.java
│       │   └── EmployeeControllerTest.java
│       ├── application/service/
│       │   ├── AuthUserApplicationServiceTest.java
│       │   ├── EmployeeApplicationServiceTest.java
│       │   ├── OccurrenceApplicationServiceTest.java
│       │   └── UserApplicationServiceTest.java
│       └── infrastructure/persistence/adapter/
│           └── EmployeeRepositoryAdapterTest.java
│
└── README.md
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java (JDK) | 21 |
| Maven | 3.9+ |
| Docker & Docker Compose | últimas versões estáveis |

---

## Como Executar

### 1. Subir o banco de dados com Docker Compose

```bash
docker-compose up -d
```

Isso inicializa:
- **PostgreSQL 15** com PostGIS na porta `5432` (database `reporteai_db`, user/password `reporteai/reporteai`)
- **pgAdmin 4** na porta `5050` (login `admin@example.com` / `admin`)

### 2. Compilar o projeto

```bash
mvn clean compile
```

### 3. Executar os testes

```bash
mvn test
```

### 4. Iniciar a aplicação

```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8082`.

---

## Segurança e Autenticação

### JWT (JSON Web Token)

| Parâmetro | Valor |
|---|---|
| Access Token | Validade 24 horas |
| Refresh Token | Validade 7 dias |
| Algoritmo | HMAC-SHA |
| Header | `Authorization: Bearer <token>` |

### Endpoints Públicos (sem autenticação)

| Método | Endpoint |
|---|---|
| POST | `/api/v1/auth/register` |
| POST | `/api/v1/auth/login` |
| GET | `/swagger-ui.html` |
| GET | `/actuator/**` |

Todos os demais endpoints requerem token JWT válido.

### Roles do Sistema

| Role | Descrição |
|---|---|
| `ROLE_USER` | Usuário padrão |
| `ROLE_ADMIN` | Administrador do sistema |
| `ROLE_ANALYST` | Analista de dados |
| `ROLE_VALIDATOR` | Validador de ocorrências |
| `ROLE_REPORT_CREATOR` | Criador de relatórios |
| `ROLE_NOTIFICATION_SENDER` | Emissor de notificações |

### Usuários Seed (Flyway V3)

| Usuário | Role |
|---|---|
| `admin` | ROLE_ADMIN |
| `analyst` | ROLE_ANALYST |
| `user` | ROLE_USER |
| `validator` | ROLE_VALIDATOR |

### CORS

Origens permitidas: `localhost:3000`, `localhost:4200`, `localhost:8082`, `localhost:5173`

### Rate Limiting

Controle de taxa de requisições por usuário via **Bucket4j**.

---

## Endpoints da API REST

**Base URL:** `http://localhost:8082/api/v1`

**Totais:** 97 endpoints (POST: 26 | GET: 48 | PUT: 14 | DELETE: 9) — 4 públicos, 93 protegidos.

### Autenticação (`/auth`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/auth/register` | Registrar novo usuário |
| POST | `/auth/login` | Autenticar e obter token JWT |
| POST | `/auth/refresh` | Renovar access token |
| GET | `/auth/validate` | Validar token atual |

### Usuários (`/users`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/users` | Criar usuário |
| GET | `/users` | Listar todos os usuários |
| GET | `/users/{id}` | Buscar por ID |
| GET | `/users/email/{email}` | Buscar por e-mail |
| GET | `/users/phone/{phone}` | Buscar por telefone |
| PUT | `/users/{id}` | Atualizar usuário |
| DELETE | `/users/{id}` | Remover usuário |

### Funcionários (`/employees`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/employees` | Criar funcionário |
| GET | `/employees` | Listar ativos |
| GET | `/employees/all` | Listar todos |
| GET | `/employees/{id}` | Buscar por ID |
| GET | `/employees/department/{dept}` | Buscar por departamento |
| PUT | `/employees/{id}` | Atualizar |
| DELETE | `/employees/{id}` | Desativar (soft delete) |
| POST | `/employees/{id}/reactivate` | Reativar |

### Ocorrências (`/occurrences`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/occurrences` | Criar ocorrência |
| GET | `/occurrences` | Listar todas |
| GET | `/occurrences/{id}` | Buscar por ID |
| GET | `/occurrences/protocol/{protocol}` | Buscar por protocolo |
| GET | `/occurrences/user/{userId}` | Listar por usuário |
| GET | `/occurrences/category/{categoryId}` | Listar por categoria |
| GET | `/occurrences/neighborhood/{neighborhood}` | Listar por bairro |
| GET | `/occurrences/status/{status}` | Listar por status |
| GET | `/occurrences/recent` | Listar recentes |
| PUT | `/occurrences/{id}` | Atualizar |

### Imagens de Ocorrência (`/occurrence-images`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/occurrence-images` | Upload de imagem |
| GET | `/occurrence-images/{id}` | Buscar por ID |
| GET | `/occurrence-images/occurrence/{occurrenceId}` | Listar por ocorrência |
| PUT | `/occurrence-images/{id}` | Atualizar metadados |
| PUT | `/occurrence-images/{id}/mark-processed` | Marcar como processada |
| DELETE | `/occurrence-images/{id}` | Remover imagem |

### Histórico de Ocorrência (`/occurrence-history`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/occurrence-history` | Registrar histórico |
| GET | `/occurrence-history/occurrence/{occurrenceId}` | Listar por ocorrência |
| DELETE | `/occurrence-history/{id}` | Remover registro |

### Categorias (`/categories`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/categories` | Criar categoria |
| GET | `/categories` | Listar ativas |
| GET | `/categories/all` | Listar todas |
| GET | `/categories/{id}` | Buscar por ID |
| PUT | `/categories/{id}` | Atualizar |
| DELETE | `/categories/{id}` | Remover |

### Subcategorias (`/sub-categories`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/sub-categories` | Criar subcategoria |
| GET | `/sub-categories` | Listar todas |
| GET | `/sub-categories/{id}` | Buscar por ID |
| GET | `/sub-categories/category/{categoryId}` | Listar por categoria |
| PUT | `/sub-categories/{id}` | Atualizar |
| DELETE | `/sub-categories/{id}` | Remover |

### Relatórios (`/reports`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/reports` | Criar relatório |
| GET | `/reports/type/{type}` | Listar por tipo |
| GET | `/reports/status/{status}` | Listar por status |
| PUT | `/reports/{id}/status` | Atualizar status |
| DELETE | `/reports/{id}` | Remover |

### Notificações (`/notifications`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/notifications` | Enviar notificação |
| GET | `/notifications/user/{userId}` | Listar por usuário |
| GET | `/notifications/user/{userId}/unread` | Listar não lidas |
| PUT | `/notifications/{id}/read` | Marcar como lida |
| DELETE | `/notifications/{id}` | Remover |

### Validações (`/validations`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/validations` | Criar validação |
| GET | `/validations/{id}` | Buscar por ID |
| GET | `/validations/occurrence/{occurrenceId}` | Listar por ocorrência |
| GET | `/validations/result/{result}` | Listar por resultado |
| PUT | `/validations/{id}` | Atualizar |
| DELETE | `/validations/{id}` | Remover |

### Indicadores (`/indicators`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/indicators` | Criar indicador |
| GET | `/indicators/type/{type}` | Buscar por tipo |
| GET | `/indicators/category/{categoryId}` | Buscar por categoria |
| DELETE | `/indicators/{id}` | Remover |

### Clusters Espaciais (`/spatial-clusters`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/spatial-clusters` | Criar cluster |
| GET | `/spatial-clusters/neighborhood/{neighborhood}` | Buscar por bairro |
| DELETE | `/spatial-clusters/{id}` | Remover |

### Analytics (`/analytics`)

| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/analytics/dashboard` | Dashboard geral |
| GET | `/analytics/period` | Dados por período |
| GET | `/analytics/geographic-clusters` | Clusters geográficos |

### Deduplicação (`/deduplication-records`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/deduplication-records` | Registrar deduplicação |
| GET | `/deduplication-records/occurrence/{occurrenceId}` | Buscar por ocorrência |
| DELETE | `/deduplication-records/{id}` | Remover |

### Configurações do Sistema (`/system-settings`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/system-settings` | Criar configuração |
| GET | `/system-settings` | Listar todas |
| GET | `/system-settings/{key}` | Buscar por chave |
| PUT | `/system-settings/{key}` | Atualizar |
| DELETE | `/system-settings/{key}` | Remover |

### Consentimento do Usuário (`/user-consents`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/user-consents` | Registrar consentimento |
| GET | `/user-consents/user/{userId}` | Listar por usuário |
| GET | `/user-consents/type/{type}` | Listar por tipo |
| DELETE | `/user-consents/{id}` | Revogar |

### Rate Limit por Usuário (`/user-rate-limits`)

| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/user-rate-limits` | Criar limite |
| GET | `/user-rate-limits/user/{userId}` | Consultar limite |
| PUT | `/user-rate-limits/user/{userId}/block` | Bloquear usuário |
| PUT | `/user-rate-limits/user/{userId}/unblock` | Desbloquear usuário |

---

## Documentação Swagger / OpenAPI

Com a aplicação em execução:

| Recurso | URL |
|---|---|
| Swagger UI | `http://localhost:8082/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8082/v3/api-docs` |

---

## Exemplo de Uso (cURL)

### 1. Registrar usuário

```bash
curl -X POST http://localhost:8082/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "password": "Senha@123",
    "cpf": "12345678901"
  }'
```

### 2. Fazer login e obter token

```bash
curl -X POST http://localhost:8082/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@example.com",
    "password": "Senha@123"
  }'
```

### 3. Usar token nos endpoints protegidos

```bash
curl http://localhost:8082/api/v1/occurrences \
  -H "Authorization: Bearer <seu_token_aqui>"
```

---

## Testes

**63 testes** — todos passando (0 falhas, 0 erros).

| Classe de Teste | Testes | Tempo |
|---|---|---|
| `AuthControllerIntegrationTest` | 12 | 10.75s |
| `AuthUserApplicationServiceTest` | 14 | 0.624s |
| `EmployeeRepositoryAdapterTest` | 10 | 0.106s |
| `EmployeeApplicationServiceTest` | 9 | 0.190s |
| `EmployeeControllerTest` | 8 | 1.222s |
| `OccurrenceApplicationServiceTest` | 5 | 0.098s |
| `UserApplicationServiceTest` | 5 | 0.101s |

```bash
# Executar todos os testes
mvn test

# Teste específico
mvn test -Dtest=AuthControllerIntegrationTest
```

---

## Padrões e Princípios

### Princípios SOLID

- **SRP** — Cada classe tem responsabilidade única
- **OCP** — Aberta para extensão, fechada para modificação
- **LSP** — Substituição de Liskov via ports/adapters
- **ISP** — Interfaces segregadas por domínio
- **DIP** — Domain layer depende apenas de abstrações

### Design Patterns

| Padrão | Aplicação |
|---|---|
| **Adapter** | 16 Repository Adapters implementam seus respectivos Ports |
| **Strategy** | 8 Exception Handler Strategies + Validation Strategies (Email, CPF) |
| **Repository** | Abstração de acesso a dados via Ports |
| **DTO** | 25 DTOs para transferência entre camadas |
| **Builder** | Construção de objetos de domínio |
| **Factory** | Criação de instâncias de estratégia |

---

## Códigos de Resposta HTTP

| Código | Significado |
|---|---|
| `200 OK` | Requisição bem-sucedida |
| `201 Created` | Recurso criado |
| `204 No Content` | Operação sem retorno (ex: delete) |
| `400 Bad Request` | Dados inválidos |
| `401 Unauthorized` | Token ausente ou inválido |
| `403 Forbidden` | Sem permissão para o recurso |
| `404 Not Found` | Recurso não encontrado |
| `409 Conflict` | Dados duplicados |
| `500 Internal Server Error` | Erro interno |

---

## Documentação Adicional

- [Diagrama de Arquitetura](docs/architecture-diagram.md)
- [Modelo de Dados](docs/data-model.md)
- [Schema Detalhado do Banco](docs/database-schema-detailed.md)

---

## Licença

MIT License
