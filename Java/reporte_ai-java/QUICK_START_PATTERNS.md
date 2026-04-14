# Quick Start - Design Patterns na Prática

**Para Desenvolvedores:** Como usar os padrões no dia a dia

---

## 🎯 1. Como Adicionar um Novo Caso de Uso (Command)

### Cenário: Desativar usuário

**Passo 1: Criar o Command**
```java
// src/main/java/.../command/DeactivateUserCommand.java
public class DeactivateUserCommand implements Command {
    private final String username;
    private final String reason;

    public DeactivateUserCommand(String username, String reason) {
        this.username = username;
        this.reason = reason;
    }

    public String getUsername() { return username; }
    public String getReason() { return reason; }
}
```

**Passo 2: Criar o CommandHandler**
```java
// src/main/java/.../commandhandler/DeactivateUserCommandHandler.java
@Component
@Transactional
public class DeactivateUserCommandHandler implements CommandHandler<DeactivateUserCommand> {
    
    private final AuthUserApplicationService authService;
    private final AuditLogApplicationService auditService;

    @Override
    public void handle(DeactivateUserCommand command) throws Exception {
        // Implementar lógica
        authService.deactivateUser(command.getUsername());
        
        // Auditoria automática
        auditService.logSecurityIncident(
            "USER_DEACTIVATED",
            "Usuário desativado: " + command.getUsername() + 
            " Motivo: " + command.getReason()
        );
    }
}
```

**Passo 3: Usar no Controller**
```java
// No AuthController
@DeleteMapping("/{username}/deactivate")
public ResponseEntity<?> deactivateUser(@PathVariable String username,
                                        @RequestParam String reason) {
    DeactivateUserCommand cmd = new DeactivateUserCommand(username, reason);
    commandBus.execute(cmd);
    return ResponseEntity.ok("Usuário desativado");
}
```

---

## 📖 2. Como Adicionar uma Nova Query (Leitura)

### Cenário: Buscar usuários por data de criação

**Passo 1: Criar a Query**
```java
// src/main/java/.../query/FindUsersByDateQuery.java
public class FindUsersByDateQuery implements Query<List<UserResponseDTO>> {
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    public FindUsersByDateQuery(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
}
```

**Passo 2: Criar o QueryHandler (com Cache)**
```java
// src/main/java/.../queryhandler/FindUsersByDateQueryHandler.java
@Component
public class FindUsersByDateQueryHandler 
    implements QueryHandler<FindUsersByDateQuery, List<UserResponseDTO>> {
    
    private final AuthUserRepository userRepository;

    @Override
    @Cacheable(value = "usersByDate", key = "#query.startDate + '-' + #query.endDate")
    public List<UserResponseDTO> handle(FindUsersByDateQuery query) throws Exception {
        return userRepository.findAllActiveUsers().stream()
            .filter(u -> {
                LocalDateTime created = u.getCreatedAt();
                return created.isAfter(query.getStartDate()) && 
                       created.isBefore(query.getEndDate());
            })
            .map(UserResponseDTO::from)
            .collect(Collectors.toList());
    }
}
```

**Passo 3: Usar no Controller**
```java
@GetMapping("/by-date")
public ResponseEntity<List<UserResponseDTO>> getUsersByDate(
    @RequestParam LocalDateTime startDate,
    @RequestParam LocalDateTime endDate) {
    
    FindUsersByDateQuery query = new FindUsersByDateQuery(startDate, endDate);
    List<UserResponseDTO> users = queryBus.execute(query);
    return ResponseEntity.ok(users);
}
```

---

## 🎪 3. Como Criar um Novo Evento de Domínio

### Cenário: Usuário foi bloqueado

**Passo 1: Criar o Evento**
```java
// src/main/java/.../event/UserLockedEvent.java
public class UserLockedEvent extends DomainEvent {
    private final String username;
    private final String reason;
    private final int failedAttempts;

    public UserLockedEvent(UUID userId, String username, String reason, int failedAttempts) {
        super(userId);
        this.username = username;
        this.reason = reason;
        this.failedAttempts = failedAttempts;
    }

    public String getUsername() { return username; }
    public String getReason() { return reason; }
    public int getFailedAttempts() { return failedAttempts; }

    @Override
    public String getDescription() {
        return String.format("Usuário %s bloqueado: %s (%d tentativas)",
            username, reason, failedAttempts);
    }
}
```

**Passo 2: Publicar o Evento (no Service)**
```java
// No AuthUserApplicationService.recordFailedLoginAttempt()
if (user.getFailedLoginAttempts() >= 5) {
    user.setIsLocked(true);
    user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
    
    // Publicar evento
    UserLockedEvent event = new UserLockedEvent(
        user.getId(),
        user.getUsername(),
        "Múltiplas tentativas de login falhadas",
        user.getFailedLoginAttempts()
    );
    eventPublisher.publish(event);
}
```

**Passo 3: Criar Listener para o Evento**
```java
// src/main/java/.../listener/UserLockedEventListener.java
@Component
public class UserLockedEventListener implements DomainEventListener {
    
    private final AuditLogApplicationService auditService;

    @Override
    @Async
    public void handle(DomainEvent event) throws Exception {
        if (event instanceof UserLockedEvent) {
            UserLockedEvent lockedEvent = (UserLockedEvent) event;
            auditService.logAccountLock(
                lockedEvent.getUsername(),
                lockedEvent.getDescription()
            );
            // Poderia enviar notificação, etc.
        }
    }

    @Override
    public boolean canHandle(DomainEvent event) {
        return event instanceof UserLockedEvent;
    }
}
```

---

## ✔️ 4. Como Criar uma Especificação de Validação

### Cenário: Validar que username não contém espaços

**Passo 1: Criar a Specification**
```java
// src/main/java/.../specification/NoSpacesUsernameSpecification.java
public class NoSpacesUsernameSpecification extends Specification<String> {
    
    @Override
    public boolean isSatisfiedBy(String username) {
        if (username == null || username.isEmpty()) {
            return false;
        }
        return !username.contains(" ");
    }

    @Override
    public String getDescription() {
        return "Username não pode conter espaços";
    }
}
```

**Passo 2: Usar a Specification (Com Composição)**
```java
// Validar múltiplas regras
Specification<String> validUsername = 
    new UniqueUsernameSpecification(repo)
        .and(new NoSpacesUsernameSpecification())
        .and(new ValidEmailSpecification());

if (!validUsername.isSatisfiedBy(username)) {
    throw new ValidationException(validUsername.getDescription());
}
```

---

## 🛡️ 5. Como Usar Circuit Breaker

### Cenário: Chamar API externa com fallback

```java
// Onde quer que precise chamar serviço externo
public void notifyUser(String email, String message) {
    String result = circuitBreakerService.executeWithFallback(
        "externalNotificationAPI",
        () -> {
            // Tenta chamar API
            return externalApiClient.send(email, message);
        },
        "Notificação agendada para envio posterior"  // Fallback
    );
    
    logger.info("Resultado: {}", result);
}

// Ou sem fallback (lança exceção se falhar)
try {
    circuitBreakerService.executeWithCircuitBreaker(
        "externalNotificationAPI",
        () -> externalApiClient.send(email, message)
    );
} catch (RuntimeException e) {
    if ("Serviço indisponível".equals(e.getMessage())) {
        // Circuit breaker aberto
        logger.warn("Serviço indisponível, tentando novamente depois");
    }
}
```

---

## 🎭 6. Como Criar uma Saga

### Cenário: Saga para criação de relatório com PDF

```java
// src/main/java/.../saga/ReportGenerationSaga.java
@Component
public class ReportGenerationSaga {
    
    private final ReportService reportService;
    private final PdfGeneratorService pdfService;
    private final EmailService emailService;
    private final AuditLogApplicationService auditService;

    @Transactional
    public void executeReportGeneration(ReportGenerationRequest request) {
        logger.info("Iniciando saga de geração de relatório");

        try {
            // PASSO 1: Criar estrutura do relatório
            Report report = reportService.createReport(request);
            
            // PASSO 2: Gerar PDF (com fallback)
            try {
                byte[] pdf = pdfService.generatePdf(report);
                report.setPdfContent(pdf);
                reportService.update(report);
            } catch (Exception ex) {
                // Compensação: continuar sem PDF
                logger.warn("Falha ao gerar PDF, continuando sem");
                auditService.logSecurityIncident(
                    "PDF_GENERATION_FAILED",
                    "Relatório criado mas PDF falhou: " + request.getTitle()
                );
            }
            
            // PASSO 3: Enviar email (com fallback)
            try {
                emailService.sendReport(request.getEmailRecipient(), report);
            } catch (Exception ex) {
                logger.warn("Falha ao enviar email, relatório disponível para download");
            }
            
            // PASSO 4: Auditoria
            auditService.logAuditEvent(
                AuditEventType.DATA_EXPORT,
                "REPORT",
                request.getCreatedBy(),
                "Relatório gerado: " + request.getTitle()
            );
            
            logger.info("Saga completada com sucesso");
            
        } catch (BusinessException ex) {
            compensate(request);
            throw ex;
        }
    }

    @Transactional
    private void compensate(ReportGenerationRequest request) {
        logger.warn("Executando compensação para relatório: {}", request.getTitle());
        // Deletar arquivos temporários
        // Registrar falha
        // etc.
    }
}
```

---

## 💾 7. Como Usar Cache

### Scenario: Cachear dados de user

```java
// Automaticamente cachea
@Cacheable(value = "users", key = "#username")
public UserResponseDTO getUserByUsername(String username) {
    // Primeira chamada: executa a lógica
    // Chamadas subsequentes: retorna do cache
    return userRepository.findByUsername(username)
        .map(UserResponseDTO::from)
        .orElseThrow();
}

// Invalidar cache ao atualizar
@CacheEvict(value = "users", key = "#username")
public void updateUser(String username, UserUpdateRequest dto) {
    // Executa atualização
    // Cache é limpo automaticamente
}

// Limpar todo o cache de users
@CacheEvict(value = "users", allEntries = true)
public void clearAllUserCache() {
    logger.info("Cache de users limpado");
}

// Usar em Controller
@GetMapping("/{username}")
public ResponseEntity<UserResponseDTO> getUser(@PathVariable String username) {
    // Primeira requisição: ~50ms (DB)
    // Requisições subsequentes: ~5ms (Cache)
    UserResponseDTO user = getUserByUsername(username);
    return ResponseEntity.ok(user);
}
```

---

## 📊 8. Fluxo Completo: Mudança de Senha

```java
// ===== CONTROLLER =====
@PutMapping("/change-password")
public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest dto) {
    // 1. Criar command
    ChangePasswordCommand cmd = new ChangePasswordCommand(
        getCurrentUsername(),
        dto.getNewPassword()
    );
    
    // 2. Enviar para CommandBus
    commandBus.execute(cmd);
    
    return ResponseEntity.ok("Senha alterada com sucesso");
}

// ===== COMMAND HANDLER =====
@Component
public class ChangePasswordCommandHandler implements CommandHandler<ChangePasswordCommand> {
    
    @Override
    @Transactional
    public void handle(ChangePasswordCommand command) throws Exception {
        authService.updatePassword(
            command.getUsername(),
            command.getNewPassword()
        );
    }
}

// ===== SERVICE (com validação e evento) =====
public void updatePassword(String username, String newPassword) {
    // 1. Validar nova senha (Specification Pattern)
    Specification<String> strongPassword = new StrongPasswordSpecification();
    if (!strongPassword.isSatisfiedBy(newPassword)) {
        throw new BusinessException(strongPassword.getDescription());
    }
    
    // 2. Atualizar no banco
    AuthUserJpaEntity user = getUserByUsername(username);
    user.setPasswordHash(passwordEncoder.encode(newPassword));
    userRepository.save(user);
    
    // 3. Publicar evento (Event Sourcing + Observer)
    UserPasswordChangedEvent event = new UserPasswordChangedEvent(
        user.getId(),
        username,
        getCurrentUser()
    );
    eventPublisher.publish(event);  // Async!
    
    // 4. Invalidar cache
    cacheManager.evict("users:" + username);
}

// ===== EVENT LISTENER (automático, async) =====
@Component
public class AuditLogEventListener implements DomainEventListener {
    
    @Override
    @Async
    public void handle(DomainEvent event) {
        if (event instanceof UserPasswordChangedEvent) {
            UserPasswordChangedEvent evt = (UserPasswordChangedEvent) event;
            auditService.logPasswordChange(evt.getUsername());
            // Registrado no event_store
        }
    }
}

// ===== RESULTADO =====
// 1. Senha alterada no banco ✅
// 2. Evento publicado ✅
// 3. Auditado automaticamente ✅
// 4. Cache invalidado ✅
// 5. Resposta rápida ao cliente (<100ms) ✅
```

---

## 🧪 Testando os Padrões

### Unit Test de Command
```java
@Test
public void testChangePasswordCommand() {
    // Arrange
    ChangePasswordCommand cmd = new ChangePasswordCommand("user1", "NewPass123!@");
    
    // Act
    commandBus.execute(cmd);
    
    // Assert
    AuthUserJpaEntity user = userRepository.findByUsername("user1").get();
    assertTrue(passwordEncoder.matches("NewPass123!@", user.getPasswordHash()));
}
```

### Unit Test de Query
```java
@Test
public void testGetUserQueryWithCache() {
    // Arrange
    GetUserQuery query = new GetUserQuery("user1");
    
    // Act - Primeira chamada
    long start = System.currentTimeMillis();
    UserResponseDTO user1 = queryBus.execute(query);
    long firstCall = System.currentTimeMillis() - start;
    
    // Act - Segunda chamada (do cache)
    start = System.currentTimeMillis();
    UserResponseDTO user2 = queryBus.execute(query);
    long secondCall = System.currentTimeMillis() - start;
    
    // Assert
    assertEquals(user1, user2);
    assertTrue(secondCall < firstCall);  // Cache é mais rápido
}
```

### Unit Test de Specification
```java
@Test
public void testStrongPasswordSpecification() {
    Specification<String> spec = new StrongPasswordSpecification();
    
    assertFalse(spec.isSatisfiedBy("weak"));              // Muito curta
    assertFalse(spec.isSatisfiedBy("NoNumbers!"));         // Sem números
    assertFalse(spec.isSatisfiedBy("nouppercase123!"));   // Sem maiúscula
    assertTrue(spec.isSatisfiedBy("StrongPass123!@"));    // OK
}
```

---

## ⚡ Performance Checklist

Ao usar os padrões, verifique:

- [ ] Queries com `@Cacheable` estão retornando do cache?
- [ ] Commands são executados via CommandBus?
- [ ] Events são publicados após cada mudança de estado?
- [ ] Circuit Breaker está configurado para serviços externos?
- [ ] Specifications estão sendo compostas quando apropriado?
- [ ] Cache é invalidado quando dados mudam?
- [ ] Sagas têm compensações configuradas?

---

## 🚨 Troubleshooting

### "Handler não registrado para Command X"
```
Solução: Verifique se o handler está @Component
        e implementa CommandHandler<MeuCommand>
```

### "Query não está sendo cacheada"
```
Solução: Verifique se @Cacheable tem value e key corretos
        Certifique-se de que CacheConfig foi carregado
        @EnableCaching no SpringBootApplication
```

### "Evento não disparou listener"
```
Solução: Listener deve estar @Component
        e implementar DomainEventListener
        Verifique canHandle() retorna true
        Verifique @Async está habilitado
```

---

**Próximos Passos:**
1. Copie os exemplos acima
2. Crie seus próprios Commands/Queries
3. Adicione Specifications
4. Implemente Listeners
5. Teste com JUnit

Happy Coding! 🚀

