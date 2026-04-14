package opus.social.app.reporteai.adapters.http.exception;

import opus.social.app.reporteai.domain.exception.BusinessException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.factory.exception.ExceptionResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Tratador global de exceções para a API REST
 * Atualizado para usar Factory + Registry Pattern
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ExceptionResponseFactory exceptionResponseFactory;

    public GlobalExceptionHandler(ExceptionResponseFactory exceptionResponseFactory) {
        this.exceptionResponseFactory = exceptionResponseFactory;
    }

    /**
     * Trata exceções de funcionário não encontrado
     */
    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        return exceptionResponseFactory.createResponse(ex);
    }

    /**
     * Trata exceções de dados duplicados
     */
    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Object> handleDuplicateDataException(DuplicateDataException ex) {
        return exceptionResponseFactory.createResponse(ex);
    }

    /**
     * Trata exceções de negócio genéricas
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        return exceptionResponseFactory.createResponse(ex);
    }

    /**
     * Trata erros de validação de entrada
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException ex) {
        return exceptionResponseFactory.createValidationErrorResponse(ex);
    }

    /**
     * Trata exceções genéricas não tratadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex) {
        // Log completo apenas no servidor (com stack trace)
        logger.error("Unhandled exception occurred", ex);

        // Retorna erro genérico ao cliente (sem detalhes)
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(exceptionResponseFactory.createGenericErrorResponse(
                "INTERNAL_ERROR",
                "Ocorreu um erro interno. Contate o suporte se o problema persistir."
            ));
    }
}