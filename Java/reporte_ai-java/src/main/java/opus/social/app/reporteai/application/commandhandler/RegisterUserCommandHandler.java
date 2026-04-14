package opus.social.app.reporteai.application.commandhandler;

import opus.social.app.reporteai.application.bus.CommandHandler;
import opus.social.app.reporteai.application.command.RegisterUserCommand;
import opus.social.app.reporteai.application.dto.RegisterRequest;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler do Comando RegisterUserCommand
 * CQRS Pattern - Responsável pela execução da lógica de registro
 *
 * Responsabilidades:
 * - Converter comando em request de domínio
 * - Invocar serviço de aplicação
 * - Manejar exceções específicas do comando
 */
@Component
@Transactional
public class RegisterUserCommandHandler implements CommandHandler<RegisterUserCommand> {
    private static final Logger logger = LoggerFactory.getLogger(RegisterUserCommandHandler.class);

    private final AuthUserApplicationService authUserService;

    public RegisterUserCommandHandler(AuthUserApplicationService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void handle(RegisterUserCommand command) throws Exception {
        logger.info("Processando comando RegisterUser para username: {}", command.getUsername());

        // Converter comando em DTO de aplicação
        RegisterRequest request = new RegisterRequest(
            command.getUsername(),
            command.getEmail(),
            command.getPassword(),
            command.getPassword(),  // passwordConfirmation
            command.getFullName()
        );

        // Executar lógica de registro
        authUserService.registerUser(request);

        logger.info("Usuário registrado com sucesso: {}", command.getUsername());
    }
}
