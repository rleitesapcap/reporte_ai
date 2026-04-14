package opus.social.app.reporteai.application.commandhandler;

import opus.social.app.reporteai.application.bus.CommandHandler;
import opus.social.app.reporteai.application.command.ChangePasswordCommand;
import opus.social.app.reporteai.application.service.AuthUserApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler do Comando ChangePasswordCommand
 * CQRS Pattern - Responsável pela execução da mudança de senha
 */
@Component
@Transactional
public class ChangePasswordCommandHandler implements CommandHandler<ChangePasswordCommand> {
    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordCommandHandler.class);

    private final AuthUserApplicationService authUserService;

    public ChangePasswordCommandHandler(AuthUserApplicationService authUserService) {
        this.authUserService = authUserService;
    }

    @Override
    public void handle(ChangePasswordCommand command) throws Exception {
        logger.info("Processando comando ChangePassword para username: {}", command.getUsername());

        authUserService.updatePassword(command.getUsername(), command.getNewPassword());

        logger.info("Senha alterada com sucesso para: {}", command.getUsername());
    }
}
