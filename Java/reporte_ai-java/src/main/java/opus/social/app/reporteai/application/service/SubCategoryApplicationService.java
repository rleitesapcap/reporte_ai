package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.SubCategory;
import opus.social.app.reporteai.domain.port.SubCategoryRepositoryPort;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class SubCategoryApplicationService {
    private final SubCategoryRepositoryPort subCategoryRepository;

    public SubCategoryApplicationService(SubCategoryRepositoryPort subCategoryRepository) {
        this.subCategoryRepository = subCategoryRepository;
    }

    public SubCategory createSubCategory(UUID categoryId, String name, String description) {
        SubCategory subCategory = SubCategory.builder()
            .id(UUID.randomUUID())
            .categoryId(categoryId)
            .name(name)
            .description(description)
            .isActive(true)
            .build();
        return subCategoryRepository.save(subCategory);
    }

    public SubCategory getSubCategoryById(UUID id) {
        return subCategoryRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("SubCategory not found"));
    }

    public List<SubCategory> getSubCategoriesByCategoryId(UUID categoryId) {
        return subCategoryRepository.findByCategoryIdActive(categoryId);
    }

    public List<SubCategory> getAllActiveSubCategories() {
        return subCategoryRepository.findAllActive();
    }

    public SubCategory updateSubCategory(UUID id, String name, String description) {
        SubCategory subCategory = getSubCategoryById(id);
        subCategory.setName(name);
        subCategory.setDescription(description);
        return subCategoryRepository.update(subCategory);
    }

    public void deleteSubCategory(UUID id) {
        subCategoryRepository.delete(id);
    }
}
