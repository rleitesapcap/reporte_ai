package opus.social.app.reporteai.domain.strategy.validation;

import opus.social.app.reporteai.domain.exception.DomainException;

public interface ValidationStrategy<T> {
    void validate(T value) throws DomainException;
    String getValidationType();
}