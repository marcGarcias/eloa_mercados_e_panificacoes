package garcias.api.content.domain.valueobjects;

import java.util.List;

public record Banner(
        String selo,
        String titulo,
        String subtitulo,
        String descricao,
        List<Indicador> indicadores
) {}