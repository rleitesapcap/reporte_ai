package opus.social.app.reporteai.domain.specification;

import opus.social.app.reporteai.infrastructure.persistence.repository.AuthUserRepository;

/**
 * Especificação que valida unicidade de username
 * Specification Pattern - Validação de regra de negócio
 */
public class UniqueUsernameSpecification extends Specification<String> {
    private final AuthUserRepository userRepository;

    public UniqueUsernameSpecification(AuthUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isSatisfiedBy(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !userRepository.existsByUsername(username);
    }

    @Override
    public String getDescription() {
        return "Username deve ser único no sistema";
    }
}
