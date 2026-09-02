package garcias.api.content.infrastructure.mapper;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.*;
import java.util.stream.Collectors;

public class ContentDtoMapper {

    public static SiteContent toDomain(SiteContentDto dto) {
        if (dto == null) return null;

        var bannerDto = dto.getBanner() != null ? dto.getBanner() : new SiteContentDto.BannerDto();
        var bannerInds = bannerDto.getIndicadores() != null ? bannerDto.getIndicadores() : java.util.Collections.<SiteContentDto.IndicadorDto>emptyList();
        var banner = new Banner(
                bannerDto.getSelo(),
                bannerDto.getTitulo(),
                bannerDto.getSubtitulo(),
                bannerDto.getDescricao(),
                bannerInds.stream()
                        .map(ind -> new Indicador(ind.getNome(), ind.getValor()))
                        .collect(Collectors.toList())
        );

        var difDto = dto.getDiferenciais() != null ? dto.getDiferenciais() : new SiteContentDto.DiferenciaisDto();
        var difCards = difDto.getCards() != null ? difDto.getCards() : java.util.Collections.<SiteContentDto.CardDto>emptyList();
        var diferenciais = new Diferenciais(
                difDto.getSelo(),
                difDto.getTitulo(),
                difDto.getDescricao(),
                difCards.stream()
                        .map(c -> new Card(c.getTitulo(), c.getTexto()))
                        .collect(Collectors.toList())
        );

        var catDto = dto.getCatalogo() != null ? dto.getCatalogo() : new SiteContentDto.CatalogoDto();
        var catalogo = new Catalogo(
                catDto.getSelo(),
                catDto.getDescricao()
        );

        var sobreDto = dto.getSobre() != null ? dto.getSobre() : new SiteContentDto.SobreDto();
        var sobreLista = sobreDto.getLista() != null ? sobreDto.getLista() : java.util.Collections.<SiteContentDto.DescricaoItemDto>emptyList();
        var sobre = new Sobre(
                sobreDto.getSelo(),
                sobreDto.getTitulo(),
                sobreDto.getDescricao(),
                sobreLista.stream()
                        .map(item -> new DescricaoItem(item.getNome(), item.getDescricao()))
                        .collect(Collectors.toList())
        );

        var estDto = dto.getEstatisticas() != null ? dto.getEstatisticas() : new SiteContentDto.EstatisticasDto();
        var estLista = estDto.getLista() != null ? estDto.getLista() : java.util.Collections.<SiteContentDto.IndicadorDto>emptyList();
        var estatisticas = new Estatisticas(
                estLista.stream()
                        .map(est -> new Indicador(est.getNome(), est.getValor()))
                        .collect(Collectors.toList())
        );

        var ctaDto = dto.getCta() != null ? dto.getCta() : new SiteContentDto.CtaDto();
        var cta = new Cta(
                ctaDto.getSelo(),
                ctaDto.getTitulo(),
                ctaDto.getDescricao()
        );

        var rodDto = dto.getRodape() != null ? dto.getRodape() : new SiteContentDto.RodapeDto();
        var rodape = new Rodape(
                rodDto.getDescricao(),
                rodDto.getTextoContato(),
                rodDto.getTextoDireitos()
        );

        var dadDto = dto.getDados() != null ? dto.getDados() : new SiteContentDto.DadosDto();
        var dados = new Dados(
                dadDto.getEndereco(),
                dadDto.getHorarioAbertura(),
                dadDto.getHorarioFechamento(),
                dadDto.getDiasFuncionamento(),
                dadDto.getWhatsapp(),
                dadDto.getCnpj()
        );

        return new SiteContent(banner, diferenciais, catalogo, sobre, estatisticas, cta, rodape, dados);
    }

    public static SiteContentDto toDto(SiteContent domain) {
        if (domain == null) return null;
        var dto = new SiteContentDto();

        var bannerDto = new SiteContentDto.BannerDto();
        if (domain.getBanner() != null) {
            bannerDto.setSelo(domain.getBanner().selo());
            bannerDto.setTitulo(domain.getBanner().titulo());
            bannerDto.setSubtitulo(domain.getBanner().subtitulo());
            bannerDto.setDescricao(domain.getBanner().descricao());
            if (domain.getBanner().indicadores() != null) {
                bannerDto.setIndicadores(domain.getBanner().indicadores().stream()
                        .map(ind -> {
                            var i = new SiteContentDto.IndicadorDto();
                            i.setNome(ind.nome());
                            i.setValor(ind.valor());
                            return i;
                        }).collect(Collectors.toList()));
            }
        }
        dto.setBanner(bannerDto);

        var difDto = new SiteContentDto.DiferenciaisDto();
        if (domain.getDiferenciais() != null) {
            difDto.setSelo(domain.getDiferenciais().selo());
            difDto.setTitulo(domain.getDiferenciais().titulo());
            difDto.setDescricao(domain.getDiferenciais().descricao());
            if (domain.getDiferenciais().cards() != null) {
                difDto.setCards(domain.getDiferenciais().cards().stream()
                        .map(c -> {
                            var card = new SiteContentDto.CardDto();
                            card.setTitulo(c.titulo());
                            card.setTexto(c.texto());
                            return card;
                        }).collect(Collectors.toList()));
            }
        }
        dto.setDiferenciais(difDto);

        var catDto = new SiteContentDto.CatalogoDto();
        if (domain.getCatalogo() != null) {
            catDto.setSelo(domain.getCatalogo().selo());
            catDto.setDescricao(domain.getCatalogo().descricao());
        }
        dto.setCatalogo(catDto);

        var sobreDto = new SiteContentDto.SobreDto();
        if (domain.getSobre() != null) {
            sobreDto.setSelo(domain.getSobre().selo());
            sobreDto.setTitulo(domain.getSobre().titulo());
            sobreDto.setDescricao(domain.getSobre().descricao());
            if (domain.getSobre().lista() != null) {
                sobreDto.setLista(domain.getSobre().lista().stream()
                        .map(item -> {
                            var desc = new SiteContentDto.DescricaoItemDto();
                            desc.setNome(item.nome());
                            desc.setDescricao(item.descricao());
                            return desc;
                        }).collect(Collectors.toList()));
            }
        }
        dto.setSobre(sobreDto);

        var estDto = new SiteContentDto.EstatisticasDto();
        if (domain.getEstatisticas() != null && domain.getEstatisticas().lista() != null) {
            estDto.setLista(domain.getEstatisticas().lista().stream()
                    .map(est -> {
                        var ind = new SiteContentDto.IndicadorDto();
                        ind.setNome(est.nome());
                        ind.setValor(est.valor());
                        return ind;
                    }).collect(Collectors.toList()));
        }
        dto.setEstatisticas(estDto);

        var ctaDto = new SiteContentDto.CtaDto();
        if (domain.getCta() != null) {
            ctaDto.setSelo(domain.getCta().selo());
            ctaDto.setTitulo(domain.getCta().titulo());
            ctaDto.setDescricao(domain.getCta().descricao());
        }
        dto.setCta(ctaDto);

        var rodDto = new SiteContentDto.RodapeDto();
        if (domain.getRodape() != null) {
            rodDto.setDescricao(domain.getRodape().descricao());
            rodDto.setTextoContato(domain.getRodape().textoContato());
            rodDto.setTextoDireitos(domain.getRodape().textoDireitos());
        }
        dto.setRodape(rodDto);

        var dadDto = new SiteContentDto.DadosDto();
        if (domain.getDados() != null) {
            dadDto.setEndereco(domain.getDados().endereco());
            dadDto.setHorarioAbertura(domain.getDados().horarioAbertura());
            dadDto.setHorarioFechamento(domain.getDados().horarioFechamento());
            dadDto.setDiasFuncionamento(domain.getDados().diasFuncionamento());
            dadDto.setWhatsapp(domain.getDados().whatsapp());
            dadDto.setCnpj(domain.getDados().cnpj());
        }
        dto.setDados(dadDto);

        return dto;
    }

    public static SiteContent merge(SiteContent existing, SiteContentDto patch) {
        if (patch == null) return existing;
        if (existing == null) return toDomain(patch);

        var existingDto = toDto(existing);

        if (patch.getBanner() != null) {
            if (patch.getBanner().getSelo() != null) existingDto.getBanner().setSelo(patch.getBanner().getSelo());
            if (patch.getBanner().getTitulo() != null) existingDto.getBanner().setTitulo(patch.getBanner().getTitulo());
            if (patch.getBanner().getSubtitulo() != null) existingDto.getBanner().setSubtitulo(patch.getBanner().getSubtitulo());
            if (patch.getBanner().getDescricao() != null) existingDto.getBanner().setDescricao(patch.getBanner().getDescricao());
            if (patch.getBanner().getIndicadores() != null) existingDto.getBanner().setIndicadores(patch.getBanner().getIndicadores());
        }

        if (patch.getDiferenciais() != null) {
            if (patch.getDiferenciais().getSelo() != null) existingDto.getDiferenciais().setSelo(patch.getDiferenciais().getSelo());
            if (patch.getDiferenciais().getTitulo() != null) existingDto.getDiferenciais().setTitulo(patch.getDiferenciais().getTitulo());
            if (patch.getDiferenciais().getDescricao() != null) existingDto.getDiferenciais().setDescricao(patch.getDiferenciais().getDescricao());
            if (patch.getDiferenciais().getCards() != null) existingDto.getDiferenciais().setCards(patch.getDiferenciais().getCards());
        }

        if (patch.getCatalogo() != null) {
            if (patch.getCatalogo().getSelo() != null) existingDto.getCatalogo().setSelo(patch.getCatalogo().getSelo());
            if (patch.getCatalogo().getDescricao() != null) existingDto.getCatalogo().setDescricao(patch.getCatalogo().getDescricao());
        }

        if (patch.getSobre() != null) {
            if (patch.getSobre().getSelo() != null) existingDto.getSobre().setSelo(patch.getSobre().getSelo());
            if (patch.getSobre().getTitulo() != null) existingDto.getSobre().setTitulo(patch.getSobre().getTitulo());
            if (patch.getSobre().getDescricao() != null) existingDto.getSobre().setDescricao(patch.getSobre().getDescricao());
            if (patch.getSobre().getLista() != null) existingDto.getSobre().setLista(patch.getSobre().getLista());
        }

        if (patch.getEstatisticas() != null) {
            if (patch.getEstatisticas().getLista() != null) existingDto.getEstatisticas().setLista(patch.getEstatisticas().getLista());
        }

        if (patch.getCta() != null) {
            if (patch.getCta().getSelo() != null) existingDto.getCta().setSelo(patch.getCta().getSelo());
            if (patch.getCta().getTitulo() != null) existingDto.getCta().setTitulo(patch.getCta().getTitulo());
            if (patch.getCta().getDescricao() != null) existingDto.getCta().setDescricao(patch.getCta().getDescricao());
        }

        if (patch.getRodape() != null) {
            if (patch.getRodape().getDescricao() != null) existingDto.getRodape().setDescricao(patch.getRodape().getDescricao());
            if (patch.getRodape().getTextoContato() != null) existingDto.getRodape().setTextoContato(patch.getRodape().getTextoContato());
            if (patch.getRodape().getTextoDireitos() != null) existingDto.getRodape().setTextoDireitos(patch.getRodape().getTextoDireitos());
        }

        if (patch.getDados() != null) {
            if (patch.getDados().getEndereco() != null) existingDto.getDados().setEndereco(patch.getDados().getEndereco());
            if (patch.getDados().getHorarioAbertura() != null) existingDto.getDados().setHorarioAbertura(patch.getDados().getHorarioAbertura());
            if (patch.getDados().getHorarioFechamento() != null) existingDto.getDados().setHorarioFechamento(patch.getDados().getHorarioFechamento());
            if (patch.getDados().getDiasFuncionamento() != null) existingDto.getDados().setDiasFuncionamento(patch.getDados().getDiasFuncionamento());
            if (patch.getDados().getWhatsapp() != null) existingDto.getDados().setWhatsapp(patch.getDados().getWhatsapp());
            if (patch.getDados().getCnpj() != null) existingDto.getDados().setCnpj(patch.getDados().getCnpj());
        }

        return toDomain(existingDto);
    }
}
