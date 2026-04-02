package opus.social.app.reporteai.infrastructure.persistence.adapter;

import opus.social.app.reporteai.domain.entity.SubCategory;
import opus.social.app.reporteai.domain.port.SubCategoryRepositoryPort;
import opus.social.app.reporteai.infrastructure.persistence.entity.SubCategoryJpaEntity;
import opus.social.app.reporteai.infrastructure.persistence.repository.SubCategoryJpaRepository;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Component
public class SubCategoryRepositoryAdapter implements SubCategoryRepositoryPort {
    private final SubCategoryJpaRepository repository;

    public SubCategoryRepositoryAdapter(SubCategoryJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public SubCategory save(SubCategory subCategory) {
        SubCategoryJpaEntity entity = toDomainEntity(subCategory);
        SubCategoryJpaEntity saved = repository.save(entity);
        return toSubCategory(saved);
    }

    @Override
    public Optional<SubCategory> findById(UUID id) {
        return repository.findById(id).map(this::toSubCategory);
    }

    @Override
    public List<SubCategory> findByCategoryId(UUID categoryId) {
        return repository.findByCategoryId(categoryId).stream()
            .map(this::toSubCategory)
            .toList();
    }

    @Override
    public List<SubCategory> findByCategoryIdActive(UUID categoryId) {
        return repository.findByCategoryIdActive(categoryId).stream()
            .map(this::toSubCategory)
            .toList();
    }

    @Override
    public List<SubCategory> findAllActive() {
        return repository.findAllActive().stream()
            .map(this::toSubCategory)
            .toList();
    }

    @Override
    public List<SubCategory> findAll() {
        return repository.findAll().stream()
            .map(this::toSubCategory)
            .toList();
    }

    @Override
    public SubCategory update(SubCategory subCategory) {
        return save(subCategory);
    }

    @Override
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    private SubCategory toSubCategory(SubCategoryJpaEntity entity) {
        return SubCategory.builder()
            .id(entity.getId())
            .categoryId(entity.getCategoryId())
            .name(entity.getName())
            .description(entity.getDescription())
            .isActive(entity.getIsActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
    }

    private SubCategoryJpaEntity toDomainEntity(SubCategory subCategory) {
        return new SubCategoryJpaEntity(
            subCategory.getId(),
            subCategory.getCategoryId(),
            subCategory.getName(),
            subCategory.getDescription(),
            subCategory.getIsActive(),
            subCategory.getCreatedAt(),
            subCategory.getUpdatedAt()
        );
    }
}
