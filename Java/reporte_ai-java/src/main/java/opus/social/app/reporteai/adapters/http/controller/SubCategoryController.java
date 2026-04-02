package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.service.SubCategoryApplicationService;
import opus.social.app.reporteai.domain.entity.SubCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/sub-categories")
@Tag(name = "SubCategories", description = "SubCategory management endpoints")
public class SubCategoryController {
    private final SubCategoryApplicationService subCategoryService;

    public SubCategoryController(SubCategoryApplicationService subCategoryService) {
        this.subCategoryService = subCategoryService;
    }

    @PostMapping
    @Operation(summary = "Create a new sub-category")
    public ResponseEntity<Map<String, Object>> createSubCategory(@RequestBody Map<String, Object> request) {
        SubCategory subCategory = subCategoryService.createSubCategory(
            UUID.fromString((String) request.get("categoryId")),
            (String) request.get("name"),
            (String) request.get("description"));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", subCategory.getId()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get sub-category by ID")
    public ResponseEntity<Map<String, Object>> getSubCategoryById(@PathVariable UUID id) {
        SubCategory subCategory = subCategoryService.getSubCategoryById(id);
        return ResponseEntity.ok(toMap(subCategory));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get sub-categories by category")
    public ResponseEntity<List<Map<String, Object>>> getSubCategoriesByCategory(@PathVariable UUID categoryId) {
        List<SubCategory> subCategories = subCategoryService.getSubCategoriesByCategoryId(categoryId);
        return ResponseEntity.ok(subCategories.stream().map(this::toMap).toList());
    }

    @GetMapping
    @Operation(summary = "Get all active sub-categories")
    public ResponseEntity<List<Map<String, Object>>> getAllActiveSubCategories() {
        List<SubCategory> subCategories = subCategoryService.getAllActiveSubCategories();
        return ResponseEntity.ok(subCategories.stream().map(this::toMap).toList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update sub-category")
    public ResponseEntity<Map<String, Object>> updateSubCategory(@PathVariable UUID id,
            @RequestBody Map<String, Object> request) {
        SubCategory subCategory = subCategoryService.updateSubCategory(id,
            (String) request.get("name"), (String) request.get("description"));
        return ResponseEntity.ok(toMap(subCategory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete sub-category")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable UUID id) {
        subCategoryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }

    private Map<String, Object> toMap(SubCategory subCategory) {
        return Map.of(
            "id", subCategory.getId(),
            "categoryId", subCategory.getCategoryId(),
            "name", subCategory.getName(),
            "description", subCategory.getDescription()
        );
    }
}
