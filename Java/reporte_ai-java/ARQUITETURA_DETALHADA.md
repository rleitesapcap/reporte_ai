# Documentação Arquitetural - Reporte AI

**Versão:** 2.0  
**Data:** April 14, 2026  
**Framework:** Spring Boot 3.2.5 + Java 21  
**Padrão Arquitetural:** Hexagonal Architecture (Ports & Adapters)

---

## Índice

1. [Visão Geral da Arquitetura](#visão-geral-da-arquitetura)
2. [Camadas e Responsabilidades](#camadas-e-responsabilidades)
3. [Fluxos de Execução](#fluxos-de-execução)
4. [Estrutura de Diretórios](#estrutura-de-diretórios)
5. [Roles do Sistema](#roles-do-sistema)
6. [Mapeamento de Classes](#mapeamento-de-classes)

---

## Visão Geral da Arquitetura

O Reporte AI utiliza **Arquitetura Hexagonal (Ports & Adapters)**, que:

- **Isola o domínio** do negócio de detalhes técnicos
- **Permite testes** sem dependências externas
- **Facilita manutenção** e evolução
- **Estrutura camadas** de forma clara e desacoplada

### Diagrama de Camadas

```
┌─────────────────────────────────────────────────────────────────┐
│                     ADAPTERS (Entrada/Saída)                    │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │  HTTP Controllers  │  │  Security (JWT)    │                 │
│  │  REST Endpoints    │  │  Authentication    │                 │
│  └────────────────────┘  └────────────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────────┐
│              APPLICATION SERVICES (Orquestração)                 │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │  App Services      │  │  DTOs              │                 │
│  │  Business Logic    │  │  Validation        │                 │
│  └────────────────────┘  └────────────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────────┐
│                DOMAIN LAYER (Núcleo do Negócio)                  │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │  Entities          │  │  Ports (Interfaces)│                 │
│  │  Business Rules    │  │  Contracts         │                 │
│  │  Exceptions        │  │  Abstractions      │                 │
│  └────────────────────┘  └────────────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────────┐
│           INFRASTRUCTURE LAYER (Implementação Técnica)           │
│  ┌────────────────────┐  ┌────────────────────┐                 │
│  │  Repository        │  │  Database          │                 │
│  │  Adapters          │  │  JPA Entities      │                 │
│  │  Implementations   │  │  Persistence       │                 │
│  └────────────────────┘  └────────────────────┘                 │
└─────────────────────────────────────────────────────────────────┘
                              ↓↑
┌─────────────────────────────────────────────────────────────────┐
│                    EXTERNAL SYSTEMS                              │
│                  PostgreSQL 15 + PostGIS                         │
└─────────────────────────────────────────────────────────────────┘
```

---

## Camadas e Responsabilidades

### 1️⃣ ADAPTER LAYER (Entrada/Saída)
**Localização:** `adapters/`

**Responsabilidade:** Interface com o mundo externo

#### HTTP Controllers
- **Classe Base:** `@RestController`
- **Localização:** `adapters/http/controller/`
- **Exemplos:**
  - `AuthController.java` - Autenticação, registro, login, logout
  - `EmployeeController.java` - Gerenciamento de funcionários
  - `OccurrenceController.java` - Ocorrências
  - `UserController.java` - Gerenciamento de usuários

**Responsabilidades:**
- Receber requisições HTTP
- Validar inputs (validação básica)
- Chamar Application Services
- Formatar respostas HTTP
- Aplicar segurança (@PreAuthorize, @Secured)

#### Security Adapters
- **Localização:** `adapters/security/`
- **Classes Principais:**
  - `SecurityConfig.java` - Configuração Spring Security
  - `JwtTokenProvider.java` - Geração e validação de JWT
  - `CustomUserDetailsService.java` - Carregamento de usuários

#### Configuração
- **Localização:** `adapters/config/`
- **Classes:**
  - `OpenApiConfig.java` - Swagger/OpenAPI
  - `DatabaseInitializerConfig.java` - Inicialização do banco

---

### 2️⃣ APPLICATION LAYER (Orquestração)
**Localização:** `application/`

**Responsabilidade:** Orquestrar a lógica de negócio

#### Application Services
- **Localização:** `application/service/`
- **Padrão:** Uma classe de serviço por domínio

**Exemplos:**
- `AuthUserApplicationService.java` - Autenticação
- `EmployeeApplicationService.java` - Funcionários
- `OccurrenceApplicationService.java` - Ocorrências
- `UserApplicationService.java` - Gerenciamento de usuários
- `AuditLogApplicationService.java` - Auditoria (LGPD)
- `InputValidationService.java` - Validação centralizada
- `TwoFactorAuthenticationService.java` - 2FA
- `DataEncryptionService.java` - Criptografia
- `EnhancedRateLimitingService.java` - Rate limiting

**Responsabilidades:**
- Coordenar fluxo de negócio
- Chamar Domain Entities
- Usar Repositories (via Ports)
- Tratar BusinessExceptions
- Logging e Auditoria

#### DTOs (Data Transfer Objects)
- **Localização:** `application/dto/`
- **Padrão:** Request/Response

**Exemplos:**
- `RegisterRequest.java` - Dados para registrar usuário
- `EmployeeResponse.java` - Resposta de funcionário
- `OccurrenceCreateRequest.java` - Dados para criar ocorrência

---

### 3️⃣ DOMAIN LAYER (Núcleo do Negócio)
**Localização:** `domain/`

**Responsabilidade:** Lógica de negócio pura, independente de tecnologia

#### Entities (Modelos de Domínio)
- **Localização:** `domain/entity/`
- **Padrão:** Sem anotações JPA, apenas lógica de negócio

**Classes Principais:**
- `Employee.java` - Funcionário
- `User.java` - Usuário do sistema
- `Occurrence.java` - Ocorrência reportada
- `Category.java` - Categoria
- `Report.java` - Relatório

**Exemplo: User.java**
```java
public class User {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;
    private Set<Role> roles;
    
    // Métodos de negócio
    public void addRole(Role role) { ... }
    public void removeRole(Role role) { ... }
    public boolean hasRole(String roleName) { ... }
    public void deactivate() { ... }
}
```

#### Ports (Interfaces)
- **Localização:** `domain/port/`
- **Padrão:** Interfaces que definem contratos

**Exemplos:**
- `EmployeeRepositoryPort.java` - Contrato de persistência
- `UserRepositoryPort.java` - Contrato de usuários
- `OccurrenceRepositoryPort.java` - Contrato de ocorrências

**Exemplo:**
```java
public interface EmployeeRepositoryPort {
    Employee save(Employee employee);
    Optional<Employee> findById(UUID id);
    List<Employee> findAll();
    void delete(UUID id);
}
```

#### Exceptions
- **Localização:** `domain/exception/`
- **Padrão:** Exceções específicas do domínio

**Classes:**
- `BusinessException.java` - Erro de negócio
- `DuplicateDataException.java` - Dados duplicados
- `EmployeeNotFoundException.java` - Não encontrado

#### Builders
- **Localização:** `domain/entity/`
- **Padrão:** Builder pattern para construir entidades

**Exemplos:**
- `EmployeeBuilder.java`
- `UserBuilder.java`
- `OccurrenceBuilder.java`

---

### 4️⃣ INFRASTRUCTURE LAYER (Implementação Técnica)
**Localização:** `infrastructure/`

**Responsabilidade:** Detalles técnicos, implementação de Ports

#### Repository Adapters
- **Localização:** `infrastructure/persistence/adapter/`
- **Implementam:** Ports do domínio
- **Padrão:** Adapter pattern

**Exemplo:**
```java
@Repository
public class EmployeeRepositoryAdapter implements EmployeeRepositoryPort {
    private final EmployeeJpaRepository jpaRepository;
    
    @Override
    public Employee save(Employee employee) {
        EmployeeJpaEntity entity = mapper.toEntity(employee);
        EmployeeJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }
}
```

#### JPA Entities
- **Localização:** `infrastructure/persistence/entity/`
- **Padrão:** Mapeamento para banco de dados
- **Sufixo:** `JpaEntity`

**Exemplo:**
```java
@Entity
@Table(name = "employees")
public class EmployeeJpaEntity extends AuditableEntity {
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;
}
```

#### JPA Repositories
- **Localização:** `infrastructure/persistence/repository/`
- **Padrão:** Spring Data JPA

**Exemplo:**
```java
public interface EmployeeJpaRepository extends JpaRepository<EmployeeJpaEntity, UUID> {
    Optional<EmployeeJpaEntity> findByEmail(String email);
    List<EmployeeJpaEntity> findByIsDeletedFalse();
}
```

---

## Fluxos de Execução

### Fluxo 1: Registrar Novo Usuário

**Endpoint:** `POST /api/v1/auth/register`

#### Sequência de Classes e Métodos

```
┌─────────────────────────────────────────────────────────┐
│ 1. HTTP Request (Client)                                 │
│    POST /api/v1/auth/register                            │
│    Body: { username, email, password, passwordConfirm }  │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 2. AuthController.register()                             │
│    @PostMapping("/register")                             │
│    → Recebe RegisterRequest (DTO)                        │
│    → Valida passwordConfirm                              │
│    → Chama authUserService.registerUser()                │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 3. AuthUserApplicationService.registerUser()             │
│    → Chama validatePasswordStrength()                    │
│    → Verifica duplicatas (username/email)                │
│    → Cria entidade AuthUserJpaEntity                     │
│    → Busca role "USER" padrão                            │
│    → Chama authUserRepository.save()                     │
│    → Chama auditLogService.logUserRegistration()        │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 4. InputValidationService.validatePasswordStrength()     │
│    → Valida comprimento (12-128 caracteres)             │
│    → Verifica regex de complexidade                      │
│    → Lança BusinessException se inválido                │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 5. AuthUserRepository.save()                             │
│    (Spring Data JPA)                                     │
│    → Converte para SQL INSERT                            │
│    → Persiste em auth_users                              │
│    → Retorna entidade salva                              │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 6. AuditLogApplicationService.logUserRegistration()      │
│    → Cria estrutura de log                               │
│    → Registra timestamp UTC                              │
│    → Persiste em arquivo de auditoria                    │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 7. HTTP Response (201 Created)                           │
│    {                                                      │
│      "status": "SUCCESS",                                 │
│      "message": "Usuário registrado com sucesso",        │
│      "userId": "uuid-aqui",                              │
│      "username": "joao.silva"                            │
│    }                                                      │
└─────────────────────────────────────────────────────────┘
```

#### Classes Envolvidas

| Camada | Classe | Responsabilidade |
|--------|--------|-----------------|
| **Adapter** | `AuthController` | Receber requisição, validação básica, formatar resposta |
| **Application** | `AuthUserApplicationService` | Orquestrar fluxo, chamar validações, persistir |
| **Application** | `InputValidationService` | Validar força da senha |
| **Domain** | `AuthUserJpaEntity` | Mapear dados para banco |
| **Domain** | `RegisterRequest` | DTO com validações |
| **Infrastructure** | `AuthUserRepository` | Persistência em banco |
| **Infrastructure** | `PasswordEncoder` | Criptografar senha |
| **Application** | `AuditLogApplicationService` | Registrar auditoria |

---

### Fluxo 2: Login (Autenticação)

**Endpoint:** `POST /api/v1/auth/login`

#### Sequência de Classes e Métodos

```
┌─────────────────────────────────────────────────────────┐
│ 1. HTTP Request (Client)                                 │
│    POST /api/v1/auth/login                               │
│    Body: { username, password }                          │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 2. AuthController.login()                                │
│    @PostMapping("/login")                                │
│    → Recebe LoginRequest (DTO)                           │
│    → Chama authenticationManager.authenticate()          │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 3. Spring Security AuthenticationManager                 │
│    → Carrega usuário via CustomUserDetailsService       │
│    → Valida senha (PasswordEncoder)                      │
│    → Verifica se usuário está ativo                      │
│    → Verifica se não está bloqueado                      │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 4. CustomUserDetailsService.loadUserByUsername()        │
│    → Busca usuário em AuthUserRepository                 │
│    → Converte para UserDetails                           │
│    → Carrega roles do usuário                            │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 5. PasswordEncoder.matches()                             │
│    → Compara senha fornecida com hash armazenado        │
│    → Se falhar: AuthUserApplicationService.              │
│      recordFailedLoginAttempt()                          │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 6. AuthUserApplicationService.recordSuccessfulLogin()   │
│    → Reseta contador de tentativas falhas                │
│    → Desbloqueia usuário se estava bloqueado             │
│    → Registra último login                               │
│    → Chama auditLogService.logSuccessfulLogin()          │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 7. JwtTokenProvider.generateToken()                      │
│    → Cria JWT com claims (username, roles)               │
│    → Assina com chave secreta (HSA256)                   │
│    → Define expiração (24 horas)                         │
│    → Adiciona JTI (JWT ID) único                         │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 8. JwtTokenProvider.generateRefreshToken()               │
│    → Cria refresh token                                  │
│    → Validade maior (7 dias)                             │
└─────────────────────────────────────────────────────────┘
                        ↓
┌─────────────────────────────────────────────────────────┐
│ 9. HTTP Response (200 OK)                                │
│    {                                                      │
│      "accessToken": "eyJhbGciOiJIUzI1NiIs...",         │
│      "refreshToken": "eyJhbGciOiJIUzI1NiIs...",        │
│      "tokenType": "Bearer",                              │
│      "expiresIn": 86400,                                 │
│      "username": "joao.silva"                            │
│    }                                                      │
└─────────────────────────────────────────────────────────┘
```

#### Classes Envolvidas

| Camada | Classe | Responsabilidade |
|--------|--------|-----------------|
| **Adapter** | `AuthController` | Receber requisição, chamar autenticação |
| **Security** | `AuthenticationManager` | Spring Security - validar credenciais |
| **Security** | `CustomUserDetailsService` | Carregar usuário e roles |
| **Application** | `AuthUserApplicationService` | Registrar login bem-sucedido |
| **Security** | `JwtTokenProvider` | Gerar JWT e refresh token |
| **Application** | `AuditLogApplicationService` | Registrar auditoria |
| **Infrastructure** | `AuthUserRepository` | Buscar e atualizar usuário |
| **Infrastructure** | `PasswordEncoder` | Validar senha (BCrypt) |

---

### Fluxo 3: Criar Ocorrência (Caso Complexo)

**Endpoint:** `POST /api/v1/occurrences`

#### Sequência Simplificada

```
1. OccurrenceController.create()
   ↓
2. OccurrenceApplicationService.createOccurrence()
   ├─ InputValidationService.validate*() ← Validação centralizada
   ├─ UserApplicationService.getUserById() ← Carregar usuário
   ├─ CategoryApplicationService.getCategoryById() ← Carregar categoria
   ├─ OccurrenceRepository.save() ← Persistir ocorrência
   └─ AuditLogApplicationService.logDataModification() ← Auditoria
   ↓
3. OccurrenceNotificationService.notifyValidators()
   ├─ UserApplicationService.getUsersByRole("VALIDATOR")
   └─ NotificationApplicationService.sendNotification()
   ↓
4. HTTP Response (201 Created)
   {
     "id": "uuid",
     "title": "...",
     "description": "...",
     "status": "PENDING",
     "createdAt": "2026-04-14T10:30:00Z"
   }
```

---

## Estrutura de Diretórios

```
src/main/java/opus/social/app/reporteai/
│
├── ReporteAiApplication.java                 ← Classe principal
│
├── adapters/                                 ← ADAPTER LAYER
│   ├── http/
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── EmployeeController.java
│   │   │   ├── OccurrenceController.java
│   │   │   ├── UserController.java
│   │   │   └── ...
│   │   └── exception/
│   │       └── GlobalExceptionHandler.java
│   ├── security/
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   └── CustomUserDetailsService.java
│   └── config/
│       ├── OpenApiConfig.java
│       ├── DatabaseInitializerConfig.java
│       └── ...
│
├── application/                              ← APPLICATION LAYER
│   ├── service/
│   │   ├── AuthUserApplicationService.java
│   │   ├── EmployeeApplicationService.java
│   │   ├── OccurrenceApplicationService.java
│   │   ├── UserApplicationService.java
│   │   ├── AuditLogApplicationService.java
│   │   ├── InputValidationService.java
│   │   ├── TwoFactorAuthenticationService.java
│   │   ├── DataEncryptionService.java
│   │   ├── EnhancedRateLimitingService.java
│   │   ├── DataMaskingService.java
│   │   ├── TokenBlacklistService.java
│   │   └── ...
│   └── dto/
│       ├── RegisterRequest.java
│       ├── LoginRequest.java
│       ├── EmployeeResponse.java
│       ├── OccurrenceCreateRequest.java
│       ├── UserCreateRequest.java
│       └── ...
│
├── domain/                                   ← DOMAIN LAYER
│   ├── entity/
│   │   ├── User.java
│   │   ├── UserBuilder.java
│   │   ├── Employee.java
│   │   ├── EmployeeBuilder.java
│   │   ├── Occurrence.java
│   │   ├── OccurrenceBuilder.java
│   │   ├── Category.java
│   │   ├── Report.java
│   │   ├── Notification.java
│   │   └── ...
│   ├── port/
│   │   ├── EmployeeRepositoryPort.java
│   │   ├── UserRepositoryPort.java
│   │   ├── OccurrenceRepositoryPort.java
│   │   └── ...
│   ├── exception/
│   │   ├── BusinessException.java
│   │   ├── DomainException.java
│   │   ├── DuplicateDataException.java
│   │   ├── EmployeeNotFoundException.java
│   │   └── ...
│   ├── factory/
│   │   └── exception/
│   │       ├── ExceptionHandlerStrategy.java
│   │       ├── BusinessExceptionHandlerStrategy.java
│   │       └── ExceptionHandlerRegistry.java
│   └── strategy/
│       └── validation/
│           ├── ValidationStrategy.java
│           ├── EmailValidationStrategy.java
│           ├── CpfValidationStrategy.java
│           └── ValidationContext.java
│
└── infrastructure/                           ← INFRASTRUCTURE LAYER
    └── persistence/
        ├── entity/
        │   ├── UserJpaEntity.java
        │   ├── EmployeeJpaEntity.java
        │   ├── OccurrenceJpaEntity.java
        │   ├── AuthUserJpaEntity.java
        │   ├── AuthRoleJpaEntity.java
        │   └── AuditableEntity.java
        ├── repository/
        │   ├── UserJpaRepository.java
        │   ├── EmployeeJpaRepository.java
        │   ├── AuthUserRepository.java
        │   ├── AuthRoleRepository.java
        │   └── ...
        └── adapter/
            ├── UserRepositoryAdapter.java
            ├── EmployeeRepositoryAdapter.java
            └── ...

src/main/resources/
├── application.yml                           ← Configuração principal
├── logback-spring.xml                        ← Logging configurado
└── db/migration/
    ├── V1__create_schema.sql                 ← Schema principal
    ├── V2__seed_master_data.sql              ← Dados iniciais
    └── V3__create_auth_schema.sql            ← Schema autenticação
```

---

## Roles do Sistema

### Roles Definidas na Base de Dados

**Tabela:** `auth_roles`

| ID | Role Name | Descrição | Permissões |
|----|-----------|-----------| ------------|
| uuid-1 | **ADMIN** | Administrador do sistema com acesso total | ✅ Todas as operações |
| uuid-2 | **ANALYST** | Analista de dados | ✅ Consultar dados, gerar relatórios, análise |
| uuid-3 | **VALIDATOR** | Validador de ocorrências | ✅ Validar ocorrências, aprovar/rejeitar |
| uuid-4 | **USER** | Usuário padrão | ✅ Criar ocorrência, acessar dados próprios |
| uuid-5 | **NOTIFICATION_SENDER** | Pode enviar notificações | ✅ Enviar notificações, gerenciar canais |
| uuid-6 | **REPORT_CREATOR** | Pode criar relatórios | ✅ Criar/editar/deletar relatórios |

### Matriz de Permissões por Role

| Operação | ADMIN | ANALYST | VALIDATOR | USER | NOTIFICATION_SENDER | REPORT_CREATOR |
|----------|-------|---------|-----------|------|---------------------|-----------------|
| **Autenticação** | | | | | | |
| Login | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Logout | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Mudar Senha | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| **Gerenciamento de Usuários** | | | | | | |
| Criar usuário | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Atualizar usuário | ✅ | ❌ | ❌ | ❓ | ❌ | ❌ |
| Deletar usuário | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Listar usuários | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Atribuir roles | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Ocorrências** | | | | | | |
| Criar ocorrência | ✅ | ✅ | ✅ | ✅ | ✅ | ✅ |
| Listar ocorrências | ✅ | ✅ | ✅ | ✅ | ❌ | ❌ |
| Validar ocorrência | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ |
| Aprovar ocorrência | ✅ | ❌ | ✅ | ❌ | ❌ | ❌ |
| Deletar ocorrência | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| **Relatórios** | | | | | | |
| Criar relatório | ✅ | ✅ | ❌ | ❌ | ❌ | ✅ |
| Editar relatório | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Deletar relatório | ✅ | ❌ | ❌ | ❌ | ❌ | ✅ |
| Visualizar relatório | ✅ | ✅ | ✅ | ✅ | ❌ | ✅ |
| **Notificações** | | | | | | |
| Enviar notificação | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ |
| Configurar canal | ✅ | ❌ | ❌ | ❌ | ✅ | ❌ |
| **Administração** | | | | | | |
| Configurar sistema | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Ver auditoria | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |
| Gerenciar extensões | ✅ | ❌ | ❌ | ❌ | ❌ | ❌ |

### Hierarquia de Roles

```
ADMIN (Root)
│
├── ANALYST (Análise)
│   └── Consultar, Analisar, Gerar Relatórios
│
├── VALIDATOR (Validação)
│   └── Validar, Aprovar/Rejeitar Ocorrências
│
├── NOTIFICATION_SENDER (Notificações)
│   └── Enviar, Configurar Canais
│
├── REPORT_CREATOR (Relatórios)
│   └── Criar, Editar, Deletar Relatórios
│
└── USER (Base)
    └── Criar Ocorrências, Acessar Próprios Dados
```

### Usuários Padrão (Teste/Development)

| Username | Email | Roles | Senha Padrão |
|----------|-------|-------|--------------|
| `admin` | admin@reporteai.local | ADMIN, USER | Definir após primeira execução |
| `analyst` | analyst@reporteai.local | ANALYST, USER | Definir após primeira execução |
| `validator` | validator@reporteai.local | VALIDATOR, USER | Definir após primeira execução |
| `user` | user@reporteai.local | USER | Definir após primeira execução |

**⚠️ IMPORTANTE:** Remover ou desativar estes usuários em produção!

---

## Mapeamento de Classes

### Padrão de Nomenclatura

#### 1. Domain Entity
- **Padrão:** `{Entidade}.java`
- **Localização:** `domain/entity/`
- **Exemplo:** `User.java`, `Employee.java`
- **Anotações:** Nenhuma (POJO puro)

#### 2. JPA Entity
- **Padrão:** `{Entidade}JpaEntity.java`
- **Localização:** `infrastructure/persistence/entity/`
- **Exemplo:** `UserJpaEntity.java`, `EmployeeJpaEntity.java`
- **Anotações:** `@Entity`, `@Table`

#### 3. Repository Port (Interface)
- **Padrão:** `{Entidade}RepositoryPort.java`
- **Localização:** `domain/port/`
- **Exemplo:** `UserRepositoryPort.java`
- **Tipo:** Interface pura

#### 4. JPA Repository
- **Padrão:** `{Entidade}JpaRepository.java`
- **Localização:** `infrastructure/persistence/repository/`
- **Exemplo:** `UserJpaRepository.java`
- **Extends:** `JpaRepository<{Entidade}JpaEntity, UUID>`

#### 5. Repository Adapter
- **Padrão:** `{Entidade}RepositoryAdapter.java`
- **Localização:** `infrastructure/persistence/adapter/`
- **Exemplo:** `UserRepositoryAdapter.java`
- **Implements:** `{Entidade}RepositoryPort`

#### 6. Application Service
- **Padrão:** `{Entidade}ApplicationService.java`
- **Localização:** `application/service/`
- **Exemplo:** `UserApplicationService.java`
- **Anotação:** `@Service`, `@Transactional`

#### 7. Controller
- **Padrão:** `{Entidade}Controller.java`
- **Localização:** `adapters/http/controller/`
- **Exemplo:** `UserController.java`
- **Anotações:** `@RestController`, `@RequestMapping`

#### 8. Request DTO
- **Padrão:** `{Entidade}CreateRequest.java` ou `{Entidade}UpdateRequest.java`
- **Localização:** `application/dto/`
- **Exemplo:** `UserCreateRequest.java`, `UserUpdateRequest.java`
- **Anotações:** `@Valid` com validadores

#### 9. Response DTO
- **Padrão:** `{Entidade}Response.java`
- **Localização:** `application/dto/`
- **Exemplo:** `UserResponse.java`
- **Anotações:** Jackson (serialização)

### Exemplo Completo: Entidade User

```
Domain Layer (Negócio Puro):
  └─ User.java (POJO)
       ├─ id: UUID
       ├─ username: String
       ├─ email: String
       └─ Métodos: addRole(), hasRole(), isActive()
     UserRepositoryPort.java (Interface)
       └─ save(), findById(), findAll(), delete()

Application Layer (Orquestração):
  └─ UserApplicationService.java
       ├─ createUser(UserCreateRequest)
       ├─ updateUser(UUID, UserUpdateRequest)
       ├─ deleteUser(UUID)
       └─ getUserById(UUID)
     UserCreateRequest.java (DTO)
       └─ username, email, password
     UserResponse.java (DTO)
       └─ id, username, email, roles

Adapter Layer (HTTP):
  └─ UserController.java
       ├─ POST /api/v1/users → create()
       ├─ PUT /api/v1/users/{id} → update()
       ├─ DELETE /api/v1/users/{id} → delete()
       └─ GET /api/v1/users/{id} → getById()

Infrastructure Layer (Técnico):
  └─ UserJpaEntity.java (Mapeado para DB)
       └─ @Table("users")
     UserJpaRepository.java (Spring Data)
       └─ extends JpaRepository<UserJpaEntity, UUID>
     UserRepositoryAdapter.java (Implementação)
       └─ implements UserRepositoryPort
```

---

## Fluxo de Requisição Completo

### Exemplo: PUT /api/v1/users/123

```
1. CLIENT
   PUT /api/v1/users/123
   Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   Content-Type: application/json
   {
     "email": "novo.email@example.com",
     "fullName": "Novo Nome"
   }

2. SECURITY FILTER
   ├─ JwtAuthenticationFilter.doFilterInternal()
   ├─ Extrai token do header
   ├─ JwtTokenProvider.validateToken()
   ├─ JwtTokenProvider.getUsernameFromToken()
   └─ CustomUserDetailsService.loadUserByUsername()

3. SPRING SECURITY
   ├─ Valida autorização (@PreAuthorize)
   └─ Popula SecurityContext

4. HTTP ADAPTER
   UserController.update(UUID id, UserUpdateRequest request)
   ├─ @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
   ├─ Valida @Valid UserUpdateRequest
   ├─ InputValidationService.validateEmail()
   └─ Chama userApplicationService.updateUser()

5. APPLICATION SERVICE
   UserApplicationService.updateUser(UUID id, UserUpdateRequest req)
   ├─ UserRepository.findById(id)
   ├─ User.update(request)  ← Lógica de negócio
   ├─ UserRepository.save(user)
   └─ AuditLogApplicationService.logDataModification()

6. DOMAIN LAYER
   User.update(UserUpdateRequest request)
   ├─ Valida email único
   ├─ Atualiza campos
   ├─ Registra timestamp

7. INFRASTRUCTURE
   UserRepositoryAdapter.save(User user)
   ├─ Converte User → UserJpaEntity
   ├─ UserJpaRepository.save(entity)
   └─ Converte UserJpaEntity → User

8. DATABASE
   UPDATE users SET email='...', full_name='...' WHERE id='...'

9. RESPONSE
   200 OK
   {
     "id": "123",
     "username": "joao.silva",
     "email": "novo.email@example.com",
     "fullName": "Novo Nome",
     "createdAt": "2026-01-15T10:30:00Z",
     "updatedAt": "2026-04-14T14:25:30Z"
   }

10. AUDIT LOG
    AUDIT_EVENT | timestamp=2026-04-14T14:25:30Z | 
    eventType=DATA_MODIFICATION | userId=admin | 
    resourceType=USER | resourceId=123 | 
    details=Dados modificados: email, fullName
```

---

## Conclusão

A arquitetura Hexagonal do Reporte AI proporciona:

✅ **Separação de Conceitos** - Cada camada tem responsabilidade clara  
✅ **Testabilidade** - Fácil criar testes unitários sem dependências  
✅ **Manutenibilidade** - Código organizado e estruturado  
✅ **Escalabilidade** - Fácil adicionar novos recursos  
✅ **Flexibilidade** - Trocar implementações sem afetar domínio  

**Próximo nível:** Role-Based Access Control (RBAC) implementado conforme matriz de permissões.

---

*Documento gerado: 2026-04-14*  
*Versão: 2.0 (Com todas as classes de segurança)*  
*Framework: Spring Boot 3.2.5 + Java 21*
