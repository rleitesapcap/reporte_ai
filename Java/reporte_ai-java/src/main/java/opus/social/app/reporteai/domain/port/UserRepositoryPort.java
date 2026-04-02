package opus.social.app.reporteai.domain.port;

import opus.social.app.reporteai.domain.entity.User;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findById(UUID id);
    Optional<User> findByPhoneNumber(String phoneNumber);
    Optional<User> findByEmail(String email);
    List<User> findAllActive();
    List<User> findAll();
    User update(User user);
    void delete(UUID id);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
}
