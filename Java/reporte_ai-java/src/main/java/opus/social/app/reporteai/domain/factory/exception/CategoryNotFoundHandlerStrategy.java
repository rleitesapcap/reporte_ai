package opus.social.app.reporteai.domain.factory.exception;

import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class CategoryNotFoundHandlerStrategy implements ExceptionHandlerStrategy {

    @Override
    public boolean supports(Exception exception) {
        return exception instanceof EmployeeNotFoundException
            && exception.getMessage().toLowerCase().contains("category");
    }

    @Override
    public ResponseEntity<Map<String, Object>> handle(Exception exception) {
        Map<String, Object> response = Map.of(
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.NOT_FOUND.value(),
            "error", "Not Found",
            "message", exception.getMessage(),
            "errorType", "CATEGORY_NOT_FOUND"
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
}
