import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { SiteContent } from '../models/content.model';

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private mockData: SiteContent = {
    banner: {
      selo: 'Bem-vindo à Eloa Mercados',
      titulo: 'Pães fresquinhos todos os dias.',
      subtitulo: 'A melhor padaria da região.',
      descricao: 'Doces, bolos, salgados, bebidas e produtos de mercado em um só lugar.',
      indicadores: [
        { nome: 'Anos de Tradição', valor: '10+' },
        { nome: 'Clientes Satisfeitos', valor: '5000+' },
        { nome: 'Produtos Diários', valor: '200+' }
      ]
    },
    diferenciais: {
      selo: 'Nossos Diferenciais',
      titulo: 'Por que escolher a Eloa?',
      descricao: 'Qualidade, atendimento e variedade para o seu dia a dia.',
      cards: [
        { titulo: 'Qualidade Premium', texto: 'Ingredientes selecionados para o melhor sabor.' },
        { titulo: 'Atendimento Rápido', texto: 'Sem filas, sem estresse.' },
        { titulo: 'Variedade', texto: 'Tudo o que você precisa em um só lugar.' }
      ]
    },
    catalogo: {
      selo: 'Nosso Catálogo',
      descricao: 'Conheça nossos produtos mais populares.'
    },
    sobre: {
      selo: 'Sobre Nós',
      titulo: 'Mais que uma padaria.',
      descricao: 'Na Eloa Mercados & Panificações acreditamos que pão quentinho é sinônimo de carinho. Produzimos diariamente pães, doces, bolos e salgados sempre buscando qualidade, sabor e atendimento próximo dos nossos clientes.',
      lista: [
        { nome: 'Missão', descricao: 'Levar qualidade para sua mesa.' },
        { nome: 'Visão', descricao: 'Ser a melhor padaria da cidade.' },
        { nome: 'Valores', descricao: 'Respeito, qualidade e tradição.' },
        { nome: 'Equipe', descricao: 'Profissionais dedicados e qualificados.' },
        { nome: 'Sustentabilidade', descricao: 'Compromisso com o meio ambiente.' }
      ]
    },
    estatisticas: {
      lista: [
        { nome: 'Pães Assados', valor: '1M+' },
        { nome: 'Bolos Vendidos', valor: '50k+' },
        { nome: 'Cafés Servidos', valor: '100k+' },
        { nome: 'Funcionários', valor: '25' }
      ]
    },
    cta: {
      selo: 'Faça seu Pedido',
      titulo: 'Pronto para experimentar?',
      descricao: 'Visite nossa loja ou faça seu pedido por WhatsApp.'
    },
    rodape: {
      descricao: 'Sua padaria e mercado de confiança todos os dias.',
      textoContato: 'Entre em contato pelo nosso WhatsApp ou redes sociais.',
      textoDireitos: '© 2026 Eloa Mercados & Panificações. Todos os direitos reservados.'
    },
    dados: {
      endereco: 'Rua Exemplo, 123, Centro, Sua Cidade - SP',
      horarioAbertura: '07:00',
      horarioFechamento: '19:00',
      diasFuncionamento: 'Segunda a Sábado',
      whatsapp: '(11) 94792-5296',
      cnpj: '00.000.000/0001-00'
    }
  };

  private contentSubject = new BehaviorSubject<SiteContent>(this.mockData);

  getContent(): Observable<SiteContent> {
    return this.contentSubject.asObservable();
  }

  saveContent(newContent: SiteContent): Observable<boolean> {
    // In a real app, this would be an HTTP call (e.g., this.http.put)
    this.contentSubject.next(newContent);
    return of(true);
  }
}
