package opus.social.app.reporteai.domain.strategy.validation;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ValidationContext {

    private final Map<String, ValidationStrategy<String>> strategies;

    public ValidationContext(List<ValidationStrategy<String>> strategies) {
        this.strategies = new HashMap<>();
        for (ValidationStrategy<String> strategy : strategies) {
            this.strategies.put(strategy.getValidationType(), strategy);
        }
    }

    public void validateEmail(String email) {
        ValidationStrategy<String> strategy = strategies.get("EMAIL");
        if (strategy != null) {
            strategy.validate(email);
        }
    }

    public void validateCpf(String cpf) {
        ValidationStrategy<String> strategy = strategies.get("CPF");
        if (strategy != null) {
            strategy.validate(cpf);
        }
    }

    public void validate(String type, String value) {
        ValidationStrategy<String> strategy = strategies.get(type.toUpperCase());
        if (strategy != null) {
            strategy.validate(value);
        } else {
            throw new IllegalArgumentException("Tipo de validação não suportado: " + type);
        }
    }
}