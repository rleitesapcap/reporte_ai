package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.User;
import opus.social.app.reporteai.domain.port.UserRepositoryPort;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Service
public class UserApplicationService {
    private final UserRepositoryPort userRepository;

    public UserApplicationService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String phoneNumber, String name, String email) {
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new DuplicateDataException("User with phone number already exists: " + phoneNumber);
        }
        if (email != null && userRepository.existsByEmail(email)) {
            throw new DuplicateDataException("User with email already exists: " + email);
        }

        User user = User.builder()
            .id(UUID.randomUUID())
            .phoneNumber(phoneNumber)
            .name(name)
            .email(email)
            .build();

        return userRepository.save(user);
    }

    public User getUserById(UUID id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new EmployeeNotFoundException("User not found with id: " + id));
    }

    public User getUserByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber)
            .orElseThrow(() -> new EmployeeNotFoundException("User not found with phone: " + phoneNumber));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new EmployeeNotFoundException("User not found with email: " + email));
    }

    public List<User> getAllActiveUsers() {
        return userRepository.findAllActive();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(UUID id, String name, String email) {
        User user = getUserById(id);
        user.setName(name);
        user.setEmail(email);
        return userRepository.update(user);
    }

    public void deleteUser(UUID id) {
        userRepository.delete(id);
    }
}
