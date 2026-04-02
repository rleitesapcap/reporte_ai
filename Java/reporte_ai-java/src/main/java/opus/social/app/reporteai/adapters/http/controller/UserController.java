package opus.social.app.reporteai.adapters.http.controller;

import opus.social.app.reporteai.application.dto.UserCreateRequest;
import opus.social.app.reporteai.application.dto.UserResponse;
import opus.social.app.reporteai.application.service.UserApplicationService;
import opus.social.app.reporteai.domain.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "User management endpoints")
public class UserController {
    private final UserApplicationService userService;

    public UserController(UserApplicationService userService) {
        this.userService = userService;
    }

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = userService.createUser(request.getPhoneNumber(), request.getName(),
            request.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping("/phone/{phoneNumber}")
    @Operation(summary = "Get user by phone number")
    public ResponseEntity<UserResponse> getUserByPhone(@PathVariable String phoneNumber) {
        User user = userService.getUserByPhoneNumber(phoneNumber);
        return ResponseEntity.ok(toResponse(user));
    }

    @GetMapping
    @Operation(summary = "Get all active users")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        List<User> users = userService.getAllActiveUsers();
        return ResponseEntity.ok(users.stream().map(this::toResponse).toList());
    }

    @GetMapping("/all")
    @Operation(summary = "Get all users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users.stream().map(this::toResponse).toList());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID id,
            @Valid @RequestBody UserCreateRequest request) {
        User user = userService.updateUser(id, request.getName(), request.getEmail());
        return ResponseEntity.ok(toResponse(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getPhoneNumber(), user.getName(),
            user.getEmail(), user.getTrustScore(), user.getTotalOccurrences(),
            user.getIsActive(), user.getCreatedAt(), user.getUpdatedAt());
    }
}
