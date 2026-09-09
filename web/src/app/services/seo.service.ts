import { Injectable, inject, RendererFactory2, Renderer2 } from '@angular/core';
import { Title, Meta } from '@angular/platform-browser';
import { DOCUMENT } from '@angular/common';

export interface SeoConfig {
  title?: string;
  description?: string;
  canonicalUrl?: string;
  keywords?: string;
  ogTitle?: string;
  ogDescription?: string;
  ogImage?: string;
  ogUrl?: string;
  ogType?: string;
  twitterTitle?: string;
  twitterDescription?: string;
  twitterImage?: string;
  noIndex?: boolean;
}

export interface ProductJsonLdData {
  name: string;
  description: string;
  sku?: string;
  mpn?: string;
  gtin13?: string;
  image?: string;
  category?: string;
  unitCode?: string;
  packagingInfo?: string;
  url?: string;
}

@Injectable({
  providedIn: 'root'
})
export class SeoService {
  private readonly titleService = inject(Title);
  private readonly metaService = inject(Meta);
  private readonly document = inject(DOCUMENT);
  private readonly rendererFactory = inject(RendererFactory2);
  private readonly renderer: Renderer2;

  private readonly siteName = 'Eloá Mercados & Panificações';
  private readonly baseUrl = 'https://eloapanificacoes.com.br';
  private readonly defaultOgImage = 'https://eloapanificacoes.com.br/assets/images/og-eloa-banner.jpg';

  constructor() {
    this.renderer = this.rendererFactory.createRenderer(null, null);
  }

  /**
   * Atualiza os metadados principais da página (Title, Description, Canonical, OG e Twitter Cards)
   */
  updateMetaTags(config: SeoConfig): void {
    const title = config.title ? `${config.title} | ${this.siteName}` : this.siteName;
    this.titleService.setTitle(title);

    const description = config.description || 'Catálogo e vitrine de produtos de panificação e confeitaria Eloá. Consulte nossa linha completa e faça seu pedido diretamente pelo WhatsApp.';
    this.metaService.updateTag({ name: 'description', content: description });

    if (config.keywords) {
      this.metaService.updateTag({ name: 'keywords', content: config.keywords });
    }

    // Robots
    if (config.noIndex) {
      this.metaService.updateTag({ name: 'robots', content: 'noindex, nofollow' });
    } else {
      this.metaService.updateTag({ name: 'robots', content: 'index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1' });
    }

    // Canonical
    this.setCanonicalUrl(config.canonicalUrl || this.baseUrl);

    // OpenGraph
    this.metaService.updateTag({ property: 'og:site_name', content: this.siteName });
    this.metaService.updateTag({ property: 'og:title', content: config.ogTitle || title });
    this.metaService.updateTag({ property: 'og:description', content: config.ogDescription || description });
    this.metaService.updateTag({ property: 'og:image', content: config.ogImage || this.defaultOgImage });
    this.metaService.updateTag({ property: 'og:url', content: config.ogUrl || config.canonicalUrl || this.baseUrl });
    this.metaService.updateTag({ property: 'og:type', content: config.ogType || 'website' });
    this.metaService.updateTag({ property: 'og:locale', content: 'pt_BR' });

    // Twitter
    this.metaService.updateTag({ name: 'twitter:card', content: 'summary_large_image' });
    this.metaService.updateTag({ name: 'twitter:title', content: config.twitterTitle || config.ogTitle || title });
    this.metaService.updateTag({ name: 'twitter:description', content: config.twitterDescription || config.ogDescription || description });
    this.metaService.updateTag({ name: 'twitter:image', content: config.twitterImage || config.ogImage || this.defaultOgImage });
  }

  /**
   * Define a tag canonical absoluta no head
   */
  setCanonicalUrl(url: string): void {
    let link: HTMLLinkElement | null = this.document.querySelector("link[rel='canonical']");
    if (!link) {
      link = this.renderer.createElement('link');
      this.renderer.setAttribute(link, 'rel', 'canonical');
      this.renderer.appendChild(this.document.head, link);
    }
    this.renderer.setAttribute(link, 'href', url);
  }

  /**
   * Injeta ou substitui o bloco de dados estruturados JSON-LD por ID
   */
  setJsonLd(schemaId: string, schemaData: object): void {
    const existingScript = this.document.getElementById(schemaId);
    if (existingScript) {
      this.renderer.removeChild(this.document.head, existingScript);
    }

    const script = this.renderer.createElement('script');
    this.renderer.setAttribute(script, 'type', 'application/ld+json');
    this.renderer.setAttribute(script, 'id', schemaId);
    this.renderer.setProperty(script, 'text', JSON.stringify(schemaData, null, 2));
    this.renderer.appendChild(this.document.head, script);
  }

  /**
   * Configura os dados estruturados da Home (Organization + LocalBusiness / Atendimento via WhatsApp)
   */
  setHomeStructuredData(): void {
    const homeSchema = {
      "@context": "https://schema.org",
      "@graph": [
        {
          "@type": "Organization",
          "@id": `${this.baseUrl}/#organization`,
          "name": "Eloá Mercados & Panificações",
          "legalName": "Eloá Mercados e Panificações Ltda.",
          "url": this.baseUrl,
          "logo": {
            "@type": "ImageObject",
            "@id": `${this.baseUrl}/#logo`,
            "url": `${this.baseUrl}/assets/images/logo-eloa.webp`,
            "contentUrl": `${this.baseUrl}/assets/images/logo-eloa.webp`,
            "caption": "Eloá Panificações e Alimentos para Atacado",
            "width": "512",
            "height": "512"
          },
          "image": {
            "@id": `${this.baseUrl}/#logo`
          },
          "sameAs": [
            "https://www.instagram.com/eloapanificacoes",
            "https://www.facebook.com/eloapanificacoes",
            "https://www.linkedin.com/company/eloapanificacoes"
          ],
          "contactPoint": [
            {
              "@type": "ContactPoint",
              "telephone": "+55-11-99999-9999",
              "contactType": "sales",
              "areaServed": "BR",
              "availableLanguage": ["Portuguese"]
            }
          ]
        },
        {
          "@type": ["Bakery", "WholesaleStore"],
          "@id": `${this.baseUrl}/#localbusiness`,
          "name": "Eloá Mercados & Panificações - Catálogo & Pedidos via WhatsApp",
          "image": `${this.baseUrl}/assets/images/fachada-fabrica-eloa.webp`,
          "url": this.baseUrl,
          "telephone": "+55-11-99999-9999",
          "priceRange": "$$",
          "servesCuisine": "Panificação, Confeitaria e Produtos para Revenda",
          "address": {
            "@type": "PostalAddress",
            "streetAddress": "Av. Principal das Indústrias, 1000",
            "addressLocality": "São Paulo",
            "addressRegion": "SP",
            "postalCode": "01000-000",
            "addressCountry": "BR"
          },
          "geo": {
            "@type": "GeoCoordinates",
            "latitude": -23.550520,
            "longitude": -46.633308
          },
          "openingHoursSpecification": [
            {
              "@type": "OpeningHoursSpecification",
              "dayOfWeek": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
              "opens": "07:00",
              "closes": "18:00"
            },
            {
              "@type": "OpeningHoursSpecification",
              "dayOfWeek": ["Saturday"],
              "opens": "07:00",
              "closes": "13:00"
            }
          ],
          "areaServed": [
            { "@type": "AdministrativeArea", "name": "Grande São Paulo" },
            { "@type": "AdministrativeArea", "name": "Região Metropolitana de Campinas" },
            { "@type": "AdministrativeArea", "name": "Vale do Paraíba" },
            { "@type": "AdministrativeArea", "name": "Litoral de São Paulo" }
          ],
          "parentOrganization": {
            "@id": `${this.baseUrl}/#organization`
          }
        }
      ]
    };

    this.setJsonLd('json-ld-home', homeSchema);
  }

  /**
   * Configura os dados estruturados de um Produto do Catálogo (Consulta via WhatsApp)
   */
  setProductStructuredData(data: ProductJsonLdData): void {
    const productUrl = data.url || this.baseUrl;
    const categoryName = data.category || 'Panificação';

    const productSchema = {
      "@context": "https://schema.org",
      "@graph": [
        {
          "@type": "BreadcrumbList",
          "@id": `${productUrl}#breadcrumb`,
          "itemListElement": [
            {
              "@type": "ListItem",
              "position": 1,
              "name": "Início",
              "item": this.baseUrl
            },
            {
              "@type": "ListItem",
              "position": 2,
              "name": "Catálogo",
              "item": `${this.baseUrl}/#catalog`
            },
            {
              "@type": "ListItem",
              "position": 3,
              "name": categoryName,
              "item": `${this.baseUrl}/#catalog`
            },
            {
              "@type": "ListItem",
              "position": 4,
              "name": data.name,
              "item": productUrl
            }
          ]
        },
        {
          "@type": "Product",
          "@id": `${productUrl}#product`,
          "name": data.name,
          "description": data.description,
          "image": data.image ? [data.image] : [this.defaultOgImage],
          "sku": data.sku || 'ELO-CATALOGO',
          "mpn": data.mpn || 'MPN-ELO',
          "gtin13": data.gtin13 || '7890000000000',
          "category": `Food, Beverages & Tobacco > Bakery > ${categoryName}`,
          "brand": {
            "@type": "Brand",
            "name": "Eloá"
          },
          "manufacturer": {
            "@id": `${this.baseUrl}/#organization`
          },
          "offers": {
            "@type": "Offer",
            "@id": `${productUrl}#offer`,
            "url": productUrl,
            "availability": "https://schema.org/InStock",
            "itemCondition": "https://schema.org/NewCondition",
            "businessFunction": "http://purl.org/goodrelations/v1#Sell",
            "priceSpecification": {
              "@type": "PriceSpecification",
              "priceCurrency": "BRL",
              "description": "Produto disponível sob consulta. Pedidos e cotações realizados diretamente pelo WhatsApp."
            },
            "seller": {
              "@id": `${this.baseUrl}/#organization`
            }
          }
        }
      ]
    };

    this.setJsonLd('json-ld-product', productSchema);
  }
}
