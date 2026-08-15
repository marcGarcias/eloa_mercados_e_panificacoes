package garcias.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@DisplayName("Application Modular Structure Tests")
class ModularStructureTest {

    private final ApplicationModules modules = ApplicationModules.of(ApiApplication.class);

    @Test
    @DisplayName("Verify that the application modular structure has no cycles and respects architectural rules")
    void verifyModularStructure() {
        modules.verify();
    }

    @Test
    @DisplayName("Generate PlantUML documentation for the application modules")
    void generateModuleDocumentation() {
        new Documenter(modules)
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }
}
