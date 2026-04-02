package opus.social.app.reporteai.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class SpatialClusterResponse {
    private UUID id;
    private String clusterName;
    private String neighborhood;
    private BigDecimal centerLatitude;
    private BigDecimal centerLongitude;
    private BigDecimal radiusMeters;
    private Integer occurrenceCount;
    private BigDecimal densityScore;
    private BigDecimal severityAvg;
    private BigDecimal priorityScoreAvg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SpatialClusterResponse() {}

    public SpatialClusterResponse(UUID id, String name, String neighborhood, BigDecimal lat,
            BigDecimal lon, BigDecimal radius, Integer count, BigDecimal density,
            BigDecimal severity, BigDecimal priority, LocalDateTime created, LocalDateTime updated) {
        this.id = id;
        this.clusterName = name;
        this.neighborhood = neighborhood;
        this.centerLatitude = lat;
        this.centerLongitude = lon;
        this.radiusMeters = radius;
        this.occurrenceCount = count;
        this.densityScore = density;
        this.severityAvg = severity;
        this.priorityScoreAvg = priority;
        this.createdAt = created;
        this.updatedAt = updated;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getClusterName() { return clusterName; }
    public void setClusterName(String name) { this.clusterName = name; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public BigDecimal getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(BigDecimal lat) { this.centerLatitude = lat; }

    public BigDecimal getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(BigDecimal lon) { this.centerLongitude = lon; }

    public BigDecimal getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(BigDecimal radius) { this.radiusMeters = radius; }

    public Integer getOccurrenceCount() { return occurrenceCount; }
    public void setOccurrenceCount(Integer count) { this.occurrenceCount = count; }

    public BigDecimal getDensityScore() { return densityScore; }
    public void setDensityScore(BigDecimal density) { this.densityScore = density; }

    public BigDecimal getSeverityAvg() { return severityAvg; }
    public void setSeverityAvg(BigDecimal severity) { this.severityAvg = severity; }

    public BigDecimal getPriorityScoreAvg() { return priorityScoreAvg; }
    public void setPriorityScoreAvg(BigDecimal priority) { this.priorityScoreAvg = priority; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime created) { this.createdAt = created; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updated) { this.updatedAt = updated; }
}
