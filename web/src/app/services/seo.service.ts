import { Injectable, inject, RendererFactory2, Renderer2 } from '@angular/core';
import { Title, Meta } from '@angular/platform-browser';
import { DOCUMENT } from '@angular/common';
import { SiteContent, SiteData, ContentFaq } from '../models/content.model';
import { Product } from '../models/product.model';

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
  geoRegion?: string;
  geoPlacename?: string;
  geoPosition?: string;
  icbm?: string;
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
  private readonly defaultOgImage = 'https://eloapanificacoes.com.br/assets/images/og-eloa-banner.webp';

  constructor() {
    this.renderer = this.rendererFactory.createRenderer(null, null);
  }

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

    // Geotargeting & Local SEO
    this.metaService.updateTag({ name: 'geo.region', content: config.geoRegion || 'BR-SP' });
    this.metaService.updateTag({ name: 'geo.placename', content: config.geoPlacename || 'São Paulo, Brasil' });
    this.metaService.updateTag({ name: 'geo.position', content: config.geoPosition || '-23.550520;-46.633308' });
    this.metaService.updateTag({ name: 'ICBM', content: config.icbm || '-23.550520, -46.633308' });
  }

  /**
   * Atualiza dinamicamente metadados e Schema.org com base nos dados do banco de dados (SiteContent)
   */
  updateFromSiteContent(content: SiteContent): void {
    if (!content) return;

    const bannerDesc = content.banner?.descricao || content.sobre?.descricao || '';
    const cleanDesc = bannerDesc.length > 155 ? bannerDesc.substring(0, 152) + '...' : bannerDesc;

    // Atualiza metatags com os textos frescos do BD
    this.updateMetaTags({
      title: content.banner?.titulo ? `${content.banner.titulo} - Catálogo & Pedidos WhatsApp` : 'Catálogo de Panificação & Pedidos via WhatsApp',
      description: cleanDesc || 'Catálogo de produtos da Eloá Mercados & Panificações. Pães, doces, bolos, salgados e insumos de panificação. Consulte e faça seu pedido pelo WhatsApp.',
      canonicalUrl: this.baseUrl,
      ogTitle: `${this.siteName} | ${content.banner?.titulo || 'Catálogo de Produtos & Pedidos WhatsApp'}`,
      ogDescription: cleanDesc || 'Consulte nossa linha completa de panificação e confeitaria. Faça seu pedido diretamente pelo WhatsApp com nossa equipe.',
      ogImage: this.defaultOgImage
    });

    // Atualiza dados estruturados com dados cadastrais e de atendimento do BD
    this.setHomeStructuredData(content.dados, content.faq);
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
    const jsonString = JSON.stringify(schemaData, null, 2);
    const existingScript = this.document.getElementById(schemaId) as HTMLScriptElement | null;
    if (existingScript) {
      if (existingScript.text === jsonString) {
        return;
      }
      this.renderer.setProperty(existingScript, 'text', jsonString);
      return;
    }

    const script = this.renderer.createElement('script');
    this.renderer.setAttribute(script, 'type', 'application/ld+json');
    this.renderer.setAttribute(script, 'id', schemaId);
    this.renderer.setProperty(script, 'text', jsonString);
    this.renderer.appendChild(this.document.head, script);
  }

  /**
   * Configura os dados estruturados da Home (Organization + LocalBusiness / Atendimento via WhatsApp)
   * Recebe opcionalmente os dados dinâmicos do Banco de Dados para personalização em tempo real.
   */
  setHomeStructuredData(siteData?: SiteData, faq?: ContentFaq): void {
    const rawPhone = siteData?.whatsapp || '+55-11-99999-9999';
    const formattedPhone = this.formatPhoneNumber(rawPhone);
    const addressObj = this.parseAddress(siteData?.endereco);
    const openingHours = this.parseOpeningHours(
      siteData?.diasFuncionamento,
      siteData?.horarioAbertura,
      siteData?.horarioFechamento
    );

    const faqItens = faq?.itens && faq.itens.length > 0
      ? faq.itens
      : [
          {
            pergunta: 'Como faço para pedir ou cotar produtos da Eloá Panificações?',
            resposta: 'Você pode consultar todo o catálogo online e clicar no botão de pedido para ser atendido diretamente pela nossa equipe comercial via WhatsApp, com agilidade e cotações personalizadas.'
          },
          {
            pergunta: 'Quais tipos de produtos a Eloá comercializa?',
            resposta: 'Trabalhamos com linha completa de panificação tradicional e especial, pães artesanais, bolos caseiros e recheados, doces finos, salgados para eventos e atacado, além de insumos selecionados.'
          },
          {
            pergunta: 'A Eloá atende compras no atacado para padarias, mercados e restaurantes?',
            resposta: 'Sim. Atendemos tanto clientes diretos quanto compras em grande volume no atacado para estabelecimentos comerciais, com fornecimento programado e condições comerciais especiais.'
          },
          {
            pergunta: 'Quais regiões são atendidas para entregas e fornecimento?',
            resposta: 'Atendemos toda a Grande São Paulo, Região Metropolitana de Campinas, Vale do Paraíba e Litoral de São Paulo através de nossa logística especializada de distribuição.'
          },
          {
            pergunta: 'Quais são as formas de pagamento aceitas?',
            resposta: 'Aceitamos Pix, cartões de crédito e débito, dinheiro e condições faturadas para clientes corporativos e comerciais cadastrados.'
          }
        ];

    const faqMainEntities = faqItens.map(item => ({
      "@type": "Question",
      "name": item.pergunta,
      "acceptedAnswer": {
        "@type": "Answer",
        "text": item.resposta
      }
    }));

    const homeSchema = {
      "@context": "https://schema.org",
      "@graph": [
        {
          "@type": "WebSite",
          "@id": `${this.baseUrl}/#website`,
          "url": this.baseUrl,
          "name": "Eloá Mercados & Panificações",
          "description": "Catálogo de produtos da Eloá Mercados & Panificações. Pães frescos, confeitaria e atacado para pedidos via WhatsApp.",
          "publisher": {
            "@id": `${this.baseUrl}/#organization`
          },
          "inLanguage": "pt-BR"
        },
        {
          "@type": "Organization",
          "@id": `${this.baseUrl}/#organization`,
          "name": "Eloá Mercados & Panificações",
          "legalName": "Eloá Mercados e Panificações Ltda.",
          "taxID": siteData?.cnpj || undefined,
          "url": this.baseUrl,
          "logo": {
            "@type": "ImageObject",
            "@id": `${this.baseUrl}/#logo`,
            "url": `${this.baseUrl}/assets/images/og-eloa-banner.webp`,
            "contentUrl": `${this.baseUrl}/assets/images/og-eloa-banner.webp`,
            "caption": "Eloá Panificações e Alimentos para Atacado",
            "width": "1200",
            "height": "630"
          },
          "image": {
            "@id": `${this.baseUrl}/#logo`
          },
          "sameAs": [
            "https://www.instagram.com/eloapanificacoes",
            "https://www.facebook.com/eloapanificacoes"
          ],
          "contactPoint": [
            {
              "@type": "ContactPoint",
              "telephone": formattedPhone,
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
          "image": `${this.baseUrl}/assets/images/eloa_building.webp`,
          "url": this.baseUrl,
          "telephone": formattedPhone,
          "priceRange": "$$",
          "currenciesAccepted": "BRL",
          "paymentAccepted": "Dinheiro, Cartão de Crédito, Cartão de Débito, Pix",
          "servesCuisine": "Panificação, Confeitaria e Alimentos para Revenda",
          "address": addressObj,
          "geo": {
            "@type": "GeoCoordinates",
            "latitude": -23.550520,
            "longitude": -46.633308
          },
          "openingHoursSpecification": openingHours,
          "areaServed": [
            { "@type": "AdministrativeArea", "name": "São Paulo" },
            { "@type": "AdministrativeArea", "name": "Grande São Paulo" },
            { "@type": "AdministrativeArea", "name": "Região Metropolitana de Campinas" },
            { "@type": "AdministrativeArea", "name": "Vale do Paraíba" },
            { "@type": "AdministrativeArea", "name": "Litoral de São Paulo" }
          ],
          "parentOrganization": {
            "@id": `${this.baseUrl}/#organization`
          }
        },
        {
          "@type": "FAQPage",
          "@id": `${this.baseUrl}/#faq`,
          "mainEntity": faqMainEntities
        }
      ]
    };

    this.setJsonLd('json-ld-home', homeSchema);
  }

  /**
   * Atualiza os dados estruturados do Catálogo de Produtos e Categorias dinamicamente a partir do BD
   */
  updateCatalogStructuredData(products: Product[], categories?: string[]): void {
    if (!products || products.length === 0) return;

    const itemListElements = products.slice(0, 30).map((prod, index) => {
      const slug = prod.nome
        ? prod.nome.toLowerCase().trim().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '')
        : `item-${index + 1}`;
      const itemUrl = `${this.baseUrl}/#catalog-${slug || (index + 1)}`;

      return {
        "@type": "ListItem",
        "position": index + 1,
        "name": prod.nome,
        "description": `${prod.nome} - Linha de panificação e confeitaria Eloá. Disponível sob consulta para pedidos e cotações no atacado.`,
        "url": itemUrl,
        "image": prod.imagem || this.defaultOgImage
      };
    });

    const catalogSchema = {
      "@context": "https://schema.org",
      "@graph": [
        {
          "@type": "ItemList",
          "@id": `${this.baseUrl}/#catalog-list`,
          "name": "Catálogo de Produtos Eloá Panificações",
          "description": "Vitrine de produtos de panificação, confeitaria e atacado para pedidos via WhatsApp.",
          "numberOfItems": products.length,
          "itemListElement": itemListElements
        }
      ]
    };

    this.setJsonLd('json-ld-catalog', catalogSchema);
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
          }
        }
      ]
    };

    this.setJsonLd('json-ld-product', productSchema);
  }

  // --- MÉTODOS DE APOIO / NORMALIZAÇÃO DE DADOS ---

  /**
   * Normaliza números de telefone para o padrão internacional E.164 (+55-11-99999-9999)
   */
  private formatPhoneNumber(raw?: string): string {
    if (!raw) return '+55-11-99999-9999';
    const digits = raw.replace(/\D/g, '');
    if (digits.length === 11) {
      // Ex: 11999998888 -> +55-11-99999-8888
      return `+55-${digits.substring(0, 2)}-${digits.substring(2, 7)}-${digits.substring(7)}`;
    } else if (digits.length === 10) {
      // Ex: 1133334444 -> +55-11-3333-4444
      return `+55-${digits.substring(0, 2)}-${digits.substring(2, 6)}-${digits.substring(6)}`;
    } else if (digits.length === 13 && digits.startsWith('55')) {
      // Ex: 5511999998888 -> +55-11-99999-8888
      return `+55-${digits.substring(2, 4)}-${digits.substring(4, 9)}-${digits.substring(9)}`;
    }
    return raw;
  }

  /**
   * Faz o parse da string de endereço do BD para o objeto Schema.org PostalAddress
   */
  private parseAddress(rawAddress?: string): object {
    if (!rawAddress) {
      return {
        "@type": "PostalAddress",
        "streetAddress": "Av. Principal das Indústrias, 1000",
        "addressLocality": "São Paulo",
        "addressRegion": "SP",
        "postalCode": "01000-000",
        "addressCountry": "BR"
      };
    }

    return {
      "@type": "PostalAddress",
      "streetAddress": rawAddress,
      "addressLocality": "São Paulo",
      "addressRegion": "SP",
      "postalCode": "01000-000",
      "addressCountry": "BR"
    };
  }

  /**
   * Mapeia os horários salvos no BD para o formato OpeningHoursSpecification do Schema.org
   */
  private parseOpeningHours(dias?: string, abre?: string, fecha?: string): object[] {
    const opens = abre || '07:00';
    const closes = fecha || '18:00';
    const diasStr = (dias || 'Segunda a Sexta').toLowerCase();

    const specs: object[] = [];

    if ((diasStr.includes('segunda') && diasStr.includes('sábado')) || diasStr.includes('sabado')) {
      specs.push({
        "@type": "OpeningHoursSpecification",
        "dayOfWeek": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"],
        "opens": opens,
        "closes": closes
      });
    } else if (diasStr.includes('todos') || diasStr.includes('diari') || diasStr.includes('domingo')) {
      specs.push({
        "@type": "OpeningHoursSpecification",
        "dayOfWeek": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
        "opens": opens,
        "closes": closes
      });
    } else {
      // Padrão: Segunda a Sexta + Sábado reduzido
      specs.push({
        "@type": "OpeningHoursSpecification",
        "dayOfWeek": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"],
        "opens": opens,
        "closes": closes
      });
      specs.push({
        "@type": "OpeningHoursSpecification",
        "dayOfWeek": ["Saturday"],
        "opens": opens,
        "closes": "13:00"
      });
    }

    return specs;
  }
}

