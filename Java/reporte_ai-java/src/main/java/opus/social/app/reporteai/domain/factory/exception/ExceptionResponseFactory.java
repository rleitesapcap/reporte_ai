package opus.social.app.reporteai.domain.factory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class ExceptionResponseFactory {

    private final ExceptionHandlerRegistry registry;

    public ExceptionResponseFactory(ExceptionHandlerRegistry registry) {
        this.registry = registry;
    }

    public <T extends Exception> ResponseEntity<Object> createResponse(T exception) {
        return registry.getHandler(exception)
                .map(handler -> handler.handle(exception))
                .orElse(createDefaultErrorResponse(exception));
    }

    public ResponseEntity<Object> createValidationErrorResponse(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Validation Failed");
        errorResponse.put("message", "Erro na validação dos dados");
        errorResponse.put("fieldErrors", errors);
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    public Map<String, Object> createGenericErrorResponse(String errorCode, String message) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("timestamp", LocalDateTime.now());
        return errorResponse;
    }

    private ResponseEntity<Object> createDefaultErrorResponse(Exception exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", "Erro interno do servidor");
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}