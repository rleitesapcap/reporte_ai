package opus.social.app.reporteai.domain.event;

import java.util.UUID;

/**
 * Evento disparado quando um usuário realiza login bem-sucedido
 * Event Sourcing - Marca atividade de autenticação
 */
public class UserLoginEvent extends DomainEvent {
    private final String username;
    private final String ipAddress;

    public UserLoginEvent(UUID userId, String username, String ipAddress) {
        super(userId);
        this.username = username;
        this.ipAddress = ipAddress;
    }

    public String getUsername() {
        return username;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    @Override
    public String getDescription() {
        return String.format("Login bem-sucedido: %s (IP: %s)", username, ipAddress);
    }
}
