package opus.social.app.reporteai.domain.exception;

public class EmployeeNotFoundException extends BusinessException {

    public EmployeeNotFoundException(Long id) {
        super("Funcionário com ID " + id + " não encontrado");
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}