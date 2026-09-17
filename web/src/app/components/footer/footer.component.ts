import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentRodape, SiteData } from '../../models/content.model';
import { formatCnpj, formatWhatsappLink } from '../../core/utils/formatters.util';
import { smoothScrollToSection } from '../../core/utils/navigation.util';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent implements OnInit, OnDestroy {
  @Input() rodape?: ContentRodape | null;
  @Input() dados?: SiteData | null;

  currentYear: number = new Date().getFullYear();
  updateTimer?: ReturnType<typeof setTimeout>;

  onNavClick(event: Event, targetId: string): void {
    smoothScrollToSection(event, targetId);
  }

  ngOnInit(): void {
    this.updateCurrentYear();
    this.scheduleNextMonthlyUpdate();
  }

  ngOnDestroy(): void {
    if (this.updateTimer) {
      clearTimeout(this.updateTimer);
      this.updateTimer = undefined;
    }
  }

  updateCurrentYear(): void {
    this.currentYear = new Date().getFullYear();
  }

  scheduleNextMonthlyUpdate(): void {
    if (this.updateTimer) {
      clearTimeout(this.updateTimer);
      this.updateTimer = undefined;
    }

    const now = new Date();
    const nextMonthFirstDay = new Date(now.getFullYear(), now.getMonth() + 1, 1, 0, 0, 0, 0);
    const delay = Math.max(1000, nextMonthFirstDay.getTime() - now.getTime());
    const maxSafeTimeout = 24 * 60 * 60 * 1000;

    this.updateTimer = setTimeout(() => {
      this.updateCurrentYear();
      this.scheduleNextMonthlyUpdate();
    }, Math.min(delay, maxSafeTimeout));
  }

  get formattedCnpj(): string {
    const rawCnpj = this.dados?.cnpj?.trim() || '57.068.741/0001-38';
    return formatCnpj(rawCnpj);
  }

  get whatsappLink(): string {
    return formatWhatsappLink(this.dados?.whatsapp);
  }
}
