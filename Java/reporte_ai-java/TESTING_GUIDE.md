# 🧪 Testing Guide - Design Patterns

**Reporte AI - Spring Boot Backend**  
**Testing Framework:** JUnit 5 + Mockito + Performance Benchmarks  
**Data:** 14 de Abril de 2026

---

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Estrutura de Testes](#estrutura-de-testes)
3. [Como Executar](#como-executar)
4. [Interpretando Resultados](#interpretando-resultados)
5. [Best Practices](#best-practices)
6. [CI/CD Integration](#cicd-integration)

---

## 🎯 Visão Geral

Este guia cobre a execução de testes para todos os 8 design patterns implementados:

```
✅ CQRS Pattern                - 4 testes unitários
✅ Queries & Caching           - 6 testes unitários
✅ Specification Pattern        - 14 testes unitários
✅ Event Sourcing Pattern       - 11 testes unitários
✅ Circuit Breaker Pattern      - 11 testes unitários
✅ Caching Strategy             - 10 testes unitários
✅ Saga Pattern                 - 8 testes unitários
✅ Integration Tests            - 8 testes de integração
✅ Performance Benchmarks       - 8 testes de performance
                               ─────────────────
TOTAL:                          80 testes
```

---

## 📁 Estrutura de Testes

```
src/test/java/com/reporte/ai/
│
├─ application/
│  ├─ bus/
│  │  ├─ CommandBusTest.java          (4 testes)
│  │  └─ QueryBusTest.java            (6 testes)
│  ├─ service/
│  │  └─ CircuitBreakerServiceTest.java (11 testes)
│  ├─ saga/
│  │  └─ SagaPatternTest.java         (8 testes)
│  └─ (outros services...)
│
├─ domain/
│  ├─ specification/
│  │  └─ SpecificationPatternTest.java (14 testes)
│  └─ event/
│     └─ EventSourcingTest.java       (11 testes)
│
├─ adapters/
│  └─ config/
│     └─ CacheConfigTest.java         (10 testes)
│
├─ integration/
│  └─ DesignPatternsIntegrationTest.java (8 testes)
│
└─ performance/
   └─ PerformanceBenchmarkTest.java   (8 testes)
```

---

## 🚀 Como Executar

### **1. Executar TODOS os Testes**

```bash
# Opção 1: Maven padrão
mvn clean test

# Opção 2: Com output detalhado
mvn clean test -X

# Opção 3: Com foco em patterns
mvn test -Dtest=*PatternTest

# Tempo esperado: ~30 segundos
# Resultado esperado: 80 PASSED
```

### **2. Executar Testes por Categoria**

#### **CQRS Pattern**
```bash
mvn test -Dtest=CommandBusTest,QueryBusTest
# Tempo: ~5 segundos
# Testes: 10
```

#### **Domain-Driven Design**
```bash
mvn test -Dtest=SpecificationPatternTest,EventSourcingTest
# Tempo: ~8 segundos
# Testes: 25
```

#### **Resilience & Performance**
```bash
mvn test -Dtest=CircuitBreakerServiceTest,CacheConfigTest
# Tempo: ~12 segundos
# Testes: 21
```

#### **Orchestration**
```bash
mvn test -Dtest=SagaPatternTest
# Tempo: ~4 segundos
# Testes: 8
```

#### **Integration Tests**
```bash
mvn test -Dtest=DesignPatternsIntegrationTest
# Tempo: ~6 segundos
# Testes: 8
```

#### **Performance Benchmarks**
```bash
mvn test -Dtest=PerformanceBenchmarkTest
# Tempo: ~15 segundos
# Testes: 8
# Nota: Gera output de performance
```

### **3. Executar Teste Específico**

```bash
# Um único teste
mvn test -Dtest=CommandBusTest#testExecuteCommandSuccess

# Padrão de matches
mvn test -Dtest=*CommandBus*

# Todos com "Cache" no nome
mvn test -Dtest=*Cache*
```

### **4. Gerar Relatório de Cobertura JaCoCo**

```bash
# Gerar relatório completo
mvn clean test jacoco:report

# Abrir relatório (Windows)
start target/site/jacoco/index.html

# Abrir relatório (Linux/Mac)
open target/site/jacoco/index.html
```

**Métricas esperadas:**
- Line Coverage: 85%+
- Branch Coverage: 80%+
- Complexity: Médio

### **5. SonarQube Code Quality Analysis**

```bash
# Análise local (requer SonarQube rodando em localhost:9000)
mvn clean test sonar:sonar \
  -Dsonar.projectKey=reporte-ai-patterns \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=YOUR_TOKEN

# Ou com Docker
docker run --name sonarqube -d -p 9000:9000 sonarqube:latest
```

### **6. Failsafe para Testes de Integração**

```bash
# Apenas testes unitários
mvn test

# Testes unitários + integração
mvn verify

# Apenas integração
mvn test -Dtest=*IntegrationTest
```

---

## 📊 Interpretando Resultados

### **Execução com Sucesso**

```
[INFO] Tests run: 80, Failures: 0, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**O que significa:**
- ✅ Todos os 80 testes passaram
- ✅ Zero falhas
- ✅ Padrões implementados corretamente

### **Com Falhas**

```
[INFO] Tests run: 80, Failures: 2, Errors: 1, Skipped: 0
[INFO] BUILD FAILURE
```

**Ações:**
1. Ver qual teste falhou: `[ERROR] testCachePerformanceFirstCall`
2. Ver a razão: `AssertionError: expected <true> but found <false>`
3. Debugar o teste
4. Revisar a implementação do padrão

### **Com Skipped Tests**

```
[INFO] Tests run: 80, Failures: 0, Errors: 0, Skipped: 3
[INFO] BUILD SUCCESS
```

**Significa:**
- Testes foram ignorados (anotados com `@Disabled`)
- Build ainda passa (skipped não é failure)
- Revisar por quê foram pulados

---

## 🎯 Best Practices

### **1. Rodar Testes Antes de Commitar**

```bash
# Pre-commit hook (Git)
#!/bin/bash
mvn clean test
if [ $? -ne 0 ]; then
  echo "Tests failed. Commit aborted."
  exit 1
fi

# Salvar em .git/hooks/pre-commit
```

### **2. Debugging de Teste**

```java
// Adicionar breakpoint
@Test
void testExample() {
    // ... setup
    String result = methodUnderTest();  // ← Breakpoint aqui
    assertEquals("expected", result);
}

// Executar com debug
mvn test -Dtest=CommandBusTest#testExecuteCommandSuccess -X
```

### **3. Executar em Paralelo (mais rápido)**

```bash
# Maven Surefire com paralelo
mvn test -DparallelTestClass=4

# Tempo: ~12 segundos vs ~30 segundos
```

### **4. Revisar Coverage**

```bash
# Só testes acima de 80% coverage
mvn test jacoco:report jacoco:check \
  -Djacoco.check.lineRate=0.80

# Falha se coverage < 80%
```

### **5. Testes em CI/CD**

```yaml
# .github/workflows/tests.yml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - uses: actions/setup-java@v2
        with:
          java-version: '21'
      - run: mvn clean test jacoco:report
      - uses: codecov/codecov-action@v2
        with:
          files: ./target/site/jacoco/jacoco.xml
```

---

## 📈 Resultados Esperados

### **CommandBusTest**
```
✅ testExecuteCommandSuccess
✅ testExecuteCommandWithoutHandler
✅ testExecuteCommandHandlerException
✅ testCommandValidation
```

### **QueryBusTest**
```
✅ testExecuteGetUserQuery
✅ testExecuteListActiveUsersQuery
✅ testExecuteQueryWithoutHandler
✅ testQueryUserNotFound
✅ testQueryEmptyList
✅ testQueryHandlerException
```

### **SpecificationPatternTest**
```
✅ testStrongPasswordValid
✅ testStrongPasswordTooShort
✅ testStrongPasswordNoNumbers
✅ testStrongPasswordNoUppercase
✅ testStrongPasswordNoLowercase
✅ testStrongPasswordNoSpecialChars
✅ testValidEmailCorrect
✅ testValidEmailInvalid
✅ testValidEmailNull
✅ testCompositionAndBothSatisfied
✅ testCompositionAndOneFails
✅ testCompositionOrOneSuccess
✅ testCompositionOrBothFail
✅ testCompositionNot
```

### **Performance Benchmarks**
```
=== Query Performance Benchmark ===
Sem cache:    1024.50 ms (1000 queries)
Com cache:    102.45 ms (1000 queries)
Melhoria:     10.0x

=== Complex Query Performance Benchmark ===
Sem cache:    1500.00 ms
Com cache:    50.00 ms
Melhoria:     30.0x

=== Circuit Breaker Performance Benchmark ===
Timeout:      50000.00 ms
Fallback:     100.00 ms
Melhoria:     500.0x
```

---

## 🔧 Troubleshooting

### **Problema: "Test not found"**
```bash
# Solução 1: Verificar nome do teste
mvn test -Dtest=CommandBusTest

# Solução 2: Limpar cache Maven
mvn clean test

# Solução 3: Verificar se arquivo existe
find . -name "CommandBusTest.java"
```

### **Problema: "Dependency not found"**
```bash
# Solução: Executar install
mvn clean install -DskipTests
mvn test
```

### **Problema: "Timeout in test"**
```bash
# Aumentar timeout (padrão 30 segundos)
mvn test -DsockConnectTimeout=60000

# Ou para teste específico
@Test(timeout = 10000)  // 10 segundos
void myLongRunningTest() { ... }
```

### **Problema: "Out of memory"**
```bash
# Aumentar heap size
export MAVEN_OPTS="-Xmx1024m"
mvn clean test
```

### **Problema: "Assertion failed unexpectedly"**
```bash
# Debug o teste
mvn test -Dtest=CommandBusTest -e
mvn test -X  # Verbose output
```

---

## ✅ Checklist de Teste

### **Antes de Release**
- [ ] `mvn clean test` passa (80/80)
- [ ] `mvn jacoco:report` mostra 85%+ coverage
- [ ] `mvn verify` inclui testes integração
- [ ] Performance benchmarks validam melhorias
- [ ] Nenhum teste é @Disabled

### **Antes de PR**
- [ ] Adicionar teste para novo código
- [ ] Coverage não diminui
- [ ] Todos testes passam localmente
- [ ] Push para CI/CD e validar

### **Documentação**
- [ ] README menciona como rodar testes
- [ ] Guia de contribuição documenta teste
- [ ] Performance expectations documentadas

---

## 📚 Recursos Adicionais

### **JUnit 5**
- Official: https://junit.org/junit5/
- Annotations: `@Test`, `@DisplayName`, `@BeforeEach`

### **Mockito**
- Official: https://site.mockito.org/
- Syntax: `when()`, `verify()`, `spy()`

### **AssertJ** (alternativa a JUnit assertions)
```java
import static org.assertj.core.api.Assertions.*;

assertThat(result)
  .isNotNull()
  .hasSize(2)
  .contains("user1", "user2");
```

### **Arquillian** (integration testing)
```xml
<dependency>
  <groupId>org.jboss.arquillian.junit</groupId>
  <artifactId>arquillian-junit-container</artifactId>
</dependency>
```

---

## 🚀 Próximos Passos

### **1. Imediato**
```bash
mvn clean test
# Validar 80/80 testes passam
```

### **2. CI/CD**
```bash
# Integrar com GitHub Actions / GitLab CI
# Rodar testes em cada push
```

### **3. Coverage**
```bash
mvn jacoco:report
# Manter acima de 85%
```

### **4. Load Testing**
```bash
# JMeter para teste de carga
# Apache Bench para endpoints
```

### **5. Mutation Testing**
```bash
mvn org.pitest:pitest-maven:mutationCoverage
# Validar testes kilam mutantes
```

---

## 🎓 Exemplo Completo de Execução

```bash
#!/bin/bash
# test-suite.sh

echo "🧪 Iniciando Suite Completa de Testes"
echo "====================================="

# 1. Limpar e Build
echo "📦 Build..."
mvn clean install -DskipTests -q

# 2. Testes Unitários
echo "🧪 Testes Unitários..."
mvn test -q

# 3. Coverage
echo "📊 Gerando Coverage..."
mvn jacoco:report -q

# 4. Relatórios
echo "📄 Testes SonarQube..."
mvn sonar:sonar \
  -Dsonar.projectKey=reporte-ai \
  -Dsonar.host.url=http://localhost:9000 -q

echo ""
echo "✅ Suite Completa Finalizada!"
echo "📊 Relatórios:"
echo "   - JaCoCo:  target/site/jacoco/index.html"
echo "   - Surefire: target/surefire-reports/"
echo "   - SonarQube: http://localhost:9000"
```

**Executar:**
```bash
chmod +x test-suite.sh
./test-suite.sh
```

---

## 📞 Suporte

**Em caso de problemas:**

1. Verificar se Java 21 está instalado: `java -version`
2. Verificar se Maven está atualizado: `mvn -version`
3. Limpar cache: `mvn clean`
4. Executar com verbose: `mvn test -X`
5. Revisar logs em `target/surefire-reports/`

---

**Status:** ✅ PRONTO PARA TESTES  
**Testes:** 80/80  
**Cobertura Target:** 85%+  
**Tempo Esperado:** ~30 segundos

