package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * Serviço centralizado para validação de entrada
 * Implementa regras de validação consistentes em toda a aplicação
 */
@Service
public class InputValidationService {

    // Padrões de validação
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern USERNAME_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._-]{3,50}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^[+]?[0-9]{10,15}$"
    );

    private static final Pattern URL_PATTERN = Pattern.compile(
        "^https?://[A-Za-z0-9+_.-]+\\.[A-Za-z]{2,}[A-Za-z0-9+_./:-]*$"
    );

    private static final Pattern SAFE_STRING_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9\\s\\-._@()]+$"
    );

    private static final int MAX_STRING_LENGTH = 1000;
    private static final int MAX_TEXT_LENGTH = 10000;
    private static final int MIN_PASSWORD_LENGTH = 12;
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * Valida um email
     */
    public void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BusinessException("Email é obrigatório");
        }
        if (email.length() > 255) {
            throw new BusinessException("Email não pode exceder 255 caracteres");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException("Email inválido");
        }
    }

    /**
     * Valida um username
     */
    public void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new BusinessException("Username é obrigatório");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new BusinessException("Username deve ter entre 3 e 50 caracteres, contendo apenas letras, números, ponto, underscore e hífen");
        }
    }

    /**
     * Valida força de senha
     */
    public void validatePasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            throw new BusinessException("Senha é obrigatória");
        }
        if (password.length() < MIN_PASSWORD_LENGTH || password.length() > MAX_PASSWORD_LENGTH) {
            throw new BusinessException("Senha deve ter entre 12 e 128 caracteres");
        }

        // Verificar requisitos de complexidade
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[@#$%^&+=!*()\\[\\]{};:'\",.<>?/\\\\|`~-].*");

        if (!hasUpperCase || !hasLowerCase || !hasDigit || !hasSpecial) {
            throw new BusinessException(
                "Senha deve conter: letras maiúsculas, minúsculas, números e símbolos especiais"
            );
        }
    }

    /**
     * Valida um número de telefone
     */
    public void validatePhoneNumber(String phone) {
        if (phone != null && !phone.isEmpty()) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                throw new BusinessException("Número de telefone inválido");
            }
        }
    }

    /**
     * Valida uma URL
     */
    public void validateUrl(String url) {
        if (url != null && !url.isEmpty()) {
            if (!URL_PATTERN.matcher(url).matches()) {
                throw new BusinessException("URL inválida");
            }
            if (url.length() > 2000) {
                throw new BusinessException("URL não pode exceder 2000 caracteres");
            }
        }
    }

    /**
     * Valida uma string de texto simples (sem caracteres especiais)
     */
    public void validateSafeString(String value, String fieldName, int maxLength) {
        if (value != null && !value.isEmpty()) {
            if (value.length() > maxLength) {
                throw new BusinessException(fieldName + " não pode exceder " + maxLength + " caracteres");
            }
            if (!SAFE_STRING_PATTERN.matcher(value).matches()) {
                throw new BusinessException(fieldName + " contém caracteres não permitidos");
            }
        }
    }

    /**
     * Valida um nome (permite caracteres básicos)
     */
    public void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }
        if (name.length() < 3 || name.length() > 255) {
            throw new BusinessException("Nome deve ter entre 3 e 255 caracteres");
        }
        if (!name.matches("^[a-zA-Z\\s'-]+$")) {
            throw new BusinessException("Nome contém caracteres inválidos");
        }
    }

    /**
     * Valida uma descrição/texto longo
     */
    public void validateText(String text, String fieldName) {
        if (text != null && text.length() > MAX_TEXT_LENGTH) {
            throw new BusinessException(fieldName + " não pode exceder " + MAX_TEXT_LENGTH + " caracteres");
        }
    }

    /**
     * Valida um ID (UUID ou número)
     */
    public void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new BusinessException("ID é obrigatório");
        }
        // Valida UUID pattern (com ou sem hífen)
        if (!id.matches("^[0-9a-f]{8}-?[0-9a-f]{4}-?[0-9a-f]{4}-?[0-9a-f]{4}-?[0-9a-f]{12}$")) {
            // Se não for UUID, tenta validar como número
            if (!id.matches("^[0-9]+$")) {
                throw new BusinessException("ID inválido");
            }
        }
    }

    /**
     * Valida um número positivo
     */
    public void validatePositiveNumber(Number number, String fieldName) {
        if (number == null) {
            throw new BusinessException(fieldName + " é obrigatório");
        }
        if (number.doubleValue() <= 0) {
            throw new BusinessException(fieldName + " deve ser um número positivo");
        }
    }

    /**
     * Valida um intervalo numérico
     */
    public void validateNumberRange(Number number, Number min, Number max, String fieldName) {
        if (number == null) {
            throw new BusinessException(fieldName + " é obrigatório");
        }
        double value = number.doubleValue();
        double minVal = min.doubleValue();
        double maxVal = max.doubleValue();

        if (value < minVal || value > maxVal) {
            throw new BusinessException(fieldName + " deve estar entre " + minVal + " e " + maxVal);
        }
    }

    /**
     * Sanitiza uma string removendo caracteres perigosos (previne XSS)
     */
    public String sanitizeString(String input) {
        if (input == null) {
            return null;
        }
        return input
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
            .replace("&", "&amp;");
    }

    /**
     * Valida ausência de SQL injection patterns
     */
    public void validateNoSqlInjection(String input) {
        if (input == null) {
            return;
        }
        String lowerInput = input.toLowerCase();
        String[] sqlPatterns = {
            "'; drop", "'; delete", "'; update", "'; insert",
            "union select", "or 1=1", "or '1'='1", "--", "/*", "*/"
        };

        for (String pattern : sqlPatterns) {
            if (lowerInput.contains(pattern)) {
                throw new BusinessException("Entrada contém padrões suspeitos de SQL injection");
            }
        }
    }

    /**
     * Valida ausência de XSS patterns
     */
    public void validateNoXss(String input) {
        if (input == null) {
            return;
        }
        String lowerInput = input.toLowerCase();
        String[] xssPatterns = {
            "<script", "javascript:", "onerror=", "onload=", "onclick=",
            "alert(", "eval(", "expression("
        };

        for (String pattern : xssPatterns) {
            if (lowerInput.contains(pattern)) {
                throw new BusinessException("Entrada contém padrões suspeitos de XSS");
            }
        }
    }
}
