package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "spatial_clusters",
    indexes = {
        @Index(name = "idx_spatial_clusters_neighborhood", columnList = "neighborhood"),
        @Index(name = "idx_spatial_clusters_density_score", columnList = "density_score DESC")
    }
)
public class SpatialClusterJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 255)
    private String clusterName;

    @Column(length = 255)
    private String neighborhood;

    @Column(precision = 10, scale = 8)
    private BigDecimal centerLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal centerLongitude;

    @Column(precision = 10, scale = 2)
    private BigDecimal radiusMeters;

    @Column
    private Integer occurrenceCount;

    @Column(precision = 8, scale = 2)
    private BigDecimal densityScore;

    @Column(precision = 5, scale = 2)
    private BigDecimal severityAvg;

    @Column(precision = 8, scale = 2)
    private BigDecimal priorityScoreAvg;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public SpatialClusterJpaEntity() {
    }

    public SpatialClusterJpaEntity(UUID id, String clusterName, String neighborhood, BigDecimal centerLatitude, BigDecimal centerLongitude, BigDecimal radiusMeters, Integer occurrenceCount, BigDecimal densityScore, BigDecimal severityAvg, BigDecimal priorityScoreAvg, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.clusterName = clusterName;
        this.neighborhood = neighborhood;
        this.centerLatitude = centerLatitude;
        this.centerLongitude = centerLongitude;
        this.radiusMeters = radiusMeters;
        this.occurrenceCount = occurrenceCount;
        this.densityScore = densityScore;
        this.severityAvg = severityAvg;
        this.priorityScoreAvg = priorityScoreAvg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public BigDecimal getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(BigDecimal centerLatitude) {
        this.centerLatitude = centerLatitude;
    }

    public BigDecimal getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(BigDecimal centerLongitude) {
        this.centerLongitude = centerLongitude;
    }

    public BigDecimal getRadiusMeters() {
        return radiusMeters;
    }

    public void setRadiusMeters(BigDecimal radiusMeters) {
        this.radiusMeters = radiusMeters;
    }

    public Integer getOccurrenceCount() {
        return occurrenceCount;
    }

    public void setOccurrenceCount(Integer occurrenceCount) {
        this.occurrenceCount = occurrenceCount;
    }

    public BigDecimal getDensityScore() {
        return densityScore;
    }

    public void setDensityScore(BigDecimal densityScore) {
        this.densityScore = densityScore;
    }

    public BigDecimal getSeverityAvg() {
        return severityAvg;
    }

    public void setSeverityAvg(BigDecimal severityAvg) {
        this.severityAvg = severityAvg;
    }

    public BigDecimal getPriorityScoreAvg() {
        return priorityScoreAvg;
    }

    public void setPriorityScoreAvg(BigDecimal priorityScoreAvg) {
        this.priorityScoreAvg = priorityScoreAvg;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
