import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentCta, SiteData } from '../../models/content.model';

@Component({
  selector: 'app-cta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cta.component.html',
  styleUrl: './cta.component.css'
})
export class CtaComponent {
  @Input() cta?: ContentCta | null;
  @Input() dados?: SiteData | null;

  get whatsappLink(): string {
    if (!this.dados || !this.dados.whatsapp) {
      return 'https://wa.me/';
    }
    const cleanNumber = this.dados.whatsapp.replace(/\D/g, '');
    return `https://wa.me/${cleanNumber}`;
  }
}
