package garcias.api.catalog.category;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.test.ApplicationModuleTest;

@ApplicationModuleTest
@DisplayName("Catalog Category Module Integration Tests")
class CatalogCategoryModuleTest {

    @Test
    @DisplayName("Verify that the Category module loads context successfully in isolation")
    void verifyModuleContextLoads() {
        // Test passes if the application context for this module successfully loads
    }
}
