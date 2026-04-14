package opus.social.app.reporteai.domain.specification;

/**
 * Especificação negada - Inverte o resultado de uma especificação
 * Specification Pattern - Suporte a negação lógica
 */
public class NegatedSpecification<T> extends Specification<T> {
    private final Specification<T> spec;

    public NegatedSpecification(Specification<T> spec) {
        this.spec = spec;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        return !spec.isSatisfiedBy(candidate);
    }

    @Override
    public String getDescription() {
        return "NÃO (" + spec.getDescription() + ")";
    }
}
