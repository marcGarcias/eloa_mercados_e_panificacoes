UPDATE site_content
SET data = (
    data::jsonb || '{
        "faq": {
            "itens": [
                {
                    "id": "faq-1",
                    "pergunta": "Como faço para pedir ou cotar produtos da Eloá Panificações?",
                    "resposta": "Você pode consultar todo o nosso catálogo online e clicar no botão de pedido para falar diretamente com nossa equipe comercial pelo WhatsApp. Oferecemos atendimento ágil e cotações personalizadas para sua necessidade."
                },
                {
                    "id": "faq-2",
                    "pergunta": "Quais tipos de produtos a Eloá comercializa?",
                    "resposta": "Trabalhamos com uma linha completa de panificação tradicional e especial, pães artesanais, bolos caseiros e recheados, doces finos, salgados para festas e eventos, além de fornecimento no atacado."
                },
                {
                    "id": "faq-3",
                    "pergunta": "A Eloá atende compras no atacado para padarias, mercados e restaurantes?",
                    "resposta": "Sim! Atendemos tanto pedidos pontuais quanto fornecimento contínuo em grande volume no atacado para estabelecimentos comerciais, com condições comerciais exclusivas e entregas programadas."
                },
                {
                    "id": "faq-4",
                    "pergunta": "Quais regiões são atendidas para entregas e fornecimento?",
                    "resposta": "Nossa distribuição especializada atende toda a Grande São Paulo, Região Metropolitana de Campinas, Vale do Paraíba e Litoral Paulista com frota preparada para garantir produtos frescos."
                },
                {
                    "id": "faq-5",
                    "pergunta": "Quais são as formas de pagamento aceitas?",
                    "resposta": "Aceitamos Pix, cartões de crédito e débito, dinheiro e condições especiais de faturamento para empresas e parceiros comerciais cadastrados."
                }
            ]
        }
    }'::jsonb
)::text
WHERE id = 1
  AND NOT (data::jsonb ? 'faq');

