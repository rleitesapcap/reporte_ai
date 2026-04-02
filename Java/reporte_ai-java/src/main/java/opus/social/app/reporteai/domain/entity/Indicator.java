package opus.social.app.reporteai.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class Indicator {
    private final UUID id;
    private String indicatorName;
    private String indicatorType;
    private String description;
    private BigDecimal value;
    private String unit;
    private UUID categoryId;
    private String neighborhood;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private LocalDateTime calculatedAt;

    public Indicator(
        UUID id,
        String indicatorName,
        String indicatorType,
        String description,
        BigDecimal value,
        String unit,
        UUID categoryId,
        String neighborhood,
        LocalDate periodStart,
        LocalDate periodEnd,
        LocalDateTime calculatedAt
    ) {
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
