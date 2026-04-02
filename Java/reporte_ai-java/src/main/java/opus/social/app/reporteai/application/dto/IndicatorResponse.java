package opus.social.app.reporteai.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.math.BigDecimal;

public class IndicatorResponse {
    private UUID id;
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

    public IndicatorResponse() {}

    public IndicatorResponse(UUID id, String name, String type, String description, BigDecimal value,
            String unit, UUID categoryId, String neighborhood, LocalDate start, LocalDate end, LocalDateTime calculated) {
        this.id = id;
        this.indicatorName = name;
        this.indicatorType = type;
        this.description = description;
        this.value = value;
        this.unit = unit;
        this.categoryId = categoryId;
        this.neighborhood = neighborhood;
        this.periodStart = start;
        this.periodEnd = end;
        this.calculatedAt = calculated;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String name) { this.indicatorName = name; }

    public String getIndicatorType() { return indicatorType; }
    public void setIndicatorType(String type) { this.indicatorType = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public UUID getCategoryId() { return categoryId; }
    public void setCategoryId(UUID categoryId) { this.categoryId = categoryId; }

    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate start) { this.periodStart = start; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate end) { this.periodEnd = end; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculated) { this.calculatedAt = calculated; }
}
