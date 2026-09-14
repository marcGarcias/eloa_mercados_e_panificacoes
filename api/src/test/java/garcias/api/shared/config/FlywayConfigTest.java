package garcias.api.shared.config;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.flyway.autoconfigure.FlywayMigrationStrategy;

import static org.mockito.Mockito.verify;

@DisplayName("FlywayConfig Unit Tests")
class FlywayConfigTest {

    @Test
    @DisplayName("Deve executar repair seguido de migrate ao invocar FlywayMigrationStrategy")
    void shouldExecuteRepairAndMigrate() {
        FlywayConfig config = new FlywayConfig();
        FlywayMigrationStrategy strategy = config.flywayMigrationStrategy();

        Flyway flywayMock = Mockito.mock(Flyway.class);
        strategy.migrate(flywayMock);

        verify(flywayMock).repair();
        verify(flywayMock).migrate();
    }
}
