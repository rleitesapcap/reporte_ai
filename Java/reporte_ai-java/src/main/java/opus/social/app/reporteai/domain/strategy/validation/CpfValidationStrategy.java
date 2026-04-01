package opus.social.app.reporteai.domain.strategy.validation;

import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.port.EmployeeRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class CpfValidationStrategy implements ValidationStrategy<String> {

    private final EmployeeRepositoryPort employeeRepository;

    public CpfValidationStrategy(EmployeeRepositoryPort employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void validate(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF não pode ser vazio");
        }

        String cleanCpf = cpf.replaceAll("\\D", "");

        if (!isValidCpfFormat(cleanCpf)) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos");
        }

        if (employeeRepository.findByCpf(cpf).isPresent()) {
            throw new DuplicateDataException("CPF", cpf);
        }
    }

    @Override
    public String getValidationType() {
        return "CPF";
    }

    private boolean isValidCpfFormat(String cpf) {
        return cpf.length() == 11 && !cpf.matches("(\\d)\\1{10}");
    }
}