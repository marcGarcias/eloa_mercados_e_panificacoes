import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentFaq, ContentFaqItem } from '../../models/content.model';
import { DEFAULT_SITE_CONTENT } from '../../core/constants/content-fallbacks';

export type FaqItem = ContentFaqItem;

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.css'
})
export class FaqComponent {
  @Input() faq?: ContentFaq;

  readonly fallbackFaqs: ContentFaqItem[] = DEFAULT_SITE_CONTENT.faq!.itens;

  get itens(): ContentFaqItem[] {
    if (!this.faq?.itens || this.faq.itens.length === 0) {
      return this.fallbackFaqs;
    }
    return this.faq.itens.map(item => {
      const fallback = this.fallbackFaqs.find(f => f.id === item.id);
      return {
        id: item.id,
        pergunta: item.pergunta && item.pergunta.trim() ? item.pergunta.trim() : (fallback?.pergunta || ''),
        resposta: item.resposta && item.resposta.trim() ? item.resposta.trim() : (fallback?.resposta || '')
      };
    });
  }
}
