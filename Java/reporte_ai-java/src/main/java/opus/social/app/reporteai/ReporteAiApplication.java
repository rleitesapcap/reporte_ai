package opus.social.app.reporteai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicação principal ReporteAI
 * Arquitetura: Hexagonal (Ports & Adapters)
 * Sistema de gestão de funcionários com Design Patterns aplicados
 */
@SpringBootApplication
public class ReporteAiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReporteAiApplication.class, args);
    }

}