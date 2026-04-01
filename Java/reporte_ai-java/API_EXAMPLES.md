# Exemplos de Uso da API

Aqui estão exemplos práticos de como usar a API de Gestão de Funcionários.

## Usando cURL

### 1. Criar novo funcionário

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

### 2. Buscar funcionário por ID

```bash
curl -X GET http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json"
```

### 3. Listar todos os funcionários ativos

```bash
curl -X GET http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json"
```

### 4. Listar todos os funcionários (incluindo inativos)

```bash
curl -X GET http://localhost:8080/api/v1/employees/all \
  -H "Content-Type: application/json"
```

### 5. Buscar por departamento

```bash
curl -X GET http://localhost:8080/api/v1/employees/department/TI \
  -H "Content-Type: application/json"
```

### 6. Atualizar funcionário

```bash
curl -X PUT http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva Santos",
    "email": "joao.novo@example.com",
    "position": "Desenvolvedor Senior",
    "salary": 7000.0,
    "department": "TI"
  }'
```

### 7. Deletar funcionário

```bash
curl -X DELETE http://localhost:8080/api/v1/employees/1 \
  -H "Content-Type: application/json"
```

### 8. Reativar funcionário

```bash
curl -X POST http://localhost:8080/api/v1/employees/1/reactivate \
  -H "Content-Type: application/json"
```

## Usando JavaScript/Fetch

### Criar funcionário

```javascript
fetch('http://localhost:8080/api/v1/employees', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    name: 'Maria Santos',
    email: 'maria@example.com',
    cpf: '98765432109',
    position: 'Analista',
    salary: 6000.0,
    department: 'RH'
  })
})
  .then(response => response.json())
  .then(data => console.log('Funcionário criado:', data))
  .catch(error => console.error('Erro:', error));
```

### Buscar funcionário

```javascript
fetch('http://localhost:8080/api/v1/employees/1', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  }
})
  .then(response => response.json())
  .then(data => console.log('Funcionário:', data))
  .catch(error => console.error('Erro:', error));
```

### Listar funcionários

```javascript
fetch('http://localhost:8080/api/v1/employees', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  }
})
  .then(response => response.json())
  .then(data => console.log('Lista de funcionários:', data))
  .catch(error => console.error('Erro:', error));
```

### Atualizar funcionário

```javascript
fetch('http://localhost:8080/api/v1/employees/1', {
  method: 'PUT',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    name: 'João Silva Atualizado',
    email: 'joao.novo@example.com',
    position: 'Tech Lead',
    salary: 8000.0,
    department: 'TI'
  })
})
  .then(response => response.json())
  .then(data => console.log('Funcionário atualizado:', data))
  .catch(error => console.error('Erro:', error));
```

### Deletar funcionário

```javascript
fetch('http://localhost:8080/api/v1/employees/1', {
  method: 'DELETE',
  headers: {
    'Content-Type': 'application/json',
  }
})
  .then(response => {
    if (response.ok) {
      console.log('Funcionário deletado com sucesso');
    }
  })
  .catch(error => console.error('Erro:', error));
```

## Usando Python

### Criar funcionário

```python
import requests
import json

url = 'http://localhost:8080/api/v1/employees'
headers = {'Content-Type': 'application/json'}

data = {
    'name': 'Carlos Mendes',
    'email': 'carlos@example.com',
    'cpf': '11122233344',
    'position': 'Gerente',
    'salary': 9000.0,
    'department': 'Financeiro'
}

response = requests.post(url, headers=headers, json=data)
print(response.json())
```

### Buscar funcionário

```python
import requests

url = 'http://localhost:8080/api/v1/employees/1'
response = requests.get(url)
print(response.json())
```

### Listar funcionários

```python
import requests

url = 'http://localhost:8080/api/v1/employees'
response = requests.get(url)
employees = response.json()
for employee in employees:
    print(f"{employee['id']}: {employee['name']} - {employee['email']}")
```

### Atualizar funcionário

```python
import requests

url = 'http://localhost:8080/api/v1/employees/1'
headers = {'Content-Type': 'application/json'}

data = {
    'name': 'João Silva Atualizado',
    'email': 'joao.novo@example.com',
    'position': 'Tech Lead',
    'salary': 8500.0,
    'department': 'TI'
}

response = requests.put(url, headers=headers, json=data)
print(response.json())
```

### Deletar funcionário

```python
import requests

url = 'http://localhost:8080/api/v1/employees/1'
response = requests.delete(url)
print(f"Status: {response.status_code}")
```

## Usando Postman

### Importar Collection

1. Abra o Postman
2. Clique em "Import"
3. Cole a seguinte collection JSON:

```json
{
  "info": {
    "name": "Employee Management API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Create Employee",
      "request": {
        "method": "POST",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"João Silva\",\n  \"email\": \"joao@example.com\",\n  \"cpf\": \"12345678901\",\n  \"position\": \"Desenvolvedor\",\n  \"salary\": 5000.0,\n  \"department\": \"TI\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/v1/employees",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "employees"]
        }
      }
    },
    {
      "name": "Get Employee",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/employees/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "employees", "1"]
        }
      }
    },
    {
      "name": "List Employees",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/employees",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "employees"]
        }
      }
    },
    {
      "name": "Update Employee",
      "request": {
        "method": "PUT",
        "header": [
          {
            "key": "Content-Type",
            "value": "application/json"
          }
        ],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"name\": \"João Silva Atualizado\",\n  \"email\": \"joao.novo@example.com\",\n  \"position\": \"Desenvolvedor Senior\",\n  \"salary\": 7000.0,\n  \"department\": \"TI\"\n}"
        },
        "url": {
          "raw": "http://localhost:8080/api/v1/employees/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "employees", "1"]
        }
      }
    },
    {
      "name": "Delete Employee",
      "request": {
        "method": "DELETE",
        "header": [],
        "url": {
          "raw": "http://localhost:8080/api/v1/employees/1",
          "protocol": "http",
          "host": ["localhost"],
          "port": "8080",
          "path": ["api", "v1", "employees", "1"]
        }
      }
    }
  ]
}
```

## Tratamento de Erros

### Email já existe (409)

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Outro Usuário",
    "email": "joao@example.com",
    "cpf": "99999999999",
    "position": "Desenvolvedor",
    "salary": 5000.0,
    "department": "TI"
  }'
```

Response:
```json
{
  "status": 409,
  "message": "Já existe um funcionário cadastrado com email: joao@example.com",
  "timestamp": "2024-01-15T10:35:00"
}
```

### Email inválido (400)

```bash
curl -X POST http://localhost:8080/api/v1/employees \
  -H "Content-Type: application/json" \
  -d '{
    "name": "João Silva",
    "email": "email-invalido",
    "cpf": "12345678901",
    "position": "Desenvolvedor",
    "salary": 5000.0,
    "department": "TI"
  }'
```

Response:
```json
{
  "status": 400,
  "message": "Erro na validação dos dados",
  "errors": {
    "email": "Email deve ser válido"
  },
  "timestamp": "2024-01-15T10:37:00"
}
```

### Funcionário não encontrado (404)

```bash
curl -X GET http://localhost:8080/api/v1/employees/999
```

Response:
```json
{
  "status": 404,
  "message": "Funcionário com ID 999 não encontrado",
  "timestamp": "2024-01-15T10:36:00"
}
```

## Dicas

1. **Swagger UI**: Acesse `http://localhost:8080/swagger-ui.html` para documentação interativa
2. **JSON Format**: Use ferramentas como [JSON Formatter](https://jsonformatter.org/) para validar JSON
3. **Teste com Postman**: Importe a collection acima para testes rápidos
4. **Logs**: Verifique os logs da aplicação para debugging
