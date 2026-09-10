import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { HeroComponent } from '../hero/hero.component';
import { FeaturesComponent } from '../features/features.component';
import { CatalogComponent } from '../catalog/catalog.component';
import { AboutComponent } from '../about/about.component';
import { StatsComponent } from '../stats/stats.component';
import { CtaComponent } from '../cta/cta.component';
import { FooterComponent } from '../footer/footer.component';
import { ContentService } from '../../services/content.service';
import { SiteContent } from '../../models/content.model';
import { finalize, Subscription } from 'rxjs';

@Component({
  selector: 'app-home',
  imports: [
    CommonModule,
    HeaderComponent,
    HeroComponent,
    FeaturesComponent,
    CatalogComponent,
    AboutComponent,
    StatsComponent,
    CtaComponent,
    FooterComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  content: SiteContent | null = null;
  private readonly contentService = inject(ContentService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly subs = new Subscription();

  ngOnInit(): void {
    this.subs.add(
      this.contentService.getContentPublic().pipe(
        finalize(() => {
          this.cdr.detectChanges();
        })
      ).subscribe({
        next: (data) => {
          this.content = data;
          this.cdr.detectChanges();
        },
        error: () => {
          this.content = null;
          this.cdr.detectChanges();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}
