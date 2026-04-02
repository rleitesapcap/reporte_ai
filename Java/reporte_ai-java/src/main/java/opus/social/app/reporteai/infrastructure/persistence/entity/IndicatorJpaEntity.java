package opus.social.app.reporteai.infrastructure.persistence.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

@Entity
@Table(
    name = "indicators",
    indexes = {
        @Index(name = "idx_indicators_indicator_type", columnList = "indicator_type"),
        @Index(name = "idx_indicators_category_id", columnList = "category_id"),
        @Index(name = "idx_indicators_period", columnList = "period_start, period_end"),
        @Index(name = "idx_indicators_calculated_at", columnList = "calculated_at")
    }
)
public class IndicatorJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 255)
    private String indicatorName;

    @Column(nullable = false, length = 50)
    private String indicatorType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 15, scale = 2)
    private BigDecimal value;

    @Column(length = 50)
    private String unit;

    @Column
    private UUID categoryId;

    @Column(length = 255)
    private String neighborhood;

    @Column
    private LocalDate periodStart;

    @Column
    private LocalDate periodEnd;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime calculatedAt;

    public IndicatorJpaEntity() {
    }

    public IndicatorJpaEntity(UUID id, String indicatorName, String indicatorType, String description, BigDecimal value, String unit, UUID categoryId, String neighborhood, LocalDate periodStart, LocalDate periodEnd, LocalDateTime calculatedAt) {
        this.id = id;
        this.indicatorName = indicatorName;
        this.indicatorType = indicatorType;
        this.description = description;
        this.value = value;
        this.unit = unit;
        this.categoryId = categoryId;
        this.neighborhood = neighborhood;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.calculatedAt = calculatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public void setIndicatorName(String indicatorName) {
        this.indicatorName = indicatorName;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    public void setIndicatorType(String indicatorType) {
        this.indicatorType = indicatorType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public LocalDate getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(LocalDate periodStart) {
        this.periodStart = periodStart;
    }

    public LocalDate getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(LocalDate periodEnd) {
        this.periodEnd = periodEnd;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }
}
