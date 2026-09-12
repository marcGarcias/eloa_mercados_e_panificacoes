import { Component, ElementRef, QueryList, ViewChildren, AfterViewInit, OnDestroy, Input, inject, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ContentEstatisticas } from '../../models/content.model';

interface ParsedStat {
  nome: string;
  valorOriginal: string;
  target: number | null;
  prefix: string;
  suffix: string;
  text: string;
}

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent implements AfterViewInit, OnDestroy {
  @ViewChildren('numeroVal') numberElements!: QueryList<ElementRef>;
  private observer: IntersectionObserver | null = null;
  private readonly ngZone = inject(NgZone);

  private _estatisticas: ContentEstatisticas | null | undefined = null;
  parsedStats: ParsedStat[] = [];

  @Input() set estatisticas(value: ContentEstatisticas | null | undefined) {
    this._estatisticas = value;
    if (value && value.lista) {
      this.parsedStats = value.lista.map(est => {
        const parsed = this.parseStatValue(est.valor);
        return {
          nome: est.nome,
          valorOriginal: est.valor,
          target: parsed.target,
          prefix: parsed.prefix,
          suffix: parsed.suffix,
          text: parsed.text
        };
      });
      setTimeout(() => this.initObserver(), 100);
    } else {
      this.parsedStats = [];
    }
  }

  get estatisticas(): ContentEstatisticas | null | undefined {
    return this._estatisticas;
  }

  private parseStatValue(valor: string) {
    if (!valor) return { target: null, prefix: '', suffix: '', text: '' };
    
    const cleanNumStr = valor.replace(/[^\d]/g, '');
    const target = cleanNumStr ? parseInt(cleanNumStr, 10) : null;
    
    if (target === null || isNaN(target)) {
      return { target: null, prefix: '', suffix: '', text: valor };
    }
    
    const prefix = valor.startsWith('+') ? '+' : '';
    const suffix = valor.endsWith('%') ? '%' : (valor.endsWith('+') ? '' : valor.substring(valor.indexOf(target.toString()) + target.toString().length));
    
    return {
      target,
      prefix,
      suffix,
      text: valor
    };
  }

  ngAfterViewInit() {
    this.initObserver();
  }

  private initObserver() {
    if (this.observer) {
      this.observer.disconnect();
    }

    if (!this.numberElements || this.numberElements.length === 0) return;

    this.observer = new IntersectionObserver((entries, obs) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const el = entry.target as HTMLElement;
          const targetStr = el.getAttribute('data-target');
          if (targetStr) {
            const target = +targetStr;
            const prefix = el.getAttribute('data-prefix') || '';
            const suffix = el.getAttribute('data-suffix') || '';
            const duration = 1600;

            this.ngZone.runOutsideAngular(() => {
              const startTime = performance.now();
              const animate = (now: number) => {
                const elapsed = now - startTime;
                const progress = Math.min(elapsed / duration, 1);
                // easeOutExpo suave para contagem fluida
                const ease = progress === 1 ? 1 : 1 - Math.pow(2, -10 * progress);
                const current = Math.floor(ease * target);

                el.textContent = `${prefix}${current}${suffix}`;

                if (progress < 1) {
                  requestAnimationFrame(animate);
                } else {
                  el.textContent = `${prefix}${target}${suffix}`;
                }
              };
              requestAnimationFrame(animate);
            });
          }
          obs.unobserve(el);
        }
      });
    }, { threshold: 0.5 });

    this.numberElements.forEach(num => {
      this.observer?.observe(num.nativeElement);
    });
  }

  ngOnDestroy() {
    if (this.observer) {
      this.observer.disconnect();
    }
  }
}
