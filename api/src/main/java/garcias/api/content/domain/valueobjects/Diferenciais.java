package garcias.api.content.domain.valueobjects;

import java.util.List;

public record Diferenciais(
        String selo,
        String titulo,
        String descricao,
        List<Card> cards
) {}