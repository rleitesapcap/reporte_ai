package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.Category;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface CategoryRepositoryPort {
    Category save(Category category);
    Optional<Category> findById(UUID id);
    Optional<Category> findByName(String name);
    List<Category> findAllActive();
    List<Category> findAll();
    Category update(Category category);
    void delete(UUID id);
    boolean existsByName(String name);
}
