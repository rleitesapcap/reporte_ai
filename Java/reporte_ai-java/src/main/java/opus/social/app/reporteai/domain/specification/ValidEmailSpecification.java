package opus.social.app.reporteai.domain.specification;

import java.util.regex.Pattern;

/**
 * Especificação que valida formato de email
 * Specification Pattern - Validação de formato
 */
public class ValidEmailSpecification extends Specification<String> {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
    );

    @Override
    public boolean isSatisfiedBy(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public String getDescription() {
        return "Email deve estar em formato válido (exemplo@dominio.com)";
    }
}
