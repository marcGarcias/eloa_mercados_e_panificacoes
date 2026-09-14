package garcias.api.content.infrastructure.mapper;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ContentDtoMapper Full Unit Tests")
class ContentDtoMapperFullTest {

    @Test
    @DisplayName("Deve retornar nulo quando entradas forem nulas em toDomain e toDto")
    void shouldReturnNullWhenInputsAreNull() {
        assertThat(ContentDtoMapper.toDomain(null)).isNull();
        assertThat(ContentDtoMapper.toDto(null)).isNull();
    }

    @Test
    @DisplayName("Deve converter DTO completo para Domain e depois para DTO com sucesso")
    void shouldConvertFullDtoToDomainAndBackToDto() {
        SiteContentDto dto = new SiteContentDto();

        SiteContentDto.BannerDto banner = new SiteContentDto.BannerDto();
        banner.setSelo("Selo Banner");
        banner.setTitulo("Titulo Banner");
        banner.setSubtitulo("Subtitulo Banner");
        banner.setDescricao("Desc Banner");
        SiteContentDto.IndicadorDto ind = new SiteContentDto.IndicadorDto();
        ind.setNome("Ind 1");
        ind.setValor("100");
        banner.setIndicadores(List.of(ind));
        dto.setBanner(banner);

        SiteContentDto.DiferenciaisDto dif = new SiteContentDto.DiferenciaisDto();
        dif.setSelo("Selo Dif");
        dif.setTitulo("Titulo Dif");
        dif.setDescricao("Desc Dif");
        SiteContentDto.CardDto card = new SiteContentDto.CardDto();
        card.setTitulo("Card Tit");
        card.setTexto("Card Texto");
        dif.setCards(List.of(card));
        dto.setDiferenciais(dif);

        SiteContentDto.CatalogoDto cat = new SiteContentDto.CatalogoDto();
        cat.setSelo("Selo Cat");
        cat.setDescricao("Desc Cat");
        dto.setCatalogo(cat);

        SiteContentDto.SobreDto sobre = new SiteContentDto.SobreDto();
        sobre.setSelo("Selo Sobre");
        sobre.setTitulo("Tit Sobre");
        sobre.setDescricao("Desc Sobre");
        SiteContentDto.DescricaoItemDto descItem = new SiteContentDto.DescricaoItemDto();
        descItem.setNome("Desc Item");
        descItem.setDescricao("Desc Valor");
        sobre.setLista(List.of(descItem));
        dto.setSobre(sobre);

        SiteContentDto.EstatisticasDto est = new SiteContentDto.EstatisticasDto();
        est.setLista(List.of(ind));
        dto.setEstatisticas(est);

        SiteContentDto.CtaDto cta = new SiteContentDto.CtaDto();
        cta.setSelo("Selo CTA");
        cta.setTitulo("Tit CTA");
        cta.setDescricao("Desc CTA");
        dto.setCta(cta);

        SiteContentDto.RodapeDto rod = new SiteContentDto.RodapeDto();
        rod.setDescricao("Desc Rodape");
        rod.setTextoContato("Contato");
        dto.setRodape(rod);

        SiteContentDto.DadosDto dados = new SiteContentDto.DadosDto();
        dados.setEndereco("Rua 1");
        dados.setHorarioAbertura("08:00");
        dados.setHorarioFechamento("18:00");
        dados.setDiasFuncionamento("Seg-Sex");
        dados.setWhatsapp("11999999999");
        dados.setCnpj("00.000.000/0001-00");
        dto.setDados(dados);

        SiteContentDto.FaqDto faq = new SiteContentDto.FaqDto();
        SiteContentDto.FaqItemDto faqItem = new SiteContentDto.FaqItemDto("faq-1", "Pergunta?", "Nova Resposta Customizada");
        faq.setItens(List.of(faqItem));
        dto.setFaq(faq);

        SiteContent domain = ContentDtoMapper.toDomain(dto);

        assertThat(domain).isNotNull();
        assertThat(domain.getBanner().selo()).isEqualTo("Selo Banner");
        assertThat(domain.getBanner().indicadores()).hasSize(1);
        assertThat(domain.getDiferenciais().cards()).hasSize(1);
        assertThat(domain.getCatalogo().selo()).isEqualTo("Selo Cat");
        assertThat(domain.getSobre().lista()).hasSize(1);
        assertThat(domain.getEstatisticas().lista()).hasSize(1);
        assertThat(domain.getCta().titulo()).isEqualTo("Tit CTA");
        assertThat(domain.getRodape().descricao()).isEqualTo("Desc Rodape");
        assertThat(domain.getDados().endereco()).isEqualTo("Rua 1");
        assertThat(domain.getFaq().itens()).isNotEmpty();

        SiteContentDto convertedDto = ContentDtoMapper.toDto(domain);

        assertThat(convertedDto).isNotNull();
        assertThat(convertedDto.getBanner().getTitulo()).isEqualTo("Titulo Banner");
        assertThat(convertedDto.getDiferenciais().getTitulo()).isEqualTo("Titulo Dif");
        assertThat(convertedDto.getCatalogo().getDescricao()).isEqualTo("Desc Cat");
        assertThat(convertedDto.getSobre().getTitulo()).isEqualTo("Tit Sobre");
        assertThat(convertedDto.getEstatisticas().getLista()).hasSize(1);
        assertThat(convertedDto.getCta().getTitulo()).isEqualTo("Tit CTA");
        assertThat(convertedDto.getRodape().getTextoContato()).isEqualTo("Contato");
        assertThat(convertedDto.getDados().getCnpj()).isEqualTo("00.000.000/0001-00");
        assertThat(convertedDto.getFaq().getItens()).isNotEmpty();
    }

    @Test
    @DisplayName("Deve converter DTO vazio usando defaults e listas vazias")
    void shouldConvertEmptyDtoWithDefaults() {
        SiteContentDto emptyDto = new SiteContentDto();
        SiteContent domain = ContentDtoMapper.toDomain(emptyDto);

        assertThat(domain).isNotNull();
        assertThat(domain.getBanner().selo()).isNull();
        assertThat(domain.getBanner().indicadores()).isEmpty();
        assertThat(domain.getDiferenciais().cards()).isEmpty();
        assertThat(domain.getSobre().lista()).isEmpty();
        assertThat(domain.getEstatisticas().lista()).isEmpty();
        assertThat(domain.getFaq().itens()).isEqualTo(FaqCanonical.CANONICAL_ITEMS);
    }

    @Test
    @DisplayName("Deve realizar merge parcial de campos com sucesso")
    void shouldMergePatchCorrectly() {
        SiteContent existing = ContentDtoMapper.toDomain(new SiteContentDto());

        assertThat(ContentDtoMapper.merge(existing, null)).isSameAs(existing);
        assertThat(ContentDtoMapper.merge(null, new SiteContentDto())).isNotNull();

        SiteContentDto patch = new SiteContentDto();

        SiteContentDto.BannerDto bannerPatch = new SiteContentDto.BannerDto();
        bannerPatch.setTitulo("Novo Titulo Banner");
        SiteContentDto.IndicadorDto indPatch = new SiteContentDto.IndicadorDto();
        indPatch.setNome("Novo Ind");
        indPatch.setValor("50");
        bannerPatch.setIndicadores(List.of(indPatch));
        patch.setBanner(bannerPatch);

        SiteContentDto.DiferenciaisDto difPatch = new SiteContentDto.DiferenciaisDto();
        difPatch.setTitulo("Novo Dif");
        SiteContentDto.CardDto cardPatch = new SiteContentDto.CardDto();
        cardPatch.setTitulo("Card 1");
        cardPatch.setTexto("Texto 1");
        difPatch.setCards(List.of(cardPatch));
        patch.setDiferenciais(difPatch);

        SiteContentDto.CatalogoDto catPatch = new SiteContentDto.CatalogoDto();
        catPatch.setDescricao("Nova Desc Cat");
        patch.setCatalogo(catPatch);

        SiteContentDto.SobreDto sobrePatch = new SiteContentDto.SobreDto();
        sobrePatch.setTitulo("Novo Sobre");
        SiteContentDto.DescricaoItemDto itemPatch = new SiteContentDto.DescricaoItemDto();
        itemPatch.setNome("Item");
        itemPatch.setDescricao("Val");
        sobrePatch.setLista(List.of(itemPatch));
        patch.setSobre(sobrePatch);

        SiteContentDto.EstatisticasDto estPatch = new SiteContentDto.EstatisticasDto();
        SiteContentDto.IndicadorDto estIndPatch = new SiteContentDto.IndicadorDto();
        estIndPatch.setNome("Est");
        estIndPatch.setValor("99");
        estPatch.setLista(List.of(estIndPatch));
        patch.setEstatisticas(estPatch);

        SiteContentDto.CtaDto ctaPatch = new SiteContentDto.CtaDto();
        ctaPatch.setTitulo("Novo CTA");
        patch.setCta(ctaPatch);

        SiteContentDto.RodapeDto rodPatch = new SiteContentDto.RodapeDto();
        rodPatch.setTextoContato("Novo Contato");
        patch.setRodape(rodPatch);

        SiteContentDto.DadosDto dadPatch = new SiteContentDto.DadosDto();
        dadPatch.setWhatsapp("11888888888");
        patch.setDados(dadPatch);

        SiteContentDto.FaqDto faqPatch = new SiteContentDto.FaqDto();
        faqPatch.setItens(List.of(new SiteContentDto.FaqItemDto("faq-1", "Pergunta?", "Resposta Atualizada")));
        patch.setFaq(faqPatch);

        SiteContent merged = ContentDtoMapper.merge(existing, patch);

        assertThat(merged.getBanner().titulo()).isEqualTo("Novo Titulo Banner");
        assertThat(merged.getBanner().indicadores()).hasSize(1);
        assertThat(merged.getDiferenciais().titulo()).isEqualTo("Novo Dif");
        assertThat(merged.getCatalogo().descricao()).isEqualTo("Nova Desc Cat");
        assertThat(merged.getSobre().titulo()).isEqualTo("Novo Sobre");
        assertThat(merged.getEstatisticas().lista()).hasSize(1);
        assertThat(merged.getCta().titulo()).isEqualTo("Novo CTA");
        assertThat(merged.getRodape().textoContato()).isEqualTo("Novo Contato");
        assertThat(merged.getDados().whatsapp()).isEqualTo("11888888888");
        assertThat(merged.getFaq().itens().get(0).resposta()).isEqualTo("Resposta Atualizada");
    }
}
