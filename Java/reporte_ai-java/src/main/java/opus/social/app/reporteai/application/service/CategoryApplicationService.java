package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Category;
import opus.social.app.reporteai.domain.port.CategoryRepositoryPort;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryApplicationService {
    private final CategoryRepositoryPort categoryRepository;

    public CategoryApplicationService(CategoryRepositoryPort categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Category createCategory(String name, String description, String color, String iconUrl) {
        if (categoryRepository.existsByName(name)) {
            throw new DuplicateDataException("Category with name already exists: " + name);
        }

        Category category = Category.builder()
            .id(UUID.randomUUID())
            .name(name)
            .description(description)
            .color(color)
            .iconUrl(iconUrl)
            .isActive(true)
            .build();

        return categoryRepository.save(category);
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("Category not found with id: " + id));
    }

    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name)
            .orElseThrow(() -> new EmployeeNotFoundException("Category not found with name: " + name));
    }

    public List<Category> getAllActiveCategories() {
        return categoryRepository.findAllActive();
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category updateCategory(UUID id, String name, String description, String color) {
        Category category = getCategoryById(id);
        category.setName(name);
        category.setDescription(description);
        category.setColor(color);
        return categoryRepository.update(category);
    }

    public void deleteCategory(UUID id) {
        categoryRepository.delete(id);
    }
}
