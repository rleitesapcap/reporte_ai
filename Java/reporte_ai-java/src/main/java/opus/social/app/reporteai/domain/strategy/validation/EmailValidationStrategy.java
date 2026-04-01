package opus.social.app.reporteai.domain.strategy.validation;

import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.port.EmployeeRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class EmailValidationStrategy implements ValidationStrategy<String> {

    private final EmployeeRepositoryPort employeeRepository;

    public EmailValidationStrategy(EmployeeRepositoryPort employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void validate(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }

        if (!isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Formato de email inválido");
        }

        if (employeeRepository.findByEmail(email).isPresent()) {
            throw new DuplicateDataException("email", email);
        }
    }

    @Override
    public String getValidationType() {
        return "EMAIL";
    }

    private boolean isValidEmailFormat(String email) {
        return email.contains("@") && email.contains(".");
    }
}