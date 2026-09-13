package garcias.api.content.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SiteContentDto and Inner DTOs Unit Tests")
class SiteContentDtoTest {

    @Test
    @DisplayName("Deve cobrir todos os getters e setters de SiteContentDto e seus sub-DTOs")
    void shouldCoverAllGettersAndSetters() {
        SiteContentDto root = new SiteContentDto();

        SiteContentDto.IndicadorDto indicador = new SiteContentDto.IndicadorDto();
        indicador.setNome("Tradição");
        indicador.setValor("30+");
        assertThat(indicador.getNome()).isEqualTo("Tradição");
        assertThat(indicador.getValor()).isEqualTo("30+");

        SiteContentDto.BannerDto banner = new SiteContentDto.BannerDto();
        banner.setSelo("Selo");
        banner.setTitulo("Título");
        banner.setSubtitulo("Sub");
        banner.setDescricao("Desc");
        banner.setIndicadores(List.of(indicador));
        root.setBanner(banner);
        assertThat(root.getBanner().getSelo()).isEqualTo("Selo");
        assertThat(root.getBanner().getTitulo()).isEqualTo("Título");
        assertThat(root.getBanner().getSubtitulo()).isEqualTo("Sub");
        assertThat(root.getBanner().getDescricao()).isEqualTo("Desc");
        assertThat(root.getBanner().getIndicadores()).hasSize(1);

        SiteContentDto.CardDto card = new SiteContentDto.CardDto();
        card.setTitulo("Card Titulo");
        card.setTexto("Card Texto");
        assertThat(card.getTitulo()).isEqualTo("Card Titulo");
        assertThat(card.getTexto()).isEqualTo("Card Texto");

        SiteContentDto.DiferenciaisDto diferenciais = new SiteContentDto.DiferenciaisDto();
        diferenciais.setSelo("Selo Dif");
        diferenciais.setTitulo("Tit Dif");
        diferenciais.setDescricao("Desc Dif");
        diferenciais.setCards(List.of(card));
        root.setDiferenciais(diferenciais);
        assertThat(root.getDiferenciais().getSelo()).isEqualTo("Selo Dif");
        assertThat(root.getDiferenciais().getTitulo()).isEqualTo("Tit Dif");
        assertThat(root.getDiferenciais().getDescricao()).isEqualTo("Desc Dif");
        assertThat(root.getDiferenciais().getCards()).hasSize(1);

        SiteContentDto.CatalogoDto catalogo = new SiteContentDto.CatalogoDto();
        catalogo.setSelo("Selo Cat");
        catalogo.setDescricao("Desc Cat");
        root.setCatalogo(catalogo);
        assertThat(root.getCatalogo().getSelo()).isEqualTo("Selo Cat");
        assertThat(root.getCatalogo().getDescricao()).isEqualTo("Desc Cat");

        SiteContentDto.DescricaoItemDto descItem = new SiteContentDto.DescricaoItemDto();
        descItem.setNome("Chave");
        descItem.setDescricao("Valor");
        assertThat(descItem.getNome()).isEqualTo("Chave");
        assertThat(descItem.getDescricao()).isEqualTo("Valor");

        SiteContentDto.SobreDto sobre = new SiteContentDto.SobreDto();
        sobre.setSelo("Selo Sobre");
        sobre.setTitulo("Tit Sobre");
        sobre.setDescricao("Desc Sobre");
        sobre.setLista(List.of(descItem));
        root.setSobre(sobre);
        assertThat(root.getSobre().getSelo()).isEqualTo("Selo Sobre");
        assertThat(root.getSobre().getTitulo()).isEqualTo("Tit Sobre");
        assertThat(root.getSobre().getDescricao()).isEqualTo("Desc Sobre");
        assertThat(root.getSobre().getLista()).hasSize(1);

        SiteContentDto.EstatisticasDto estatisticas = new SiteContentDto.EstatisticasDto();
        estatisticas.setLista(List.of(indicador));
        root.setEstatisticas(estatisticas);
        assertThat(root.getEstatisticas().getLista()).hasSize(1);

        SiteContentDto.CtaDto cta = new SiteContentDto.CtaDto();
        cta.setSelo("Selo CTA");
        cta.setTitulo("Tit CTA");
        cta.setDescricao("Desc CTA");
        root.setCta(cta);
        assertThat(root.getCta().getSelo()).isEqualTo("Selo CTA");
        assertThat(root.getCta().getTitulo()).isEqualTo("Tit CTA");
        assertThat(root.getCta().getDescricao()).isEqualTo("Desc CTA");

        SiteContentDto.RodapeDto rodape = new SiteContentDto.RodapeDto();
        rodape.setDescricao("Desc Rodape");
        rodape.setTextoContato("Contato");
        rodape.setTextoDireitos("Direitos");
        root.setRodape(rodape);
        assertThat(root.getRodape().getDescricao()).isEqualTo("Desc Rodape");
        assertThat(root.getRodape().getTextoContato()).isEqualTo("Contato");
        assertThat(root.getRodape().getTextoDireitos()).isEqualTo("Direitos");

        SiteContentDto.DadosDto dados = new SiteContentDto.DadosDto();
        dados.setEndereco("Rua A");
        dados.setHorarioAbertura("06:00");
        dados.setHorarioFechamento("20:00");
        dados.setDiasFuncionamento("Seg a Sab");
        dados.setWhatsapp("11999999999");
        dados.setCnpj("00.000.000/0001-00");
        root.setDados(dados);
        assertThat(root.getDados().getEndereco()).isEqualTo("Rua A");
        assertThat(root.getDados().getHorarioAbertura()).isEqualTo("06:00");
        assertThat(root.getDados().getHorarioFechamento()).isEqualTo("20:00");
        assertThat(root.getDados().getDiasFuncionamento()).isEqualTo("Seg a Sab");
        assertThat(root.getDados().getWhatsapp()).isEqualTo("11999999999");
        assertThat(root.getDados().getCnpj()).isEqualTo("00.000.000/0001-00");

        SiteContentDto.FaqItemDto faqItem = new SiteContentDto.FaqItemDto();
        faqItem.setId("faq-1");
        faqItem.setPergunta("Pergunta?");
        faqItem.setResposta("Resposta.");
        assertThat(faqItem.getId()).isEqualTo("faq-1");
        assertThat(faqItem.getPergunta()).isEqualTo("Pergunta?");
        assertThat(faqItem.getResposta()).isEqualTo("Resposta.");

        SiteContentDto.FaqDto faq = new SiteContentDto.FaqDto();
        faq.setItens(List.of(faqItem));
        root.setFaq(faq);
        assertThat(root.getFaq().getItens()).hasSize(1);
    }
}
