import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentCta, SiteData } from '../../models/content.model';
import { formatWhatsappLink } from '../../core/utils/formatters.util';

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
    return formatWhatsappLink(this.dados?.whatsapp);
  }
}
