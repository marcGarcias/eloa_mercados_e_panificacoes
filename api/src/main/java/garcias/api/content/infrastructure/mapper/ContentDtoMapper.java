package garcias.api.content.infrastructure.mapper;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.*;
import java.util.stream.Collectors;

public class ContentDtoMapper {

    private static String blankToNull(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }
        return s.trim();
    }

    public static SiteContent toDomain(SiteContentDto dto) {
        if (dto == null) return null;

        var bannerDto = dto.getBanner() != null ? dto.getBanner() : new SiteContentDto.BannerDto();
        var bannerInds = bannerDto.getIndicadores() != null ? bannerDto.getIndicadores() : java.util.Collections.<SiteContentDto.IndicadorDto>emptyList();
        var banner = new Banner(
                blankToNull(bannerDto.getSelo()),
                blankToNull(bannerDto.getTitulo()),
                blankToNull(bannerDto.getSubtitulo()),
                blankToNull(bannerDto.getDescricao()),
                bannerInds.stream()
                        .map(ind -> new Indicador(ind != null ? blankToNull(ind.getNome()) : null, ind != null ? blankToNull(ind.getValor()) : null))
                        .collect(Collectors.toList())
        );

        var difDto = dto.getDiferenciais() != null ? dto.getDiferenciais() : new SiteContentDto.DiferenciaisDto();
        var difCards = difDto.getCards() != null ? difDto.getCards() : java.util.Collections.<SiteContentDto.CardDto>emptyList();
        var diferenciais = new Diferenciais(
                blankToNull(difDto.getSelo()),
                blankToNull(difDto.getTitulo()),
                blankToNull(difDto.getDescricao()),
                difCards.stream()
                        .map(c -> new Card(c != null ? blankToNull(c.getTitulo()) : null, c != null ? blankToNull(c.getTexto()) : null))
                        .collect(Collectors.toList())
        );

        var catDto = dto.getCatalogo() != null ? dto.getCatalogo() : new SiteContentDto.CatalogoDto();
        var catalogo = new Catalogo(
                blankToNull(catDto.getSelo()),
                blankToNull(catDto.getDescricao())
        );

        var sobreDto = dto.getSobre() != null ? dto.getSobre() : new SiteContentDto.SobreDto();
        var sobreLista = sobreDto.getLista() != null ? sobreDto.getLista() : java.util.Collections.<SiteContentDto.DescricaoItemDto>emptyList();
        var sobre = new Sobre(
                blankToNull(sobreDto.getSelo()),
                blankToNull(sobreDto.getTitulo()),
                blankToNull(sobreDto.getDescricao()),
                sobreLista.stream()
                        .map(item -> new DescricaoItem(item != null ? blankToNull(item.getNome()) : null, item != null ? blankToNull(item.getDescricao()) : null))
                        .collect(Collectors.toList())
        );

        var estDto = dto.getEstatisticas() != null ? dto.getEstatisticas() : new SiteContentDto.EstatisticasDto();
        var estLista = estDto.getLista() != null ? estDto.getLista() : java.util.Collections.<SiteContentDto.IndicadorDto>emptyList();
        var estatisticas = new Estatisticas(
                estLista.stream()
                        .map(est -> new Indicador(est != null ? blankToNull(est.getNome()) : null, est != null ? blankToNull(est.getValor()) : null))
                        .collect(Collectors.toList())
        );

        var ctaDto = dto.getCta() != null ? dto.getCta() : new SiteContentDto.CtaDto();
        var cta = new Cta(
                blankToNull(ctaDto.getSelo()),
                blankToNull(ctaDto.getTitulo()),
                blankToNull(ctaDto.getDescricao())
        );

        var rodDto = dto.getRodape() != null ? dto.getRodape() : new SiteContentDto.RodapeDto();
        var rodape = new Rodape(
                blankToNull(rodDto.getDescricao()),
                blankToNull(rodDto.getTextoContato()),
                blankToNull(rodDto.getTextoDireitos())
        );

        var dadDto = dto.getDados() != null ? dto.getDados() : new SiteContentDto.DadosDto();
        var dados = new Dados(
                blankToNull(dadDto.getEndereco()),
                blankToNull(dadDto.getHorarioAbertura()),
                blankToNull(dadDto.getHorarioFechamento()),
                blankToNull(dadDto.getDiasFuncionamento()),
                blankToNull(dadDto.getWhatsapp()),
                blankToNull(dadDto.getCnpj())
        );

        var faqDto = dto.getFaq();
        Faq faq;
        if (faqDto != null && faqDto.getItens() != null && !faqDto.getItens().isEmpty()) {
            var respostaMap = faqDto.getItens().stream()
                    .filter(item -> item != null && item.getId() != null)
                    .collect(Collectors.toMap(
                            SiteContentDto.FaqItemDto::getId,
                            item -> blankToNull(item.getResposta()),
                            (first, duplicate) -> first
                    ));

            var itens = FaqCanonical.CANONICAL_ITEMS.stream()
                    .map(canonico -> {
                        if (respostaMap.containsKey(canonico.id())) {
                            return canonico.withResposta(respostaMap.get(canonico.id()));
                        }
                        return canonico;
                    })
                    .collect(Collectors.toList());
            faq = new Faq(itens);
        } else {
            faq = Faq.defaultFaq();
        }

        return new SiteContent(banner, diferenciais, catalogo, sobre, estatisticas, cta, rodape, dados, faq);
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

        var faqDto = new SiteContentDto.FaqDto();
        if (domain.getFaq() != null && domain.getFaq().itens() != null) {
            faqDto.setItens(domain.getFaq().itens().stream()
                    .map(item -> new SiteContentDto.FaqItemDto(item.id(), item.pergunta(), item.resposta()))
                    .collect(Collectors.toList()));
        } else {
            faqDto.setItens(FaqCanonical.CANONICAL_ITEMS.stream()
                    .map(item -> new SiteContentDto.FaqItemDto(item.id(), item.pergunta(), item.resposta()))
                    .collect(Collectors.toList()));
        }
        dto.setFaq(faqDto);

        return dto;
    }

    public static SiteContent merge(SiteContent existing, SiteContentDto patch) {
        if (patch == null) return existing;
        if (existing == null) return toDomain(patch);

        var existingDto = toDto(existing);

        if (patch.getBanner() != null) {
            if (existingDto.getBanner() == null) existingDto.setBanner(new SiteContentDto.BannerDto());
            existingDto.getBanner().setSelo(blankToNull(patch.getBanner().getSelo()));
            existingDto.getBanner().setTitulo(blankToNull(patch.getBanner().getTitulo()));
            existingDto.getBanner().setSubtitulo(blankToNull(patch.getBanner().getSubtitulo()));
            existingDto.getBanner().setDescricao(blankToNull(patch.getBanner().getDescricao()));
            if (patch.getBanner().getIndicadores() != null) {
                existingDto.getBanner().setIndicadores(patch.getBanner().getIndicadores().stream()
                        .map(ind -> {
                            var i = new SiteContentDto.IndicadorDto();
                            i.setNome(ind != null ? blankToNull(ind.getNome()) : null);
                            i.setValor(ind != null ? blankToNull(ind.getValor()) : null);
                            return i;
                        }).collect(Collectors.toList()));
            }
        }

        if (patch.getDiferenciais() != null) {
            if (existingDto.getDiferenciais() == null) existingDto.setDiferenciais(new SiteContentDto.DiferenciaisDto());
            existingDto.getDiferenciais().setSelo(blankToNull(patch.getDiferenciais().getSelo()));
            existingDto.getDiferenciais().setTitulo(blankToNull(patch.getDiferenciais().getTitulo()));
            existingDto.getDiferenciais().setDescricao(blankToNull(patch.getDiferenciais().getDescricao()));
            if (patch.getDiferenciais().getCards() != null) {
                existingDto.getDiferenciais().setCards(patch.getDiferenciais().getCards().stream()
                        .map(c -> {
                            var card = new SiteContentDto.CardDto();
                            card.setTitulo(c != null ? blankToNull(c.getTitulo()) : null);
                            card.setTexto(c != null ? blankToNull(c.getTexto()) : null);
                            return card;
                        }).collect(Collectors.toList()));
            }
        }

        if (patch.getCatalogo() != null) {
            if (existingDto.getCatalogo() == null) existingDto.setCatalogo(new SiteContentDto.CatalogoDto());
            existingDto.getCatalogo().setSelo(blankToNull(patch.getCatalogo().getSelo()));
            existingDto.getCatalogo().setDescricao(blankToNull(patch.getCatalogo().getDescricao()));
        }

        if (patch.getSobre() != null) {
            if (existingDto.getSobre() == null) existingDto.setSobre(new SiteContentDto.SobreDto());
            existingDto.getSobre().setSelo(blankToNull(patch.getSobre().getSelo()));
            existingDto.getSobre().setTitulo(blankToNull(patch.getSobre().getTitulo()));
            existingDto.getSobre().setDescricao(blankToNull(patch.getSobre().getDescricao()));
            if (patch.getSobre().getLista() != null) {
                existingDto.getSobre().setLista(patch.getSobre().getLista().stream()
                        .map(item -> {
                            var d = new SiteContentDto.DescricaoItemDto();
                            d.setNome(item != null ? blankToNull(item.getNome()) : null);
                            d.setDescricao(item != null ? blankToNull(item.getDescricao()) : null);
                            return d;
                        }).collect(Collectors.toList()));
            }
        }

        if (patch.getEstatisticas() != null) {
            if (existingDto.getEstatisticas() == null) existingDto.setEstatisticas(new SiteContentDto.EstatisticasDto());
            if (patch.getEstatisticas().getLista() != null) {
                existingDto.getEstatisticas().setLista(patch.getEstatisticas().getLista().stream()
                        .map(est -> {
                            var ind = new SiteContentDto.IndicadorDto();
                            ind.setNome(est != null ? blankToNull(est.getNome()) : null);
                            ind.setValor(est != null ? blankToNull(est.getValor()) : null);
                            return ind;
                        }).collect(Collectors.toList()));
            }
        }

        if (patch.getCta() != null) {
            if (existingDto.getCta() == null) existingDto.setCta(new SiteContentDto.CtaDto());
            existingDto.getCta().setSelo(blankToNull(patch.getCta().getSelo()));
            existingDto.getCta().setTitulo(blankToNull(patch.getCta().getTitulo()));
            existingDto.getCta().setDescricao(blankToNull(patch.getCta().getDescricao()));
        }

        if (patch.getRodape() != null) {
            if (existingDto.getRodape() == null) existingDto.setRodape(new SiteContentDto.RodapeDto());
            existingDto.getRodape().setDescricao(blankToNull(patch.getRodape().getDescricao()));
            existingDto.getRodape().setTextoContato(blankToNull(patch.getRodape().getTextoContato()));
            existingDto.getRodape().setTextoDireitos(blankToNull(patch.getRodape().getTextoDireitos()));
        }

        if (patch.getDados() != null) {
            if (existingDto.getDados() == null) existingDto.setDados(new SiteContentDto.DadosDto());
            existingDto.getDados().setEndereco(blankToNull(patch.getDados().getEndereco()));
            existingDto.getDados().setHorarioAbertura(blankToNull(patch.getDados().getHorarioAbertura()));
            existingDto.getDados().setHorarioFechamento(blankToNull(patch.getDados().getHorarioFechamento()));
            existingDto.getDados().setDiasFuncionamento(blankToNull(patch.getDados().getDiasFuncionamento()));
            existingDto.getDados().setWhatsapp(blankToNull(patch.getDados().getWhatsapp()));
            existingDto.getDados().setCnpj(blankToNull(patch.getDados().getCnpj()));
        }

        if (patch.getFaq() != null && patch.getFaq().getItens() != null) {
            java.util.List<FaqItem> baseItens = (existing != null && existing.getFaq() != null && existing.getFaq().itens() != null)
                    ? existing.getFaq().itens()
                    : FaqCanonical.CANONICAL_ITEMS;

            java.util.Map<String, String> novasRespostas = patch.getFaq().getItens().stream()
                    .filter(item -> item != null && item.getId() != null)
                    .collect(Collectors.toMap(
                            SiteContentDto.FaqItemDto::getId,
                            item -> blankToNull(item.getResposta()),
                            (primeiro, duplicado) -> primeiro
                    ));

            java.util.List<FaqItem> itensAtualizados = baseItens.stream()
                    .map(canonico -> {
                        if (novasRespostas.containsKey(canonico.id())) {
                            String novaResposta = novasRespostas.get(canonico.id());

                            return novaResposta != null ? canonico.withResposta(novaResposta) : canonico.withResposta(null);
                        }
                        return canonico;
                    })
                    .collect(Collectors.toList());

            var fDto = new SiteContentDto.FaqDto();
            fDto.setItens(itensAtualizados.stream()
                    .map(item -> new SiteContentDto.FaqItemDto(item.id(), item.pergunta(), item.resposta()))
                    .collect(Collectors.toList()));
            existingDto.setFaq(fDto);
        }

        return toDomain(existingDto);
    }
}

