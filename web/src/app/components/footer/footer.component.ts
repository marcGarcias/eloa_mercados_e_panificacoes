import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentRodape, SiteData } from '../../models/content.model';

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
    const maxSafeTimeout = 24 * 60 * 60 * 1000; // 24 horas

    this.updateTimer = setTimeout(() => {
      this.updateCurrentYear();
      this.scheduleNextMonthlyUpdate();
    }, Math.min(delay, maxSafeTimeout));
  }

  get whatsappLink(): string {
    if (!this.dados || !this.dados.whatsapp) {
      return 'https://wa.me/';
    }
    const cleanNumber = this.dados.whatsapp.replace(/\D/g, '');
    return `https://wa.me/${cleanNumber}`;
  }
}
