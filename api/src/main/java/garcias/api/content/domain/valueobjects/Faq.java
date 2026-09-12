package garcias.api.content.domain.valueobjects;

import java.util.List;

public record Faq(List<FaqItem> itens) {

    public Faq {
        if (itens == null || itens.isEmpty()) {
            itens = FaqCanonical.CANONICAL_ITEMS;
        }
    }

    public static Faq defaultFaq() {
        return new Faq(FaqCanonical.CANONICAL_ITEMS);
    }
}

