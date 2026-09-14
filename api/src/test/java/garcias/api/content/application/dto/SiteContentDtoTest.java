package garcias.api.content.application.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SiteContentDto and Inner DTOs Unit Tests")
class SiteContentDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve validar limites de caracteres em BannerDto (selo 4-31, titulo 4-21, subtitulo 4-26, descricao 10-351)")
    void shouldValidateBannerDtoSizeLimits() {
        SiteContentDto.BannerDto validBanner = new SiteContentDto.BannerDto();
        validBanner.setSelo("Selo Válido");
        validBanner.setTitulo("Pães Frescos");
        validBanner.setSubtitulo("com sabor de sempre.");
        validBanner.setDescricao("Descrição com mais de 10 caracteres e menos de 351.");
        assertThat(validator.validate(validBanner)).isEmpty();

        SiteContentDto.BannerDto invalidBanner = new SiteContentDto.BannerDto();
        invalidBanner.setSelo("A"); // < 4
        invalidBanner.setTitulo("Título Muito Longo Acima De 21 Caracteres"); // > 21
        invalidBanner.setSubtitulo("Subtítulo Extremamente Longo Acima De 26 Caracteres"); // > 26
        invalidBanner.setDescricao("Curta"); // < 10
        assertThat(validator.validate(invalidBanner)).hasSize(4);
    }

    @Test
    @DisplayName("Deve validar limites de caracteres em DiferenciaisDto, CatalogoDto, SobreDto, CtaDto e RodapeDto")
    void shouldValidateOtherDtosSizeLimits() {
        SiteContentDto.DiferenciaisDto dif = new SiteContentDto.DiferenciaisDto();
        dif.setSelo("Por que a Eloa?");
        dif.setTitulo("Qualidade Garantida");
        dif.setDescricao("Cada produto é preparado com ingredientes frescos.");
        assertThat(validator.validate(dif)).isEmpty();

        SiteContentDto.CatalogoDto cat = new SiteContentDto.CatalogoDto();
        cat.setSelo("Nosso Cardápio");
        cat.setDescricao("Escolha seus favoritos pelo WhatsApp.");
        assertThat(validator.validate(cat)).isEmpty();

        SiteContentDto.SobreDto sobre = new SiteContentDto.SobreDto();
        sobre.setSelo("Nossa História");
        sobre.setTitulo("Tradição & Qualidade");
        sobre.setDescricao("Mais de uma década produzindo delícias artesanais.");
        assertThat(validator.validate(sobre)).isEmpty();

        SiteContentDto.CtaDto cta = new SiteContentDto.CtaDto();
        cta.setSelo("Seja Nosso Parceiro");
        cta.setTitulo("Seja Nosso Parceiro");
        cta.setDescricao("Entre em contato agora mesmo para encomendar.");
        assertThat(validator.validate(cta)).isEmpty();

        SiteContentDto.RodapeDto rodape = new SiteContentDto.RodapeDto();
        rodape.setDescricao("Pães frescos produzidos diariamente com tradição.");
        rodape.setTextoContato("Atendimento");
        assertThat(validator.validate(rodape)).isEmpty();
    }

    @Test
    @DisplayName("Deve validar limites contextuais em CardDto (titulo 4-35, texto 15-200)")
    void shouldValidateCardDtoContextualLimits() {
        SiteContentDto.CardDto validCard = new SiteContentDto.CardDto();
        validCard.setTitulo("Ingredientes Selecionados");
        validCard.setTexto("Usamos apenas farinha de qualidade para garantir o melhor sabor.");
        assertThat(validator.validate(validCard)).isEmpty();

        SiteContentDto.CardDto invalidCard = new SiteContentDto.CardDto();
        invalidCard.setTitulo("Oi"); // < 4
        invalidCard.setTexto("Curto"); // < 15
        assertThat(validator.validate(invalidCard)).hasSize(2);

        SiteContentDto.CardDto tooLongCard = new SiteContentDto.CardDto();
        tooLongCard.setTitulo("A".repeat(36)); // > 35
        tooLongCard.setTexto("A".repeat(201)); // > 200
        assertThat(validator.validate(tooLongCard)).hasSize(2);
    }

    @Test
    @DisplayName("Deve validar limites contextuais em IndicadorDto (nome 3-30, valor 1-15)")
    void shouldValidateIndicadorDtoContextualLimits() {
        SiteContentDto.IndicadorDto validInd = new SiteContentDto.IndicadorDto();
        validInd.setNome("Anos de Tradição");
        validInd.setValor("+10");
        assertThat(validator.validate(validInd)).isEmpty();

        SiteContentDto.IndicadorDto invalidInd = new SiteContentDto.IndicadorDto();
        invalidInd.setNome("Oi"); // < 3
        invalidInd.setValor("A".repeat(16)); // > 15
        assertThat(validator.validate(invalidInd)).hasSize(2);
    }

    @Test
    @DisplayName("Deve validar limites contextuais em DadosDto (endereco 8-150 com logradouro, diasFuncionamento 3-40)")
    void shouldValidateDadosDtoContextualLimits() {
        SiteContentDto.DadosDto validDados = new SiteContentDto.DadosDto();
        validDados.setEndereco("Rua Exemplo, 123 - Centro, São Paulo - SP");
        validDados.setDiasFuncionamento("Seg a Sáb");
        validDados.setWhatsapp("11999999999");
        validDados.setCnpj("00.000.000/0001-00");
        assertThat(validator.validate(validDados)).isEmpty();

        SiteContentDto.DadosDto invalidDados = new SiteContentDto.DadosDto();
        invalidDados.setEndereco("12345"); // < 8 e sem letras
        invalidDados.setDiasFuncionamento("<script>"); // caracteres inválidos
        assertThat(validator.validate(invalidDados)).hasSize(3); // min size endereco + pattern endereco + pattern dias
    }

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
        banner.setSelo("Selo Válido");
        banner.setTitulo("Pães Frescos");
        banner.setSubtitulo("com sabor de sempre.");
        banner.setDescricao("Descrição com mais de 10 caracteres.");
        banner.setIndicadores(List.of(indicador));
        root.setBanner(banner);
        assertThat(root.getBanner().getSelo()).isEqualTo("Selo Válido");
        assertThat(root.getBanner().getTitulo()).isEqualTo("Pães Frescos");
        assertThat(root.getBanner().getSubtitulo()).isEqualTo("com sabor de sempre.");
        assertThat(root.getBanner().getDescricao()).isEqualTo("Descrição com mais de 10 caracteres.");
        assertThat(root.getBanner().getIndicadores()).hasSize(1);

        SiteContentDto.CardDto card = new SiteContentDto.CardDto();
        card.setTitulo("Card Titulo");
        card.setTexto("Card Texto");
        assertThat(card.getTitulo()).isEqualTo("Card Titulo");
        assertThat(card.getTexto()).isEqualTo("Card Texto");

        SiteContentDto.DiferenciaisDto diferenciais = new SiteContentDto.DiferenciaisDto();
        diferenciais.setSelo("Selo Dif");
        diferenciais.setTitulo("Tit Dif");
        diferenciais.setDescricao("Desc Dif com mais de 10 chars");
        diferenciais.setCards(List.of(card));
        root.setDiferenciais(diferenciais);
        assertThat(root.getDiferenciais().getSelo()).isEqualTo("Selo Dif");
        assertThat(root.getDiferenciais().getTitulo()).isEqualTo("Tit Dif");
        assertThat(root.getDiferenciais().getDescricao()).isEqualTo("Desc Dif com mais de 10 chars");
        assertThat(root.getDiferenciais().getCards()).hasSize(1);

        SiteContentDto.CatalogoDto catalogo = new SiteContentDto.CatalogoDto();
        catalogo.setSelo("Selo Cat");
        catalogo.setDescricao("Desc Cat com mais de 10 chars");
        root.setCatalogo(catalogo);
        assertThat(root.getCatalogo().getSelo()).isEqualTo("Selo Cat");
        assertThat(root.getCatalogo().getDescricao()).isEqualTo("Desc Cat com mais de 10 chars");

        SiteContentDto.DescricaoItemDto descItem = new SiteContentDto.DescricaoItemDto();
        descItem.setNome("Chave");
        descItem.setDescricao("Valor");
        assertThat(descItem.getNome()).isEqualTo("Chave");
        assertThat(descItem.getDescricao()).isEqualTo("Valor");

        SiteContentDto.SobreDto sobre = new SiteContentDto.SobreDto();
        sobre.setSelo("Selo Sobre");
        sobre.setTitulo("Tit Sobre");
        sobre.setDescricao("Desc Sobre com mais de 10 chars");
        sobre.setLista(List.of(descItem));
        root.setSobre(sobre);
        assertThat(root.getSobre().getSelo()).isEqualTo("Selo Sobre");
        assertThat(root.getSobre().getTitulo()).isEqualTo("Tit Sobre");
        assertThat(root.getSobre().getDescricao()).isEqualTo("Desc Sobre com mais de 10 chars");
        assertThat(root.getSobre().getLista()).hasSize(1);

        SiteContentDto.EstatisticasDto estatisticas = new SiteContentDto.EstatisticasDto();
        estatisticas.setLista(List.of(indicador));
        root.setEstatisticas(estatisticas);
        assertThat(root.getEstatisticas().getLista()).hasSize(1);

        SiteContentDto.CtaDto cta = new SiteContentDto.CtaDto();
        cta.setSelo("Selo CTA");
        cta.setTitulo("Tit CTA");
        cta.setDescricao("Desc CTA com mais de 10 chars");
        root.setCta(cta);
        assertThat(root.getCta().getSelo()).isEqualTo("Selo CTA");
        assertThat(root.getCta().getTitulo()).isEqualTo("Tit CTA");
        assertThat(root.getCta().getDescricao()).isEqualTo("Desc CTA com mais de 10 chars");

        SiteContentDto.RodapeDto rodape = new SiteContentDto.RodapeDto();
        rodape.setDescricao("Desc Rodape com mais de 10 chars");
        rodape.setTextoContato("Contato");
        root.setRodape(rodape);
        assertThat(root.getRodape().getDescricao()).isEqualTo("Desc Rodape com mais de 10 chars");
        assertThat(root.getRodape().getTextoContato()).isEqualTo("Contato");

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
        faqItem.setResposta("Resposta com mais de 10 chars.");
        assertThat(faqItem.getId()).isEqualTo("faq-1");
        assertThat(faqItem.getPergunta()).isEqualTo("Pergunta?");
        assertThat(faqItem.getResposta()).isEqualTo("Resposta com mais de 10 chars.");

        SiteContentDto.FaqDto faq = new SiteContentDto.FaqDto();
        faq.setItens(List.of(faqItem));
        root.setFaq(faq);
        assertThat(root.getFaq().getItens()).hasSize(1);
    }
}
