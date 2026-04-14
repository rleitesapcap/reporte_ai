package opus.social.app.reporteai.domain.specification;

/**
 * Classe base abstrata para especificações de domínio
 * Specification Pattern - Define regras de negócio reutilizáveis
 *
 * Benefícios:
 * - Encapsula regras de validação complexas
 * - Promove reusabilidade
 * - Facilita testes unitários
 * - Expressivo e legível
 */
public abstract class Specification<T> {

    /**
     * Avalia se o candidato satisfaz esta especificação
     */
    public abstract boolean isSatisfiedBy(T candidate);

    /**
     * Retorna descrição legível da especificação
     */
    public abstract String getDescription();

    /**
     * Compõe com outra especificação usando AND
     */
    public Specification<T> and(Specification<T> other) {
        return new CompositeSpecification<>(this, other, "AND");
    }

    /**
     * Compõe com outra especificação usando OR
     */
    public Specification<T> or(Specification<T> other) {
        return new CompositeSpecification<>(this, other, "OR");
    }

    /**
     * Nega esta especificação
     */
    public Specification<T> not() {
        return new NegatedSpecification<>(this);
    }

    @Override
    public String toString() {
        return getDescription();
    }
}
