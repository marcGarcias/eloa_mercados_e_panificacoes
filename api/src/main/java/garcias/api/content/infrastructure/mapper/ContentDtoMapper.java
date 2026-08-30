package garcias.api.content.infrastructure.mapper;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import java.util.stream.Collectors;

public class ContentDtoMapper {

    public static SiteContent toDomain(SiteContentDto dto) {
        if (dto == null) return null;

        var banner = new SiteContent.Banner(
                dto.getBanner().getSelo(),
                dto.getBanner().getTitulo(),
                dto.getBanner().getSubtitulo(),
                dto.getBanner().getDescricao(),
                dto.getBanner().getIndicadores().stream()
                        .map(ind -> new SiteContent.Indicador(ind.getNome(), ind.getValor()))
                        .collect(Collectors.toList())
        );

        var diferenciais = new SiteContent.Diferenciais(
                dto.getDiferenciais().getSelo(),
                dto.getDiferenciais().getTitulo(),
                dto.getDiferenciais().getDescricao(),
                dto.getDiferenciais().getCards().stream()
                        .map(c -> new SiteContent.Card(c.getTitulo(), c.getTexto()))
                        .collect(Collectors.toList())
        );

        var catalogo = new SiteContent.Catalogo(
                dto.getCatalogo().getSelo(),
                dto.getCatalogo().getDescricao()
        );

        var sobre = new SiteContent.Sobre(
                dto.getSobre().getSelo(),
                dto.getSobre().getTitulo(),
                dto.getSobre().getDescricao(),
                dto.getSobre().getLista().stream()
                        .map(item -> new SiteContent.DescricaoItem(item.getNome(), item.getDescricao()))
                        .collect(Collectors.toList())
        );

        var estatisticas = new SiteContent.Estatisticas(
                dto.getEstatisticas().getLista().stream()
                        .map(est -> new SiteContent.Indicador(est.getNome(), est.getValor()))
                        .collect(Collectors.toList())
        );

        var cta = new SiteContent.Cta(
                dto.getCta().getSelo(),
                dto.getCta().getTitulo(),
                dto.getCta().getDescricao()
        );

        var rodape = new SiteContent.Rodape(
                dto.getRodape().getDescricao(),
                dto.getRodape().getTextoContato(),
                dto.getRodape().getTextoDireitos()
        );

        var dados = new SiteContent.Dados(
                dto.getDados().getEndereco(),
                dto.getDados().getHorarioAbertura(),
                dto.getDados().getHorarioFechamento(),
                dto.getDados().getDiasFuncionamento(),
                dto.getDados().getWhatsapp(),
                dto.getDados().getCnpj()
        );

        return new SiteContent(banner, diferenciais, catalogo, sobre, estatisticas, cta, rodape, dados);
    }

    public static SiteContentDto toDto(SiteContent domain) {
        if (domain == null) return null;

        var dto = new SiteContentDto();

        var bannerDto = new SiteContentDto.BannerDto();
        bannerDto.setSelo(domain.getBanner().selo());
        bannerDto.setTitulo(domain.getBanner().titulo());
        bannerDto.setSubtitulo(domain.getBanner().subtitulo());
        bannerDto.setDescricao(domain.getBanner().descricao());
        bannerDto.setIndicadores(domain.getBanner().indicadores().stream().map(ind -> {
            var indDto = new SiteContentDto.IndicadorDto();
            indDto.setNome(ind.nome());
            indDto.setValor(ind.valor());
            return indDto;
        }).collect(Collectors.toList()));
        dto.setBanner(bannerDto);

        var difDto = new SiteContentDto.DiferenciaisDto();
        difDto.setSelo(domain.getDiferenciais().selo());
        difDto.setTitulo(domain.getDiferenciais().titulo());
        difDto.setDescricao(domain.getDiferenciais().descricao());
        difDto.setCards(domain.getDiferenciais().cards().stream().map(card -> {
            var cardDto = new SiteContentDto.CardDto();
            cardDto.setTitulo(card.titulo());
            cardDto.setTexto(card.texto());
            return cardDto;
        }).collect(Collectors.toList()));
        dto.setDiferenciais(difDto);

        var catDto = new SiteContentDto.CatalogoDto();
        catDto.setSelo(domain.getCatalogo().selo());
        catDto.setDescricao(domain.getCatalogo().descricao());
        dto.setCatalogo(catDto);

        var sobreDto = new SiteContentDto.SobreDto();
        sobreDto.setSelo(domain.getSobre().selo());
        sobreDto.setTitulo(domain.getSobre().titulo());
        sobreDto.setDescricao(domain.getSobre().descricao());
        sobreDto.setLista(domain.getSobre().lista().stream().map(item -> {
            var itemDto = new SiteContentDto.DescricaoItemDto();
            itemDto.setNome(item.nome());
            itemDto.setDescricao(item.descricao());
            return itemDto;
        }).collect(Collectors.toList()));
        dto.setSobre(sobreDto);

        var estDto = new SiteContentDto.EstatisticasDto();
        estDto.setLista(domain.getEstatisticas().lista().stream().map(est -> {
            var indDto = new SiteContentDto.IndicadorDto();
            indDto.setNome(est.nome());
            indDto.setValor(est.valor());
            return indDto;
        }).collect(Collectors.toList()));
        dto.setEstatisticas(estDto);

        var ctaDto = new SiteContentDto.CtaDto();
        ctaDto.setSelo(domain.getCta().selo());
        ctaDto.setTitulo(domain.getCta().titulo());
        ctaDto.setDescricao(domain.getCta().descricao());
        dto.setCta(ctaDto);

        var rodDto = new SiteContentDto.RodapeDto();
        rodDto.setDescricao(domain.getRodape().descricao());
        rodDto.setTextoContato(domain.getRodape().textoContato());
        rodDto.setTextoDireitos(domain.getRodape().textoDireitos());
        dto.setRodape(rodDto);

        var dadDto = new SiteContentDto.DadosDto();
        dadDto.setEndereco(domain.getDados().endereco());
        dadDto.setHorarioAbertura(domain.getDados().horarioAbertura());
        dadDto.setHorarioFechamento(domain.getDados().horarioFechamento());
        dadDto.setDiasFuncionamento(domain.getDados().diasFuncionamento());
        dadDto.setWhatsapp(domain.getDados().whatsapp());
        dadDto.setCnpj(domain.getDados().cnpj());
        dto.setDados(dadDto);

        return dto;
    }
}
