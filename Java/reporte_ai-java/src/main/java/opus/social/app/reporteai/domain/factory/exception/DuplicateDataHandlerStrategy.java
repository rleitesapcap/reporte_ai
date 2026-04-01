package opus.social.app.reporteai.domain.factory.exception;

import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class DuplicateDataHandlerStrategy implements ExceptionHandlerStrategy<DuplicateDataException> {

    @Override
    public ResponseEntity<Object> handle(DuplicateDataException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("error", "Duplicate Data");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<DuplicateDataException> getExceptionType() {
        return DuplicateDataException.class;
    }

    @Override
    public String getHandlerName() {
        return "DuplicateDataHandler";
    }
}