# Quick Start - Guia Rápido

Inicie a aplicação em 5 minutos!

## Opção 1: Com Docker (Recomendado)

### Pré-requisitos
- Docker instalado
- Docker Compose instalado

### Passos

```bash
# 1. Navegar ao diretório do projeto
cd spring-boot-employee

# 2. Iniciar o PostgreSQL com Docker Compose
docker-compose up -d

# 3. Aguardar 10 segundos para o banco iniciar
sleep 10

# 4. Compilar a aplicação
mvn clean compile

# 5. Executar a aplicação
mvn spring-boot:run
```

**Resultado:**
- API disponível em: `http://localhost:8080`
- Swagger UI em: `http://localhost:8080/swagger-ui.html`
- PgAdmin em: `http://localhost:5050` (user: admin@example.com / password: admin)

## Opção 2: PostgreSQL Local

### Pré-requisitos
- PostgreSQL instalado e em execução
- Java 17+
- Maven 3.6+

### Passos

```bash
# 1. Criar o banco de dados
psql -U postgres -c "CREATE DATABASE employee_db;"

# 2. Atualizar application.yml com suas credenciais
# Edite: src/main/resources/application.yml
#   url: jdbc:postgresql://localhost:5432/employee_db
#   username: seu_usuario
#   password: sua_senha

# 3. Navegar ao diretório do projeto
cd spring-boot-employee

# 4. Compilar a aplicação
mvn clean compile

# 5. Executar a aplicação
mvn spring-boot:run
```

## Primeiro Teste

Crie um funcionário com curl:

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "joao@example.com",
    "cpf": "12345678901",
    "position": "Desenvolvedor",
    "salary": 5000.0,
    "department": "TI"
  }'
```

**Resultado esperado (201 Created):**
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

## Endpoints Básicos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/employees` | Criar funcionário |
| GET | `/api/v1/employees/{id}` | Buscar por ID |
| GET | `/api/v1/employees` | Listar ativos |
| GET | `/api/v1/employees/all` | Listar todos |
| GET | `/api/v1/employees/department/{dept}` | Buscar por departamento |
| PUT | `/api/v1/employees/{id}` | Atualizar |
| DELETE | `/api/v1/employees/{id}` | Deletar |
| POST | `/api/v1/employees/{id}/reactivate` | Reativar |

## Parar a Aplicação

### Docker
```bash
# Parar containers
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

### Local
```bash
# Pressionar Ctrl+C no terminal onde a aplicação está rodando
```

## Próximos Passos

- Explore a documentação no Swagger: `http://localhost:8080/swagger-ui.html`
- Leia o [README.md](README.md) para entender a arquitetura
- Veja os exemplos em [API_EXAMPLES.md](API_EXAMPLES.md)
- Execute os testes: `mvn test`

## Troubleshooting

### Porta 5432 (PostgreSQL) já em uso
```bash
# Altere a porta em docker-compose.yml
ports:
  - "5433:5432"  # Porta local 5433 mapa para 5432 do container
```

### Erro de conexão com banco
```bash
# Verifique se o PostgreSQL está rodando
docker ps | grep employee_db

# Se estiver parado, reinicie
docker-compose up -d postgres
```

### Maven command not found
```bash
# Instale Maven ou use o wrapper do projeto
./mvnw clean compile
./mvnw spring-boot:run
```

### Porta 8080 já em uso
```bash
# Altere em application.yml
server:
  port: 8081
```

## Dúvidas Frequentes

**P: Qual versão de Java preciso?**
R: Java 17 ou superior

**P: Posso usar MySQL em vez de PostgreSQL?**
R: Sim, mas precisará atualizar as dependências e configuração

**P: Como adicionar mais funcionalidades?**
R: Siga a estrutura de pastas e o padrão de arquitetura hexagonal

**P: A aplicação é pronta para produção?**
R: Não, esse é um exemplo educacional. Para produção, considere:
- Adicionar autenticação e autorização
- Implementar rate limiting
- Adicionar logging estruturado
- Configurar CORS apropriadamente
- Usar variáveis de ambiente para senhas

## Suporte

Para dúvidas, erros ou sugestões, consulte o [README.md](README.md) completo ou os exemplos em [API_EXAMPLES.md](API_EXAMPLES.md).

---

**Boa sorte! 🚀**
