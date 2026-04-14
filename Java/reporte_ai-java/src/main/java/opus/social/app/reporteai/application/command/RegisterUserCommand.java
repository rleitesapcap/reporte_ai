package opus.social.app.reporteai.application.command;

import opus.social.app.reporteai.application.bus.Command;

/**
 * Comando para registrar novo usuário
 * CQRS Pattern - Representa uma intenção de escrita no sistema
 */
public class RegisterUserCommand implements Command {
    private final String username;
    private final String email;
    private final String password;
    private final String fullName;

    private RegisterUserCommand(Builder builder) {
        this.username = builder.username;
        this.email = builder.email;
        this.password = builder.password;
        this.fullName = builder.fullName;
    }

    // ===== Getters =====
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getFullName() {
        return fullName;
    }

    // ===== Builder =====
    public static class Builder {
        private String username;
        private String email;
        private String password;
        private String fullName;

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public RegisterUserCommand build() {
            return new RegisterUserCommand(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
