import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SiteData } from '../../models/content.model';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  @Input() dados?: SiteData | null;

  get whatsappLink(): string {
    if (!this.dados || !this.dados.whatsapp) {
      return 'https://wa.me/';
    }
    const cleanNumber = this.dados.whatsapp.replace(/\D/g, '');
    return `https://wa.me/${cleanNumber}`;
  }
}
