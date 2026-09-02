package garcias.api.content.domain.valueobjects;

import java.util.List;

public record Estatisticas(
        List<Indicador> lista
) {}