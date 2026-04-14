package opus.social.app.reporteai.domain.specification;

import java.util.regex.Pattern;

/**
 * Especificação que valida força de senha
 * Specification Pattern - Regra de negócio encapsulada
 *
 * Requerimentos:
 * - Mínimo 12 caracteres
 * - Pelo menos um número
 * - Pelo menos uma letra maiúscula
 * - Pelo menos uma letra minúscula
 * - Pelo menos um símbolo especial
 */
public class StrongPasswordSpecification extends Specification<String> {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])" +                    // Requer número
        "(?=.*[a-z])" +                     // Requer minúscula
        "(?=.*[A-Z])" +                     // Requer maiúscula
        "(?=.*[@#$%^&+=!*()\\[\\]{};:'\",.<>?/\\\\|`~-])" +  // Requer símbolo
        "(?=\\S+$)" +                       // Sem espaços
        ".{12,}$"                           // Mínimo 12 caracteres
    );

    @Override
    public boolean isSatisfiedBy(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public String getDescription() {
        return "Senha deve conter: " +
            "mínimo 12 caracteres, " +
            "números, " +
            "letras maiúsculas e minúsculas, " +
            "e símbolos especiais";
    }
}
