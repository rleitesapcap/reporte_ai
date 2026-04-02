package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.SubCategory;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface SubCategoryRepositoryPort {
    SubCategory save(SubCategory subCategory);
    Optional<SubCategory> findById(UUID id);
    List<SubCategory> findByCategoryId(UUID categoryId);
    List<SubCategory> findByCategoryIdActive(UUID categoryId);
    List<SubCategory> findAllActive();
    List<SubCategory> findAll();
    SubCategory update(SubCategory subCategory);
    void delete(UUID id);
}
