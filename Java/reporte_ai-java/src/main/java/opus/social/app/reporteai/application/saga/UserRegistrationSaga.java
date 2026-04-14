package opus.social.app.reporteai.application.saga;

import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import opus.social.app.reporteai.application.service.AuditLogApplicationService;
import opus.social.app.reporteai.infrastructure.persistence.entity.AuthUserJpaEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Saga para Registro de Usuário
 * Saga Pattern - Orquestra múltiplas operações de negócio
 *
 * Fluxo:
 * 1. Registrar usuário no banco
 * 2. Enviar email de boas-vindas (com compensação em caso de erro)
 * 3. Registrar em auditoria
 *
 * Responsável por tratamento de erros e compensações
 */
@Component
public class UserRegistrationSaga {
    private static final Logger logger = LoggerFactory.getLogger(UserRegistrationSaga.class);

    private final AuthUserApplicationService authUserService;
    private final AuditLogApplicationService auditLogService;

    public UserRegistrationSaga(AuthUserApplicationService authUserService,
                               AuditLogApplicationService auditLogService) {
        this.authUserService = authUserService;
        this.auditLogService = auditLogService;
    }

    /**
     * Executa a saga de registro de usuário
     * Transação englobando todas as operações
     */
    @Transactional
    public AuthUserJpaEntity executeUserRegistration(RegisterRequest request) {
        logger.info("Iniciando saga de registro de usuário: {}", request.getUsername());

        try {
            // PASSO 1: Registrar usuário
            logger.debug("Passo 1: Registrando usuário no banco de dados");
            AuthUserJpaEntity user = authUserService.registerUser(request);
            logger.info("Usuário registrado com sucesso: {}", user.getUsername());

            // PASSO 2: Enviar email (com compensação)
            logger.debug("Passo 2: Enviando email de boas-vindas");
            try {
                sendWelcomeEmail(user);
            } catch (Exception emailEx) {
                // Compensação: Registrar falha mas continuar
                logger.warn("Falha ao enviar email de boas-vindas para: {}. " +
                    "Usuário será registrado mesmo assim.",
                    user.getEmail(), emailEx);

                auditLogService.logSecurityIncident(
                    "EMAIL_SEND_FAILED",
                    String.format("Falha ao enviar email para novo usuário: %s - %s",
                        user.getEmail(),
                        emailEx.getMessage())
                );
                // Não lança exceção - email é não-crítico
            }

            // PASSO 3: Auditoria final
            logger.debug("Passo 3: Registrando em auditoria");
            auditLogService.logUserRegistration(user.getUsername(), user.getEmail());

            logger.info("Saga de registro concluída com sucesso para: {}", user.getUsername());
            return user;

        } catch (Exception ex) {
            logger.error("Erro na saga de registro de usuário: {}", request.getUsername(), ex);

            // Registrar incidente
            auditLogService.logSecurityIncident(
                "USER_REGISTRATION_SAGA_FAILED",
                String.format("Saga de registro falhou para: %s - %s",
                    request.getUsername(),
                    ex.getMessage())
            );

            throw new RuntimeException("Falha ao registrar usuário: " + ex.getMessage(), ex);
        }
    }

    /**
     * Etapa de envio de email (pode falhar sem cancelar o registro)
     */
    private void sendWelcomeEmail(AuthUserJpaEntity user) throws Exception {
        logger.debug("Enviando email de boas-vindas para: {}", user.getEmail());

        // Simulação de envio de email
        // Em produção, integrar com serviço de email real
        String emailContent = String.format(
            "Bem-vindo ao Reporte AI, %s!\n\n" +
            "Sua conta foi criada com sucesso.\n" +
            "Email: %s\n" +
            "Username: %s\n\n" +
            "Acesse: https://reporteai.example.com",
            user.getFullName(),
            user.getEmail(),
            user.getUsername()
        );

        // TODO: Integrar com NotificationService real
        logger.info("Email enviado para: {}", user.getEmail());
    }

    /**
     * Etapa de compensação - Executada em caso de erro crítico
     * Nota: No fluxo atual, a transação será revertida automaticamente
     */
    @Transactional
    public void compensate(String username) {
        logger.warn("Executando compensação para registro de usuário: {}", username);

        try {
            // Deativar usuário em vez de deletar (soft delete)
            authUserService.deactivateUser(username);

            auditLogService.logSecurityIncident(
                "USER_REGISTRATION_COMPENSATED",
                "Registro de usuário desativado durante compensação: " + username
            );

            logger.info("Compensação concluída para: {}", username);
        } catch (Exception ex) {
            logger.error("Erro durante compensação para: {}", username, ex);
        }
    }
}
