package opus.social.app.reporteai.domain.specification;

/**
 * Especificação composta que combina duas especificações
 * Specification Pattern - Permite composição de regras
 */
public class CompositeSpecification<T> extends Specification<T> {
    private final Specification<T> left;
    private final Specification<T> right;
    private final String operator;

    public CompositeSpecification(Specification<T> left, Specification<T> right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public boolean isSatisfiedBy(T candidate) {
        if ("AND".equals(operator)) {
            return left.isSatisfiedBy(candidate) && right.isSatisfiedBy(candidate);
        } else if ("OR".equals(operator)) {
            return left.isSatisfiedBy(candidate) || right.isSatisfiedBy(candidate);
        }
        return false;
    }

    @Override
    public String getDescription() {
        return String.format("(%s %s %s)",
            left.getDescription(),
            operator,
            right.getDescription()
        );
    }
}
