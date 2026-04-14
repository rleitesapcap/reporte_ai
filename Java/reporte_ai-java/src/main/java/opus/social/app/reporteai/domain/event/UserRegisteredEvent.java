package opus.social.app.reporteai.domain.event;

import java.util.UUID;

/**
 * Evento disparado quando um novo usuário é registrado
 * Event Sourcing - Marca o ponto histórico de criação do usuário
 */
public class UserRegisteredEvent extends DomainEvent {
    private final String username;
    private final String email;
    private final String fullName;

    public UserRegisteredEvent(UUID userId, String username, String email, String fullName) {
        super(userId);
        this.username = username;
        this.email = email;
        this.fullName = fullName;
    }

    // ===== Getters =====
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String getDescription() {
        return String.format("Usuário registrado: %s (%s) [%s]", username, email, fullName);
    }
}
