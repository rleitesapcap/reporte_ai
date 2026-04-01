package opus.social.app.reporteai.adapters.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Inicializa e garante a existência do banco de dados antes do Flyway rodar.
 *
 * Fluxo na inicialização:
 *   1. Conecta ao PostgreSQL usando o banco "postgres" (sempre existente)
 *   2. Verifica se o banco alvo (reporteai_db) existe
 *   3. Se não existir, cria automaticamente
 *   4. Libera a execução do Flyway, que aplica V1 (schema) e V2 (seed)
 */
@Configuration
public class DatabaseInitializerConfig {

    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializerConfig.class);

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {
            ensureDatabaseExists();
            flyway.migrate();
        };
    }

    private void ensureDatabaseExists() {
        String dbName = extractDatabaseName(datasourceUrl);
        String adminUrl = buildAdminUrl(datasourceUrl);

        log.info("Verificando existência do banco de dados '{}'...", dbName);

        try (Connection conn = DriverManager.getConnection(adminUrl, username, password)) {
            if (!databaseExists(conn, dbName)) {
                createDatabase(conn, dbName);
            } else {
                log.info("Banco de dados '{}' já existe.", dbName);
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                "Falha ao verificar/criar o banco de dados '" + dbName + "': " + e.getMessage(), e
            );
        }
    }

    private boolean databaseExists(Connection conn, String dbName) throws SQLException {
        String sql = "SELECT 1 FROM pg_database WHERE datname = ?";
        try (var ps = conn.prepareStatement(sql)) {
            ps.setString(1, dbName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private void createDatabase(Connection conn, String dbName) throws SQLException {
        // Não é possível usar parâmetro bind em CREATE DATABASE — nome é validado
        if (!dbName.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Nome de banco de dados inválido: " + dbName);
        }
        log.info("Banco de dados '{}' não encontrado. Criando...", dbName);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE DATABASE " + dbName);
        }
        log.info("Banco de dados '{}' criado com sucesso.", dbName);
    }

    /**
     * Extrai o nome do banco da URL JDBC.
     * Exemplo: jdbc:postgresql://localhost:5432/reporteai_db -> reporteai_db
     */
    private String extractDatabaseName(String url) {
        // Remove parâmetros de query, se houver
        String clean = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        int lastSlash = clean.lastIndexOf('/');
        if (lastSlash < 0 || lastSlash == clean.length() - 1) {
            throw new IllegalArgumentException("Não foi possível extrair o nome do banco da URL: " + url);
        }
        return clean.substring(lastSlash + 1);
    }

    /**
     * Constrói a URL de conexão administrativa apontando para o banco "postgres".
     * Exemplo: jdbc:postgresql://localhost:5432/reporteai_db -> jdbc:postgresql://localhost:5432/postgres
     */
    private String buildAdminUrl(String url) {
        String clean = url.contains("?") ? url.substring(0, url.indexOf('?')) : url;
        int lastSlash = clean.lastIndexOf('/');
        return clean.substring(0, lastSlash + 1) + "postgres";
    }
}
