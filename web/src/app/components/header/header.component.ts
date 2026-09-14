import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SiteData } from '../../models/content.model';
import { formatWhatsappLink } from '../../core/utils/formatters.util';

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
    return formatWhatsappLink(this.dados?.whatsapp);
  }
}
