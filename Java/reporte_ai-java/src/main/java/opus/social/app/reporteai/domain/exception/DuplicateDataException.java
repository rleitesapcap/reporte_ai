package opus.social.app.reporteai.domain.exception;

public class DuplicateDataException extends BusinessException {

    public DuplicateDataException(String field, String value) {
        super("Já existe um funcionário cadastrado com " + field + ": " + value);
    }

    public DuplicateDataException(String message) {
        super(message);
    }
}