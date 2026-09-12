package garcias.api.content.infrastructure.mapper;

import garcias.api.content.application.dto.SiteContentDto;
import garcias.api.content.domain.entities.SiteContent;
import garcias.api.content.domain.valueobjects.Faq;
import garcias.api.content.domain.valueobjects.FaqCanonical;
import garcias.api.content.domain.valueobjects.FaqItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContentDtoMapperFaqTest {

    private SiteContent existingContent;

    @BeforeEach
    void setUp() {
        existingContent = new SiteContent(
                null, null, null, null, null, null, null, null,
                Faq.defaultFaq()
        );
    }

    @Test
    @DisplayName("Deve atualizar somente a resposta quando o payload for válido")
    void deveAtualizarSomenteRespostaQuandoPayloadValido() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-1", null, "Nova resposta personalizada para a pergunta 1.")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        assertNotNull(merged.faq());
        assertEquals(5, merged.faq().itens().size());

        FaqItem faq1 = merged.faq().itens().get(0);
        assertEquals("faq-1", faq1.id());
        assertEquals("Como faço para pedir ou cotar produtos da Eloá Panificações?", faq1.pergunta());
        assertEquals("Nova resposta personalizada para a pergunta 1.", faq1.resposta());
    }

    @Test
    @DisplayName("Deve ignorar qualquer tentativa de adulterar o texto da pergunta")
    void deveIgnorarTentativaDeAlterarPergunta() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-1", "PERGUNTA TOTALMENTE ALTERADA / HACKEADA", "Resposta legítima")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        FaqItem faq1 = merged.faq().itens().get(0);
        // A pergunta deve permanecer a canônica oficial, e não a enviada no patch!
        assertEquals("Como faço para pedir ou cotar produtos da Eloá Panificações?", faq1.pergunta());
        assertEquals("Resposta legítima", faq1.resposta());
    }

    @Test
    @DisplayName("Deve ignorar tentativa de injetar perguntas com IDs desconhecidos")
    void deveIgnorarTentativaDeAdicionarPerguntaComIdDesconhecido() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-999", "Pergunta invasora", "Resposta invasora")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        assertEquals(5, merged.faq().itens().size());
        boolean hasFaq999 = merged.faq().itens().stream().anyMatch(item -> "faq-999".equals(item.id()));
        assertFalse(hasFaq999, "O item com ID desconhecido faq-999 deve ser ignorado");
    }

    @Test
    @DisplayName("Deve preservar todas as outras perguntas quando o payload omitir itens")
    void devePreservarItensQuandoPayloadOmitirPerguntas() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        // Envia apenas alteração para o faq-3, omitindo faq-1, faq-2, faq-4 e faq-5
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-3", null, "Novo texto para o atacado.")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        assertEquals(5, merged.faq().itens().size());

        FaqItem faq3 = merged.faq().itens().stream().filter(i -> "faq-3".equals(i.id())).findFirst().orElseThrow();
        assertEquals("Novo texto para o atacado.", faq3.resposta());

        // As outras continuam intactas
        FaqItem faq1 = merged.faq().itens().stream().filter(i -> "faq-1".equals(i.id())).findFirst().orElseThrow();
        assertEquals(FaqCanonical.CANONICAL_ITEMS.get(0).resposta(), faq1.resposta());
    }

    @Test
    @DisplayName("Deve tratar IDs duplicados no payload sem duplicar itens no domínio")
    void deveTratarIdsDuplicadosSemDuplicarItens() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-2", null, "Primeira resposta duplicada"),
                new SiteContentDto.FaqItemDto("faq-2", null, "Segunda resposta duplicada")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        assertEquals(5, merged.faq().itens().size());
        long countFaq2 = merged.faq().itens().stream().filter(i -> "faq-2".equals(i.id())).count();
        assertEquals(1, countFaq2, "Não deve haver duplicação do item faq-2");
    }

    @Test
    @DisplayName("Deve manter exatamente os 5 itens canônicos na ordem oficial")
    void deveManterExatamente5ItensNaOrdemCanonica() {
        var patch = new SiteContentDto();
        var faqDto = new SiteContentDto.FaqDto();
        // Envia em ordem invertida
        faqDto.setItens(List.of(
                new SiteContentDto.FaqItemDto("faq-5", null, "Resposta 5"),
                new SiteContentDto.FaqItemDto("faq-1", null, "Resposta 1")
        ));
        patch.setFaq(faqDto);

        SiteContent merged = ContentDtoMapper.merge(existingContent, patch);

        List<FaqItem> itens = merged.faq().itens();
        assertEquals(5, itens.size());
        assertEquals("faq-1", itens.get(0).id());
        assertEquals("faq-2", itens.get(1).id());
        assertEquals("faq-3", itens.get(2).id());
        assertEquals("faq-4", itens.get(3).id());
        assertEquals("faq-5", itens.get(4).id());
    }
}
