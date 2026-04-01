package opus.social.app.reporteai.domain.factory.exception;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class ExceptionHandlerRegistry {

    private final Map<Class<? extends Exception>, ExceptionHandlerStrategy<? extends Exception>> handlers;

    public ExceptionHandlerRegistry(List<ExceptionHandlerStrategy<? extends Exception>> strategies) {
        this.handlers = new HashMap<>();
        for (ExceptionHandlerStrategy<? extends Exception> strategy : strategies) {
            handlers.put(strategy.getExceptionType(), strategy);
        }
    }

    @SuppressWarnings("unchecked")
    public <T extends Exception> Optional<ExceptionHandlerStrategy<T>> getHandler(Class<T> exceptionType) {
        ExceptionHandlerStrategy<? extends Exception> handler = handlers.get(exceptionType);
        return Optional.ofNullable((ExceptionHandlerStrategy<T>) handler);
    }

    public <T extends Exception> Optional<ExceptionHandlerStrategy<T>> getHandler(T exception) {
        @SuppressWarnings("unchecked")
        Class<T> exceptionClass = (Class<T>) exception.getClass();
        return getHandler(exceptionClass);
    }

    public boolean hasHandler(Class<? extends Exception> exceptionType) {
        return handlers.containsKey(exceptionType);
    }

    public int getHandlerCount() {
        return handlers.size();
    }
}