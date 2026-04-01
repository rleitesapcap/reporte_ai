# Employee Management Application

Segue Aplicação completa de gestão de funcionários desenvolvida com **Spring Boot**, seguindo a arquitetura **Hexagonal (Ports & Adapters)** e os princípios de **Clean Code**.

## Arquitetura Hexagonal

A arquitetura hexagonal (também conhecida como Ports & Adapters) separa a lógica de negócio das dependências técnicas:

```
┌─────────────────────────────────────────────────────────────┐
│                     ADAPTERS (Entrada)                      │
│                                                              │
│  ┌──────────────┐      ┌──────────────┐   ┌──────────────┐ │
│  │ REST         │      │ GraphQL      │   │ CLI          │ │
│  │ Controller   │      │ API          │   │ Interface    │ │
│  └──────────────┘      └──────────────┘   └──────────────┘ │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                 APPLICATION LAYER (Casos de Uso)            │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Services / Use Cases                                │  │
│  │  - CreateEmployee                                    │  │
│  │  - UpdateEmployee                                    │  │
│  │  - DeleteEmployee                                    │  │
│  │  - ListEmployees                                     │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                    DOMAIN LAYER (Core)                      │
│                                                              │
│  ┌────────────────┐  ┌──────────────┐  ┌──────────────┐   │
│  │ Entities       │  │ Value        │  │ Exceptions   │   │
│  │ (Funcionário)  │  │ Objects      │  │              │   │
│  └────────────────┘  └──────────────┘  └──────────────┘   │
│                                                              │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Ports (Interfaces)                                  │  │
│  │  - EmployeeRepositoryPort                            │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                              ▲
┌─────────────────────────────────────────────────────────────┐
│              ADAPTERS (Saída) - Infrastructure              │
│                                                              │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ JPA          │  │ REST Client  │  │ Message      │     │
│  │ Repository   │  │ Adapter      │  │ Queue        │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              ▼
┌─────────────────────────────────────────────────────────────┐
│            EXTERNAL SERVICES (Banco de Dados, APIs)         │
│                        PostgreSQL                           │
└─────────────────────────────────────────────────────────────┘
```

## Estrutura de Pastas

```
spring-boot-employee/
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   ├── EmployeeManagementApplication.java    # Classe principal
│   │   │   │
│   │   │   ├── domain/                               # DOMAIN LAYER
│   │   │   │   ├── entity/
│   │   │   │   │   └── Employee.java
│   │   │   │   ├── port/
│   │   │   │   │   └── EmployeeRepositoryPort.java   # Interface (Port)
│   │   │   │   └── exception/
│   │   │   │       ├── DomainException.java
│   │   │   │       ├── BusinessException.java
│   │   │   │       ├── EmployeeNotFoundException.java
│   │   │   │       └── DuplicateDataException.java
│   │   │   │
│   │   │   ├── application/                          # APPLICATION LAYER
│   │   │   │   ├── service/
│   │   │   │   │   └── EmployeeApplicationService.java
│   │   │   │   └── dto/
│   │   │   │       ├── CreateEmployeeRequest.java
│   │   │   │       ├── UpdateEmployeeRequest.java
│   │   │   │       └── EmployeeResponse.java
│   │   │   │
│   │   │   ├── infrastructure/                       # INFRASTRUCTURE LAYER
│   │   │   │   └── persistence/
│   │   │   │       ├── entity/
│   │   │   │       │   └── EmployeeJpaEntity.java
│   │   │   │       ├── repository/
│   │   │   │       │   └── EmployeeJpaRepository.java (Spring Data)
│   │   │   │       └── adapter/
│   │   │   │           └── EmployeeRepositoryAdapter.java # Implementação
│   │   │   │
│   │   │   └── adapters/                             # ADAPTERS LAYER
│   │   │       ├── http/
│   │   │       │   ├── controller/
│   │   │       │   │   └── EmployeeController.java
│   │   │       │   └── exception/
│   │   │       │       └── GlobalExceptionHandler.java
│   │   │       └── config/
│   │   │           └── OpenApiConfig.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       └── application-test.yml
│   │
│   └── test/
│       └── java/com/example/
│           ├── application/service/
│           │   └── EmployeeApplicationServiceTest.java
│           ├── adapters/http/controller/
│           │   └── EmployeeControllerTest.java
│           └── infrastructure/persistence/adapter/
│               └── EmployeeRepositoryAdapterTest.java
│
├── pom.xml
└── README.md
```

## Dependências Principais

- **Spring Boot 3.2.0** - Framework web
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL Driver** - Banco de dados
- **Validation** - Validação de dados
- **Springdoc OpenAPI** - Documentação Swagger/OpenAPI
- **Lombok** - Redução de boilerplate
- **JUnit 5** - Testes unitários
- **Mockito** - Mocking para testes
- **H2** - Banco em memória para testes

## Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.6+
- PostgreSQL 12+

### 1. Configurar Banco de Dados

```sql
CREATE DATABASE employee_db;
```

### 2. Configurar application.yml

Edite `src/main/resources/application.yml` com suas credenciais PostgreSQL:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/employee_db
    username: seu_usuario
    password: sua_senha
```

### 3. Compilar e Executar

```bash
# Navegar ao diretório do projeto
cd spring-boot-employee

# Compilar
mvn clean compile

# Executar os testes
mvn test

# Executar a aplicação
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Endpoints da API REST

### Documentação Swagger

**URL:** `http://localhost:8080/swagger-ui.html`

### Criar Funcionário

```http
POST /api/v1/employees
Content-Type: application/json

{
  "name": "João Silva",
  "email": "joao@example.com",
  "cpf": "12345678901",
  "position": "Desenvolvedor",
  "salary": 5000.0,
  "department": "TI"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "João Silva",
  "email": "joao@example.com",
  "cpf": "12345678901",
  "position": "Desenvolvedor",
  "salary": 5000.0,
  "department": "TI",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "active": true
}
```

### Buscar Funcionário por ID

```http
GET /api/v1/employees/1
```

### Listar Todos os Funcionários Ativos

```http
GET /api/v1/employees
```

### Listar Todos os Funcionários

```http
GET /api/v1/employees/all
```

### Buscar por Departamento

```http
GET /api/v1/employees/department/TI
```

### Atualizar Funcionário

```http
PUT /api/v1/employees/1
Content-Type: application/json

{
  "name": "João Silva Santos",
  "email": "joao.novo@example.com",
  "position": "Desenvolvedor Senior",
  "salary": 7000.0,
  "department": "TI"
}
```

### Deletar Funcionário

```http
DELETE /api/v1/employees/1
```

### Reativar Funcionário

```http
POST /api/v1/employees/1/reactivate
```

## Validações

- **Nome:** Obrigatório, máx 255 caracteres
- **Email:** Obrigatório, deve ser um email válido, único
- **CPF:** Obrigatório, exatamente 11 dígitos, único
- **Cargo:** Obrigatório, máx 100 caracteres
- **Salário:** Obrigatório, deve ser maior que 0
- **Departamento:** Obrigatório, máx 100 caracteres

## Exemplos de Erros

### Email duplicado (409 Conflict)

```json
{
  "status": 409,
  "message": "Já existe um funcionário cadastrado com email: joao@example.com",
  "timestamp": "2024-01-15T10:35:00"
}
```

### Funcionário não encontrado (404 Not Found)

```json
{
  "status": 404,
  "message": "Funcionário com ID 999 não encontrado",
  "timestamp": "2024-01-15T10:36:00"
}
```

### Validação inválida (400 Bad Request)

```json
{
  "status": 400,
  "message": "Erro na validação dos dados",
  "errors": {
    "email": "Email deve ser válido",
    "salary": "Salário deve ser maior que zero"
  },
  "timestamp": "2024-01-15T10:37:00"
}
```

## Executar Testes

```bash
# Todos os testes
mvn test

# Um teste específico
mvn test -Dtest=EmployeeApplicationServiceTest

# Com relatório de cobertura
mvn test jacoco:report
```

## Princípios de Clean Code Aplicados

1. **Single Responsibility Principle (SRP)** - Cada classe tem uma única responsabilidade
2. **Open/Closed Principle (OCP)** - Aberta para extensão, fechada para modificação
3. **Dependency Inversion Principle (DIP)** - Depende de abstrações, não de implementações
4. **Interface Segregation Principle (ISP)** - Interfaces específicas para cada cliente
5. **Don't Repeat Yourself (DRY)** - Reutilização de código

## Camadas da Aplicação

### Domain Layer (Núcleo)
- Contém as entidades de domínio
- Define as interfaces (ports) que a aplicação depende
- Define as exceções de negócio
- **Totalmente independente** de frameworks e tecnologias

### Application Layer
- Implementa os casos de uso
- Orquestra o domínio
- Contém a lógica de aplicação
- Não tem dependências de infraestrutura

### Infrastructure Layer
- Implementa as interfaces do domínio (ports)
- Acesso ao banco de dados via JPA
- Configurações técnicas

### Adapters Layer
- Controllers REST (Inbound Adapters)
- Exception Handlers
- Configuração do OpenAPI

## Padrões Utilizados

- **Adapter Pattern** - EmployeeRepositoryAdapter implementa EmployeeRepositoryPort
- **Repository Pattern** - Abstração de acesso a dados
- **Service Layer** - Lógica de negócio encapsulada
- **DTO Pattern** - Transferência de dados entre camadas
- **Exception Handling** - Tratamento centralizado de erros

## Roadmap Futuro

- [ ] Implementar Cache com Redis
- [ ] Adicionar Autenticação e Autorização (Spring Security)
- [ ] Implementar Paginação nos endpoints
- [ ] Adicionar Filtros avançados
- [ ] Integração com eventos (Spring Events)
- [ ] Implementar Auditoria (Hibernate Envers)
- [ ] Adicionar Testes de Integração
- [ ] CI/CD com GitHub Actions

## Documentação Adicional

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Ports & Adapters](https://alistair.cockburn.us/hexagonal-architecture/)

## Autor

Desenvolvido como exemplo de arquitetura hexagonal com Spring Boot.

## Licença

MIT License
