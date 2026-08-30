package garcias.api.content.domain.entities;

import java.util.List;

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


    // --- Sub-objetos do Agregado SiteContent ---

    public record Indicador(String nome, String valor) {}
    public record Card(String titulo, String texto) {}
    public record DescricaoItem(String nome, String descricao) {}

    public record Banner(
            String selo,
            String titulo,
            String subtitulo,
            String descricao,
            List<Indicador> indicadores
    ) {}

    public record Diferenciais(
            String selo,
            String titulo,
            String descricao,
            List<Card> cards
    ) {}

    public record Catalogo(
            String selo,
            String descricao
    ) {}

    public record Sobre(
            String selo,
            String titulo,
            String descricao,
            List<DescricaoItem> lista
    ) {}

    public record Estatisticas(
            List<Indicador> lista
    ) {}

    public record Cta(
            String selo,
            String titulo,
            String descricao
    ) {}

    public record Rodape(
            String descricao,
            String textoContato,
            String textoDireitos
    ) {}

    public record Dados(
            String endereco,
            String horarioAbertura,
            String horarioFechamento,
            String diasFuncionamento,
            String whatsapp,
            String cnpj
    ) {}
}
