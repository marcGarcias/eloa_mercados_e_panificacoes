package garcias.api.content.domain.valueobjects;

import java.util.List;

public record Sobre(
        String selo,
        String titulo,
        String descricao,
        List<DescricaoItem> lista
) {}