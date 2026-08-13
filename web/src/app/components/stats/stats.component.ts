import { Component, ElementRef, QueryList, ViewChildren, AfterViewInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';

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

  ngAfterViewInit() {
    this.observer = new IntersectionObserver((entries, obs) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          const el = entry.target as HTMLElement;
          const targetStr = el.getAttribute('data-target');
          if (targetStr) {
            const target = +targetStr;
            const prefix = el.getAttribute('data-prefix') || '';
            const suffix = el.getAttribute('data-suffix') || '';
            const duration = 2000;
            const stepTime = Math.abs(Math.floor(duration / target));

            let current = 0;
            const timer = setInterval(() => {
              current += 1;
              el.innerText = `${prefix}${current}${suffix}`;
              if (current >= target) {
                clearInterval(timer);
                el.innerText = `${prefix}${target}${suffix}`;
              }
            }, stepTime);
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
