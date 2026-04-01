package opus.social.app.reporteai.domain.factory.exception;

import org.springframework.http.ResponseEntity;

public interface ExceptionHandlerStrategy<T extends Exception> {
    ResponseEntity<Object> handle(T exception);
    Class<T> getExceptionType();
    String getHandlerName();
}