import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentRodape, SiteData } from '../../models/content.model';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  @Input() rodape?: ContentRodape | null;
  @Input() dados?: SiteData | null;

  get whatsappLink(): string {
    if (!this.dados || !this.dados.whatsapp) {
      return 'https://wa.me/';
    }
    const cleanNumber = this.dados.whatsapp.replace(/\D/g, '');
    return `https://wa.me/${cleanNumber}`;
  }
}
