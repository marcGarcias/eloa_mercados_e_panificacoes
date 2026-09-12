package garcias.api.content.domain.valueobjects;

import java.util.List;

public final class FaqCanonical {

    public static final List<FaqItem> CANONICAL_ITEMS = List.of(
        new FaqItem(
            "faq-1",
            "Como faço para pedir ou cotar produtos da Eloá Panificações?",
            "Você pode consultar todo o nosso catálogo online e clicar no botão de pedido para falar diretamente com nossa equipe comercial pelo WhatsApp. Oferecemos atendimento ágil e cotações personalizadas para sua necessidade."
        ),
        new FaqItem(
            "faq-2",
            "Quais tipos de produtos a Eloá comercializa?",
            "Trabalhamos com uma linha completa de panificação tradicional e especial, pães artesanais, bolos caseiros e recheados, doces finos, salgados para festas e eventos, além de fornecimento no atacado."
        ),
        new FaqItem(
            "faq-3",
            "A Eloá atende compras no atacado para padarias, mercados e restaurantes?",
            "Sim! Atendemos tanto pedidos pontuais quanto fornecimento contínuo em grande volume no atacado para estabelecimentos comerciais, com condições comerciais exclusivas e entregas programadas."
        ),
        new FaqItem(
            "faq-4",
            "Quais regiões são atendidas para entregas e fornecimento?",
            "Nossa distribuição especializada atende toda a Grande São Paulo, Região Metropolitana de Campinas, Vale do Paraíba e Litoral Paulista com frota preparada para garantir produtos frescos."
        ),
        new FaqItem(
            "faq-5",
            "Quais são as formas de pagamento aceitas?",
            "Aceitamos Pix, cartões de crédito e débito, dinheiro e condições especiais de faturamento para empresas e parceiros comerciais cadastrados."
        )
    );

    private FaqCanonical() {}
}
