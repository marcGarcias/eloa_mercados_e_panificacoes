import { Component, OnInit, OnDestroy, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { HeroComponent } from '../hero/hero.component';
import { FeaturesComponent } from '../features/features.component';
import { CatalogComponent } from '../catalog/catalog.component';
import { AboutComponent } from '../about/about.component';
import { StatsComponent } from '../stats/stats.component';
import { CtaComponent } from '../cta/cta.component';
import { FaqComponent } from '../faq/faq.component';
import { FooterComponent } from '../footer/footer.component';
import { ContentService } from '../../services/content.service';
import { SeoService } from '../../services/seo.service';
import { SiteContent } from '../../models/content.model';
import { Subscription } from 'rxjs';

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
    FaqComponent,
    FooterComponent
  ],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit, OnDestroy {
  content: SiteContent | null = null;
  private readonly contentService = inject(ContentService);
  private readonly seoService = inject(SeoService);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly subs = new Subscription();

  ngOnInit(): void {
    // Configura SEO e Schema.org da Home (Catálogo & Pedidos via WhatsApp)
    this.seoService.updateMetaTags({
      title: 'Catálogo de Panificação & Pedidos via WhatsApp',
      description: 'Catálogo de produtos da Eloá Mercados & Panificações. Pães, doces, bolos, salgados e insumos de panificação. Consulte e faça seu pedido pelo WhatsApp.',
      canonicalUrl: 'https://eloapanificacoes.com.br/',
      ogTitle: 'Eloá Mercados & Panificações | Catálogo & Pedidos via WhatsApp',
      ogDescription: 'Consulte nossa linha completa de panificação e confeitaria. Faça seu pedido diretamente pelo WhatsApp com nossa equipe.',
      ogImage: 'https://eloapanificacoes.com.br/assets/images/og-eloa-banner.webp'
    });

    this.subs.add(
      this.contentService.getContentPublic().subscribe({
        next: (data) => {
          this.content = data;
          if (data) {
            this.seoService.updateFromSiteContent(data);
          }
          this.cdr.markForCheck();
        },
        error: () => {
          this.content = null;
          this.cdr.markForCheck();
        }
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }
}

