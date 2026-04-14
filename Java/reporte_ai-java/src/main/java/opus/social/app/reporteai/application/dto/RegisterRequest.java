package opus.social.app.reporteai.application.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;

/**
 * DTO para requisição de registro de novo usuário
 * Implementa validações rigorosas de segurança
 */
public class RegisterRequest {

    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50, message = "Username deve ter entre 3 e 50 caracteres")
    @Pattern(
        regexp = "^[a-zA-Z0-9._-]+$",
        message = "Username contém caracteres inválidos. Apenas letras, números, ponto, underscore e hífen são permitidos"
    )
    private String username;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    @Size(max = 255, message = "Email não pode exceder 255 caracteres")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 12, max = 128, message = "Senha deve ter entre 12 e 128 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!*()\\[\\]{};:'\",.<>?/\\\\|`~-])(?=\\S+$).*$",
        message = "Senha deve conter: números, letras minúsculas, maiúsculas e símbolos especiais (@#$%^&+=!*[]{})"
    )
    private String password;

    @NotBlank(message = "Confirmação de senha é obrigatória")
    private String passwordConfirm;

    @NotBlank(message = "Nome completo é obrigatório")
    @Size(min = 3, max = 255, message = "Nome deve ter entre 3 e 255 caracteres")
    private String fullName;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String passwordConfirm, String fullName) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.passwordConfirm = passwordConfirm;
        this.fullName = fullName;
    }

    // Validação customizada: senhas devem corresponder
    @AssertTrue(
        message = "Senhas não correspondem",
        groups = {Default.class}
    )
    public boolean isPasswordMatching() {
        if (password == null || passwordConfirm == null) {
            return false;
        }
        return password.equals(passwordConfirm);
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPasswordConfirm() { return passwordConfirm; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
}
