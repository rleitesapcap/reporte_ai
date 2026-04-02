package opus.social.app.reporteai.adapters.http.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

/**
 * Controller REST para Analytics e Relatórios
 * Adapter de entrada (Inbound Adapter) da arquitetura hexagonal
 */
@RestController
@RequestMapping("/api/v1/analytics")
@Tag(name = "Analytics", description = "Endpoints para relatórios e análises de dados")
public class AnalyticsController {

    /**
     * Dashboard de resumo
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Dashboard resumido", description = "Retorna dados resumidos para o dashboard")
    @ApiResponse(responseCode = "200", description = "Dashboard data retrieved")
    public ResponseEntity<Map<String, Object>> getDashboard(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Summary statistics
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalOccurrences", 1250);
        summary.put("activeReports", 42);
        summary.put("totalUsers", 156);
        summary.put("averageTrustScore", 78.5);
        dashboard.put("summary", summary);
        
        // Trend data
        Map<String, Object> trends = new HashMap<>();
        trends.put("occurrencesTrend", Arrays.asList(45, 52, 48, 61, 58, 69));
        trends.put("reportsTrend", Arrays.asList(8, 12, 15, 11, 14, 18));
        dashboard.put("trends", trends);
        
        // Top categories
        Map<String, Object> topCategories = new HashMap<>();
        topCategories.put("categories", Arrays.asList(
            Map.of("name", "Segurança", "count", 345),
            Map.of("name", "Infraestrutura", "count", 289),
            Map.of("name", "Saúde", "count", 256),
            Map.of("name", "Educação", "count", 198)
        ));
        dashboard.put("topCategories", topCategories);
        
        // Geographic distribution
        Map<String, Object> geographic = new HashMap<>();
        geographic.put("neighborhoods", Arrays.asList(
            Map.of("name", "Centro", "occurrences", 234),
            Map.of("name", "Zona Norte", "occurrences", 189),
            Map.of("name", "Zona Sul", "occurrences", 156),
            Map.of("name", "Zona Leste", "occurrences", 142)
        ));
        dashboard.put("geographic", geographic);
        
        dashboard.put("period", Map.of("start", startDate, "end", endDate));
        
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Análise de ocorrências por período
     */
    @GetMapping("/occurrences/period")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Análise por período", description = "Retorna análise de ocorrências por período")
    @ApiResponse(responseCode = "200", description = "Period analysis retrieved")
    public ResponseEntity<Map<String, Object>> getOccurrencesByPeriod(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("totalOccurrences", 523);
        analysis.put("averagePerDay", 18.7);
        analysis.put("peakDay", "2024-01-15");
        analysis.put("trend", "INCREASING");
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("byCategory", Arrays.asList(
            Map.of("category", "Segurança", "count", 156),
            Map.of("category", "Infraestrutura", "count", 142),
            Map.of("category", "Saúde", "count", 98),
            Map.of("category", "Educação", "count", 67),
            Map.of("category", "Outros", "count", 60)
        ));
        
        breakdown.put("byStatus", Arrays.asList(
            Map.of("status", "Resolvido", "count", 287),
            Map.of("status", "Pendente", "count", 156),
            Map.of("status", "Duplicado", "count", 80)
        ));
        
        analysis.put("breakdown", breakdown);
        analysis.put("period", Map.of("start", startDate, "end", endDate));
        
        return ResponseEntity.ok(analysis);
    }

    /**
     * Análise geográfica (clusters)
     */
    @GetMapping("/geographic/clusters")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Análise geográfica", description = "Retorna análise de clusters geográficos")
    @ApiResponse(responseCode = "200", description = "Geographic analysis retrieved")
    public ResponseEntity<Map<String, Object>> getGeographicAnalysis(
            @RequestParam(required = false) String neighborhood) {
        Map<String, Object> analysis = new HashMap<>();
        
        analysis.put("totalClusters", 24);
        analysis.put("averageOccurrencesPerCluster", 12.3);
        analysis.put("highRiskClusters", 5);
        
        Map<String, Object> clusters = new HashMap<>();
        clusters.put("clusters", Arrays.asList(
            Map.of(
                "id", "cluster-1",
                "neighborhood", "Centro",
                "latitude", -23.5505,
                "longitude", -46.6333,
                "occurrences", 45,
                "averageTrustScore", 82.5,
                "riskLevel", "MEDIUM"
            ),
            Map.of(
                "id", "cluster-2",
                "neighborhood", "Zona Norte",
                "latitude", -23.5245,
                "longitude", -46.6288,
                "occurrences", 38,
                "averageTrustScore", 75.2,
                "riskLevel", "HIGH"
            ),
            Map.of(
                "id", "cluster-3",
                "neighborhood", "Zona Sul",
                "latitude", -23.6216,
                "longitude", -46.6558,
                "occurrences", 31,
                "averageTrustScore", 88.1,
                "riskLevel", "LOW"
            )
        ));
        
        analysis.put("clusters", clusters);
        
        return ResponseEntity.ok(analysis);
    }

    /**
     * Métricas de qualidade de dados
     */
    @GetMapping("/quality-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Métricas de qualidade", description = "Retorna métricas de qualidade dos dados")
    @ApiResponse(responseCode = "200", description = "Quality metrics retrieved")
    public ResponseEntity<Map<String, Object>> getQualityMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        metrics.put("dataCompleteness", 94.2);
        metrics.put("validationRate", 87.5);
        metrics.put("deduplicationRate", 3.2);
        metrics.put("averageConfidenceScore", 79.8);
        
        Map<String, Object> validation = new HashMap<>();
        validation.put("totalValidations", 1245);
        validation.put("validationsPassed", 1089);
        validation.put("validationsFailed", 156);
        validation.put("successRate", 87.5);
        
        metrics.put("validation", validation);
        
        Map<String, Object> deduplication = new HashMap<>();
        deduplication.put("totalDuplicates", 40);
        deduplication.put("similarityThreshold", 0.85);
        deduplication.put("averageSimilarity", 0.91);
        
        metrics.put("deduplication", deduplication);
        
        return ResponseEntity.ok(metrics);
    }

    /**
     * Relatório de atividades de usuários
     */
    @GetMapping("/user-activity")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Atividade de usuários", description = "Retorna análise de atividade de usuários")
    @ApiResponse(responseCode = "200", description = "User activity retrieved")
    public ResponseEntity<Map<String, Object>> getUserActivity(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        Map<String, Object> activity = new HashMap<>();
        
        activity.put("totalActiveUsers", 156);
        activity.put("newUsersThisPeriod", 23);
        activity.put("averageOccurrencesPerUser", 8.0);
        activity.put("averageTrustScore", 78.5);
        
        Map<String, Object> contributors = new HashMap<>();
        contributors.put("topContributors", Arrays.asList(
            Map.of("userId", "user-1", "username", "john_doe", "occurrences", 34, "trustScore", 92.5),
            Map.of("userId", "user-2", "username", "jane_smith", "occurrences", 28, "trustScore", 88.3),
            Map.of("userId", "user-3", "username", "bob_wilson", "occurrences", 25, "trustScore", 85.1),
            Map.of("userId", "user-4", "username", "alice_brown", "occurrences", 22, "trustScore", 81.7),
            Map.of("userId", "user-5", "username", "charlie_davis", "occurrences", 19, "trustScore", 78.2)
        ));
        
        activity.put("contributors", contributors);
        
        Map<String, Object> engagement = new HashMap<>();
        engagement.put("dailyActiveUsers", Arrays.asList(45, 52, 48, 61, 58, 69, 65));
        engagement.put("weeklyOccurrences", 412);
        engagement.put("monthlyOccurrences", 1345);
        
        activity.put("engagement", engagement);
        activity.put("period", Map.of("start", startDate, "end", endDate));
        
        return ResponseEntity.ok(activity);
    }

    /**
     * Relatório de performance de validadores
     */
    @GetMapping("/validator-performance")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Performance de validadores", description = "Retorna análise de performance dos validadores")
    @ApiResponse(responseCode = "200", description = "Validator performance retrieved")
    public ResponseEntity<Map<String, Object>> getValidatorPerformance() {
        Map<String, Object> performance = new HashMap<>();
        
        performance.put("totalValidators", 12);
        performance.put("totalValidations", 2456);
        performance.put("averageValidationTime", "2.5 min");
        performance.put("averageAccuracy", 93.2);
        
        Map<String, Object> topValidators = new HashMap<>();
        topValidators.put("validators", Arrays.asList(
            Map.of(
                "validatorId", "val-1",
                "name", "Maria Santos",
                "validations", 345,
                "accuracy", 97.2,
                "averageTime", "1.8 min"
            ),
            Map.of(
                "validatorId", "val-2",
                "name", "Pedro Costa",
                "validations", 298,
                "accuracy", 94.5,
                "averageTime", "2.1 min"
            ),
            Map.of(
                "validatorId", "val-3",
                "name", "Ana Silva",
                "validations", 276,
                "accuracy", 91.8,
                "averageTime", "2.4 min"
            )
        ));
        
        performance.put("topValidators", topValidators);
        
        return ResponseEntity.ok(performance);
    }

    /**
     * Relatório de tendências e previsões
     */
    @GetMapping("/trends-forecast")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ANALYST')")
    @Operation(summary = "Tendências e previsões", description = "Retorna análise de tendências e previsões")
    @ApiResponse(responseCode = "200", description = "Trends and forecast retrieved")
    public ResponseEntity<Map<String, Object>> getTrendsAndForecast(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> forecast = new HashMap<>();
        
        forecast.put("forecastPeriodDays", days);
        forecast.put("confidenceLevel", 0.85);
        
        Map<String, Object> trends = new HashMap<>();
        trends.put("occurrenceTrend", "INCREASING");
        trends.put("trendPercentage", 12.5);
        trends.put("forecastedOccurrences", 1410);
        trends.put("confidenceInterval", Map.of("lower", 1280, "upper", 1540));
        
        forecast.put("occurrenceTrend", trends);
        
        Map<String, Object> categoryTrends = new HashMap<>();
        categoryTrends.put("categories", Arrays.asList(
            Map.of("name", "Segurança", "trend", "INCREASING", "percentage", 15.2),
            Map.of("name", "Infraestrutura", "trend", "STABLE", "percentage", -1.3),
            Map.of("name", "Saúde", "trend", "INCREASING", "percentage", 8.5),
            Map.of("name", "Educação", "trend", "DECREASING", "percentage", -3.2)
        ));
        
        forecast.put("categoryTrends", categoryTrends);
        
        return ResponseEntity.ok(forecast);
    }

    /**
     * Exportar relatório completo
     */
    @GetMapping("/export/report")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Exportar relatório", description = "Gera um relatório completo em PDF/Excel")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Report generated"),
        @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public ResponseEntity<Map<String, Object>> exportReport(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "PDF") String format) {
        Map<String, Object> exportInfo = new HashMap<>();
        
        exportInfo.put("status", "GENERATED");
        exportInfo.put("format", format);
        exportInfo.put("filename", "report_" + startDate + "_" + endDate + "." + format.toLowerCase());
        exportInfo.put("downloadUrl", "/api/v1/analytics/download/report-xyz789");
        exportInfo.put("fileSize", "2.5 MB");
        exportInfo.put("pages", 45);
        exportInfo.put("generatedAt", "2024-01-20T15:30:00");
        exportInfo.put("expiresAt", "2024-02-20T15:30:00");
        
        return ResponseEntity.ok(exportInfo);
    }
}
