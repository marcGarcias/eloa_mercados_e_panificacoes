package garcias.api.content.domain.entities;

import garcias.api.content.domain.valueobjects.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SiteContent Domain Entity and Value Objects Unit Tests")
class SiteContentTest {

    @Test
    @DisplayName("Deve instanciar SiteContent e acessar todos os componentes de domínio")
    void shouldCreateSiteContentAndAccessAllComponents() {
        Indicador indicador = new Indicador("Anos de tradição", "30+");
        Banner banner = new Banner("Selo", "Título Banner", "Sub", "Desc", List.of(indicador));

        Card card = new Card("Qualidade", "Melhores ingredientes");
        Diferenciais diferenciais = new Diferenciais("Selo", "Diferenciais", "Descrição", List.of(card));
        Catalogo catalogo = new Catalogo("Catálogo Selo", "Catálogo Desc");

        DescricaoItem descItem = new DescricaoItem("Fundação", "1995");
        Sobre sobre = new Sobre("Selo Sobre", "História", "Texto sobre", List.of(descItem));

        Estatisticas estatisticas = new Estatisticas(List.of(indicador));
        Cta cta = new Cta("Selo CTA", "Faça seu Pedido", "Fale Conosco pelo WhatsApp");

        Dados dados = new Dados(
                "Rua das Flores, 123", "06:00", "20:00",
                "Segunda a Sábado", "11999999999", "00.000.000/0001-00"
        );

        FaqItem faqItem = new FaqItem("faq-1", "Pergunta teste?", "Resposta teste.");
        Faq faq = new Faq(List.of(faqItem));

        Rodape rodape = new Rodape("Descrição Rodapé", "Contato", "© 2026 Eloá");

        SiteContent siteContent = new SiteContent(
                banner, diferenciais, catalogo, sobre, estatisticas, cta, rodape, dados, faq
        );

        assertThat(siteContent.banner()).isEqualTo(banner);
        assertThat(siteContent.getBanner()).isEqualTo(banner);
        assertThat(siteContent.diferenciais()).isEqualTo(diferenciais);
        assertThat(siteContent.getDiferenciais()).isEqualTo(diferenciais);
        assertThat(siteContent.catalogo()).isEqualTo(catalogo);
        assertThat(siteContent.getCatalogo()).isEqualTo(catalogo);
        assertThat(siteContent.sobre()).isEqualTo(sobre);
        assertThat(siteContent.getSobre()).isEqualTo(sobre);
        assertThat(siteContent.estatisticas()).isEqualTo(estatisticas);
        assertThat(siteContent.getEstatisticas()).isEqualTo(estatisticas);
        assertThat(siteContent.cta()).isEqualTo(cta);
        assertThat(siteContent.getCta()).isEqualTo(cta);
        assertThat(siteContent.rodape()).isEqualTo(rodape);
        assertThat(siteContent.getRodape()).isEqualTo(rodape);
        assertThat(siteContent.dados()).isEqualTo(dados);
        assertThat(siteContent.getDados()).isEqualTo(dados);
        assertThat(siteContent.faq()).isEqualTo(faq);
        assertThat(siteContent.getFaq()).isEqualTo(faq);

        assertThat(indicador.nome()).isEqualTo("Anos de tradição");
        assertThat(indicador.valor()).isEqualTo("30+");
        assertThat(card.titulo()).isEqualTo("Qualidade");
        assertThat(card.texto()).isEqualTo("Melhores ingredientes");
        assertThat(descItem.nome()).isEqualTo("Fundação");
        assertThat(descItem.descricao()).isEqualTo("1995");
        assertThat(faqItem.id()).isEqualTo("faq-1");
        assertThat(faqItem.pergunta()).isEqualTo("Pergunta teste?");
        assertThat(faqItem.resposta()).isEqualTo("Resposta teste.");
        assertThat(faqItem.withResposta("Nova Resposta").resposta()).isEqualTo("Nova Resposta");
    }

    @Test
    @DisplayName("Deve inicializar FAQ com lista canônica quando nula ou vazia e via defaultFaq()")
    void shouldInitializeFaqWithCanonicalWhenNullOrEmpty() {
        Faq faqNull = new Faq(null);
        Faq faqEmpty = new Faq(List.of());
        Faq defaultFaq = Faq.defaultFaq();

        assertThat(faqNull.itens()).isEqualTo(FaqCanonical.CANONICAL_ITEMS);
        assertThat(faqEmpty.itens()).isEqualTo(FaqCanonical.CANONICAL_ITEMS);
        assertThat(defaultFaq.itens()).isEqualTo(FaqCanonical.CANONICAL_ITEMS);
    }

    @Test
    @DisplayName("Deve validar a integridade dos itens canônicos do FAQ")
    void shouldValidateFaqCanonicalItems() {
        List<FaqItem> canonical = FaqCanonical.CANONICAL_ITEMS;

        assertThat(canonical).hasSize(5);
        assertThat(canonical.get(0).id()).isEqualTo("faq-1");
        assertThat(canonical.get(0).pergunta()).contains("pedir ou cotar produtos");
        assertThat(canonical.get(4).id()).isEqualTo("faq-5");
        assertThat(canonical.get(4).pergunta()).contains("formas de pagamento");
    }
}
