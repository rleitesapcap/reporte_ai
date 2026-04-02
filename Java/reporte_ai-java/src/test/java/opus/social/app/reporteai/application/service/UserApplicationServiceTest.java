package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.User;
import opus.social.app.reporteai.domain.port.UserRepositoryPort;
import opus.social.app.reporteai.domain.exception.DuplicateDataException;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserApplicationServiceTest {
    @Mock
    private UserRepositoryPort userRepository;

    @InjectMocks
    private UserApplicationService userService;

    private String phoneNumber;
    private String name;
    private String email;

    @BeforeEach
    void setUp() {
        phoneNumber = "5511999999999";
        name = "Test User";
        email = "test@example.com";
    }

    @Test
    void testCreateUserSuccess() {
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(false);
        when(userRepository.existsByEmail(email)).thenReturn(false);
        
        User mockUser = User.builder()
            .id(UUID.randomUUID())
            .phoneNumber(phoneNumber)
            .name(name)
            .email(email)
            .build();
        
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        User result = userService.createUser(phoneNumber, name, email);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getPhoneNumber());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicatePhoneNumber() {
        when(userRepository.existsByPhoneNumber(phoneNumber)).thenReturn(true);

        assertThrows(DuplicateDataException.class,
            () -> userService.createUser(phoneNumber, name, email));
    }

    @Test
    void testGetUserById() {
        UUID userId = UUID.randomUUID();
        User mockUser = User.builder()
            .id(userId)
            .phoneNumber(phoneNumber)
            .name(name)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserByIdNotFound() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
            () -> userService.getUserById(userId));
    }

    @Test
    void testGetUserByPhoneNumber() {
        User mockUser = User.builder()
            .phoneNumber(phoneNumber)
            .name(name)
            .build();

        when(userRepository.findByPhoneNumber(phoneNumber)).thenReturn(Optional.of(mockUser));

        User result = userService.getUserByPhoneNumber(phoneNumber);

        assertNotNull(result);
        assertEquals(phoneNumber, result.getPhoneNumber());
    }
}
