package opus.social.app.reporteai.domain.event;

import java.util.UUID;

/**
 * Evento disparado quando a senha de um usuário é alterada
 * Event Sourcing - Marca mudança de segurança crítica
 */
public class UserPasswordChangedEvent extends DomainEvent {
    private final String username;
    private final String changedBy;

    public UserPasswordChangedEvent(UUID userId, String username, String changedBy) {
        super(userId);
        this.username = username;
        this.changedBy = changedBy;
    }

    public String getUsername() {
        return username;
    }

    public String getChangedBy() {
        return changedBy;
    }

    @Override
    public String getDescription() {
        return String.format("Senha alterada para usuário: %s (alterado por: %s)", username, changedBy);
    }
}
