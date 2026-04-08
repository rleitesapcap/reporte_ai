package opus.social.app.reporteai.application.service;

import opus.social.app.reporteai.domain.entity.Occurrence;
import opus.social.app.reporteai.domain.port.OccurrenceRepositoryPort;
import opus.social.app.reporteai.domain.exception.EmployeeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OccurrenceApplicationServiceTest {
    @Mock
    private OccurrenceRepositoryPort occurrenceRepository;

    @InjectMocks
    private OccurrenceApplicationService occurrenceService;

    @Test
    void testCreateOccurrenceSuccess() {
        UUID categoryId = UUID.randomUUID();
        String description = "Test occurrence";
        
        Occurrence mockOccurrence = Occurrence.builder()
            .id(UUID.randomUUID())
            .categoryId(categoryId)
            .protocolId("CAP-2024-000001")
            .description(description)
            .severity(3)
            .build();

        when(occurrenceRepository.save(any(Occurrence.class))).thenReturn(mockOccurrence);

        Occurrence result = occurrenceService.createOccurrence(null, categoryId, null,
            "CAP-2024-000001", description, null, null, null, null, 3, 1);

        assertNotNull(result);
        assertEquals(categoryId, result.getCategoryId());
        verify(occurrenceRepository).save(any(Occurrence.class));
    }

    @Test
    void testGetOccurrenceById() {
        UUID occurrenceId = UUID.randomUUID();
        Occurrence mockOccurrence = Occurrence.builder()
            .id(occurrenceId)
            .categoryId(UUID.randomUUID())
            .protocolId("CAP-2024-000001")
            .description("Test")
            .severity(2)
            .frequency(1)
            .build();

        when(occurrenceRepository.findById(occurrenceId)).thenReturn(Optional.of(mockOccurrence));

        Occurrence result = occurrenceService.getOccurrenceById(occurrenceId);

        assertNotNull(result);
        assertEquals(occurrenceId, result.getId());
    }

    @Test
    void testGetOccurrenceByIdNotFound() {
        UUID occurrenceId = UUID.randomUUID();
        when(occurrenceRepository.findById(occurrenceId)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class,
            () -> occurrenceService.getOccurrenceById(occurrenceId));
    }

    @Test
    void testGetOccurrencesByStatus() {
        String status = "validated";
        Occurrence mockOccurrence = Occurrence.builder()
            .id(UUID.randomUUID())
            .categoryId(UUID.randomUUID())
            .protocolId("CAP-2024-000001")
            .description("Test occurrence")
            .severity(3)
            .frequency(1)
            .status(status)
            .build();

        when(occurrenceRepository.findByStatusOrderByPriority(status))
            .thenReturn(List.of(mockOccurrence));

        List<Occurrence> results = occurrenceService.getOccurrencesByStatus(status);

        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(status, results.get(0).getStatus());
    }

    @Test
    void testUpdateOccurrenceStatus() {
        UUID occurrenceId = UUID.randomUUID();
        String newStatus = "resolved";
        
        Occurrence mockOccurrence = Occurrence.builder()
            .id(occurrenceId)
            .categoryId(UUID.randomUUID())
            .protocolId("CAP-2024-000001")
            .description("Test occurrence")
            .severity(3)
            .frequency(1)
            .status("validated")
            .build();

        when(occurrenceRepository.findById(occurrenceId)).thenReturn(Optional.of(mockOccurrence));
        when(occurrenceRepository.update(any(Occurrence.class))).thenReturn(mockOccurrence);

        Occurrence result = occurrenceService.updateOccurrenceStatus(occurrenceId, newStatus);

        assertNotNull(result);
        verify(occurrenceRepository).update(any(Occurrence.class));
    }
}
