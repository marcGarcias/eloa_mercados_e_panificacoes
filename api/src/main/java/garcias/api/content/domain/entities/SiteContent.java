package garcias.api.content.domain.entities;

import garcias.api.content.domain.valueobjects.*;

public record SiteContent(
        Banner banner,
        Diferenciais diferenciais,
        Catalogo catalogo,
        Sobre sobre,
        Estatisticas estatisticas,
        Cta cta,
        Rodape rodape,
        Dados dados
) {
    public Banner getBanner() {
        return banner;
    }

    public Diferenciais getDiferenciais() {
        return diferenciais;
    }

    public Catalogo getCatalogo() {
        return catalogo;
    }

    public Sobre getSobre() {
        return sobre;
    }

    public Estatisticas getEstatisticas() {
        return estatisticas;
    }

    public Cta getCta() {
        return cta;
    }

    public Rodape getRodape() {
        return rodape;
    }

    public Dados getDados() {
        return dados;
    }
}