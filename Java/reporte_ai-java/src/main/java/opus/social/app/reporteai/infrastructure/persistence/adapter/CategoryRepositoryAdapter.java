package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.Category;
import opus.social.app.reporteai.domain.port.CategoryRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.CategoryJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.CategoryJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {
    private final CategoryJpaRepository repository;

    public CategoryRepositoryAdapter(CategoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public Category save(Category category) {
        CategoryJpaEntity entity = toDomainEntity(category);
        CategoryJpaEntity saved = repository.save(entity);
        return toCategory(saved);
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return repository.findById(id).map(this::toCategory);
    }

    @Override
    public Optional<Category> findByName(String name) {
        return repository.findByName(name).map(this::toCategory);
    }

    @Override
    public List<Category> findAllActive() {
        return repository.findAllActive().stream()
            .map(this::toCategory)
            .toList();
    }

    @Override
    public List<Category> findAll() {
        return repository.findAll().stream()
            .map(this::toCategory)
            .toList();
    }

    @Override
    public Category update(Category category) {
        return save(category);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }

    private Category toCategory(CategoryJpaEntity entity) {
        return Category.builder()
            .id(entity.getId())
            .name(entity.getName())
            .description(entity.getDescription())
            .color(entity.getColor())
            .iconUrl(entity.getIconUrl())
            .isActive(entity.getIsActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private CategoryJpaEntity toDomainEntity(Category category) {
        return new CategoryJpaEntity(
            category.getId(),
            category.getName(),
            category.getDescription(),
            category.getColor(),
            category.getIconUrl(),
            category.getIsActive(),
            category.getCreatedAt(),
            category.getUpdatedAt()
        );
    }
}
