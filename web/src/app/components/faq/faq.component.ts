import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface FaqItem {
  pergunta: string;
  resposta: string;
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.css'
})
export class FaqComponent {
  readonly faqs: FaqItem[] = [
    {
      pergunta: 'Como faço para pedir ou cotar produtos da Eloá Panificações?',
      resposta: 'Você pode consultar todo o nosso catálogo online e clicar no botão de pedido para falar diretamente com nossa equipe comercial pelo WhatsApp. Oferecemos atendimento ágil e cotações personalizadas para sua necessidade.'
    },
    {
      pergunta: 'Quais tipos de produtos a Eloá comercializa?',
      resposta: 'Trabalhamos com uma linha completa de panificação tradicional e especial, pães artesanais, bolos caseiros e recheados, doces finos, salgados para festas e eventos, além de fornecimento no atacado.'
    },
    {
      pergunta: 'A Eloá atende compras no atacado para padarias, mercados e restaurantes?',
      resposta: 'Sim! Atendemos tanto pedidos pontuais quanto fornecimento contínuo em grande volume no atacado para estabelecimentos comerciais, com condições comerciais exclusivas e entregas programadas.'
    },
    {
      pergunta: 'Quais regiões são atendidas para entregas e fornecimento?',
      resposta: 'Nossa distribuição especializada atende toda a Grande São Paulo, Região Metropolitana de Campinas, Vale do Paraíba e Litoral Paulista com frota preparada para garantir produtos frescos.'
    },
    {
      pergunta: 'Quais são as formas de pagamento aceitas?',
      resposta: 'Aceitamos Pix, cartões de crédito e débito, dinheiro e condições especiais de faturamento para empresas e parceiros comerciais cadastrados.'
    }
  ];
}
