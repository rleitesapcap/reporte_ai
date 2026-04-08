package opus.social.app.reporteai.domain.factory.exception;

import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CategoryNotFoundHandlerStrategy implements ExceptionHandlerStrategy<EmployeeNotFoundException> {

    @Override
    public ResponseEntity<Object> handle(EmployeeNotFoundException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Category Not Found");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<EmployeeNotFoundException> getExceptionType() {
        return EmployeeNotFoundException.class;
    }

    @Override
    public String getHandlerName() {
        return "CategoryNotFoundHandler";
    }
}
