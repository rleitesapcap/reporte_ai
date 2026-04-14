package opus.social.app.reporteai.application.command;

import opus.social.app.reporteai.application.bus.Command;

/**
 * Comando para alterar senha do usuário
 * CQRS Pattern - Representa uma intenção de escrita
 */
public class ChangePasswordCommand implements Command {
    private final String username;
    private final String newPassword;

    public ChangePasswordCommand(String username, String newPassword) {
        this.username = username;
        this.newPassword = newPassword;
    }

    public String getUsername() {
        return username;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
