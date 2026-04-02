package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.CategoryCreateRequest;
import opus.social.app.reporteai.application.dto.CategoryResponse;
import opus.social.app.reporteai.application.service.CategoryApplicationService;
import opus.social.app.reporteai.domain.entity.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {
    private final CategoryApplicationService categoryService;

    public CategoryController(CategoryApplicationService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.createCategory(request.getName(),
            request.getDescription(), request.getColor(), request.getIconUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(category));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable UUID id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(toResponse(category));
    }

    @GetMapping
    @Operation(summary = "Get all active categories")
    public ResponseEntity<List<CategoryResponse>> getActiveCategories() {
        List<Category> categories = categoryService.getAllActiveCategories();
        return ResponseEntity.ok(categories.stream().map(this::toResponse).toList());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all categories")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories.stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id,
            @Valid @RequestBody CategoryCreateRequest request) {
        Category category = categoryService.updateCategory(id, request.getName(),
            request.getDescription(), request.getColor());
        return ResponseEntity.ok(toResponse(category));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    private CategoryResponse toResponse(Category category) {
        return new CategoryResponse(category.getId(), category.getName(),
            category.getDescription(), category.getColor(), category.getIconUrl(),
            category.getIsActive());
    }
}
