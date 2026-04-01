package opus.social.app.reporteai.domain.factory.exception;

import opus.social.app.reporteai.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class BusinessExceptionHandlerStrategy implements ExceptionHandlerStrategy<BusinessException> {

    @Override
    public ResponseEntity<Object> handle(BusinessException exception) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Business Rule Violation");
        errorResponse.put("message", exception.getMessage());
        errorResponse.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Class<BusinessException> getExceptionType() {
        return BusinessException.class;
    }

    @Override
    public String getHandlerName() {
        return "BusinessExceptionHandler";
    }
}