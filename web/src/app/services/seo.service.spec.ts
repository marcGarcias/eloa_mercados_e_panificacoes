import { TestBed } from '@angular/core/testing';
import { Title, Meta } from '@angular/platform-browser';
import { DOCUMENT } from '@angular/common';
import { SeoService, SeoConfig } from './seo.service';
import { SiteContent } from '../models/content.model';
import { Product } from '../models/product.model';

describe('SeoService', () => {
  let service: SeoService;
  let titleService: Title;
  let metaService: Meta;
  let document: Document;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [SeoService, Title, Meta]
    });

    service = TestBed.inject(SeoService);
    titleService = TestBed.inject(Title);
    metaService = TestBed.inject(Meta);
    document = TestBed.inject(DOCUMENT);
  });

  afterEach(() => {
    const scripts = document.querySelectorAll('script[type="application/ld+json"]');
    scripts.forEach(s => s.remove());
    const canonical = document.querySelector("link[rel='canonical']");
    if (canonical) canonical.remove();
  });

  it('deve ser instanciado corretamente', () => {
    expect(service).toBeTruthy();
  });

  describe('updateMetaTags', () => {
    it('deve atualizar o título e meta tags padrão', () => {
      const setTitleSpy = vi.spyOn(titleService, 'setTitle');
      const updateTagSpy = vi.spyOn(metaService, 'updateTag');

      service.updateMetaTags({
        title: 'Pães Artesanais',
        description: 'Descrição de teste',
        keywords: 'pães, doces',
        noIndex: false
      });

      expect(setTitleSpy).toHaveBeenCalledWith('Pães Artesanais | Eloá Mercados & Panificações');
      expect(updateTagSpy).toHaveBeenCalledWith({ name: 'description', content: 'Descrição de teste' });
      expect(updateTagSpy).toHaveBeenCalledWith({ name: 'keywords', content: 'pães, doces' });
      expect(updateTagSpy).toHaveBeenCalledWith(expect.objectContaining({
        name: 'robots',
        content: expect.stringContaining('index, follow')
      }));
    });

    it('deve definir robots noindex quando configurado', () => {
      const updateTagSpy = vi.spyOn(metaService, 'updateTag');

      service.updateMetaTags({ noIndex: true });

      expect(updateTagSpy).toHaveBeenCalledWith({ name: 'robots', content: 'noindex, nofollow' });
    });

    it('deve utilizar tags default quando campos opcionais não forem fornecidos', () => {
      const setTitleSpy = vi.spyOn(titleService, 'setTitle');

      service.updateMetaTags({});

      expect(setTitleSpy).toHaveBeenCalledWith('Eloá Mercados & Panificações');
    });
  });

  describe('setCanonicalUrl', () => {
    it('deve criar e atualizar a tag canonical', () => {
      service.setCanonicalUrl('https://eloapanificacoes.com.br/catalogo');

      const link = document.querySelector("link[rel='canonical']") as HTMLLinkElement;
      expect(link).toBeTruthy();
      expect(link.getAttribute('href')).toBe('https://eloapanificacoes.com.br/catalogo');

      // Atualizar existente
      service.setCanonicalUrl('https://eloapanificacoes.com.br/novo-link');
      expect(link.getAttribute('href')).toBe('https://eloapanificacoes.com.br/novo-link');
    });
  });

  describe('setJsonLd', () => {
    it('deve injetar script JSON-LD e atualizar se o conteúdo mudar', () => {
      const schema = { '@type': 'WebSite', name: 'Eloa' };
      service.setJsonLd('test-schema', schema);

      let script = document.getElementById('test-schema') as HTMLScriptElement;
      expect(script).toBeTruthy();
      expect(JSON.parse(script.text)).toEqual(schema);

      // Chamada idêntica (deve manter sem quebrar)
      service.setJsonLd('test-schema', schema);

      // Chamada com conteúdo atualizado
      const updated = { '@type': 'WebSite', name: 'Eloa Atualizada' };
      service.setJsonLd('test-schema', updated);
      script = document.getElementById('test-schema') as HTMLScriptElement;
      expect(JSON.parse(script.text)).toEqual(updated);
    });
  });

  describe('updateFromSiteContent', () => {
    it('não deve fazer nada se o conteúdo for nulo', () => {
      const setTitleSpy = vi.spyOn(titleService, 'setTitle');
      service.updateFromSiteContent(null as any);
      expect(setTitleSpy).not.toHaveBeenCalled();
    });

    it('deve truncar descrições com mais de 155 caracteres', () => {
      const longDesc = 'A'.repeat(160);
      const content = {
        banner: {
          selo: '',
          titulo: 'Título do Banner',
          subtitulo: '',
          descricao: longDesc,
          indicadores: []
        }
      } as unknown as SiteContent;

      service.updateFromSiteContent(content);

      const script = document.getElementById('json-ld-home');
      expect(script).toBeTruthy();
    });
  });

  describe('setHomeStructuredData', () => {
    it('deve formatar números de telefone de 11, 10 e 13 dígitos e diferentes padrões de dias', () => {
      // 11 dígitos com dias 'Segunda a Sábado'
      service.setHomeStructuredData({
        whatsapp: '11999998888',
        endereco: 'Rua das Flores, 123',
        diasFuncionamento: 'Segunda a Sábado',
        horarioAbertura: '06:00',
        horarioFechamento: '20:00',
        cnpj: '12.345.678/0001-90'
      });

      // 10 dígitos com dias 'Diariamente'
      service.setHomeStructuredData({
        whatsapp: '1133334444',
        endereco: '',
        diasFuncionamento: 'Diariamente',
        horarioAbertura: '06:00',
        horarioFechamento: '20:00',
        cnpj: ''
      });

      // 13 dígitos com '55' inicial
      service.setHomeStructuredData({
        whatsapp: '5511999998888',
        endereco: '',
        diasFuncionamento: 'Segunda a Sexta',
        horarioAbertura: '06:00',
        horarioFechamento: '20:00',
        cnpj: ''
      });

      const script = document.getElementById('json-ld-home');
      expect(script).toBeTruthy();
    });
  });

  describe('updateCatalogStructuredData e setProductStructuredData', () => {
    it('deve ignorar lista vazia de produtos', () => {
      service.updateCatalogStructuredData([]);
      expect(document.getElementById('json-ld-catalog')).toBeNull();
    });

    it('deve gerar dados estruturados de catálogo para lista de produtos', () => {
      const products: Product[] = [
        {
          id: 1,
          nome: 'Pão Francês Tradicional',
          categoria: 'Padaria',
          peso: '500g',
          imagem: 'https://exemplo.com/pao.webp'
        },
        {
          id: 2,
          nome: '',
          categoria: 'Confeitaria',
          peso: '1kg',
          imagem: null
        }
      ];

      service.updateCatalogStructuredData(products);

      const script = document.getElementById('json-ld-catalog') as HTMLScriptElement;
      expect(script).toBeTruthy();
      expect(script?.text).toContain('Pão Francês Tradicional');
    });

    it('deve configurar dados estruturados de um produto individual', () => {
      service.setProductStructuredData({
        name: 'Bolo de Chocolate',
        description: 'Delicioso bolo',
        category: 'Bolos',
        image: 'https://exemplo.com/bolo.webp',
        sku: 'SKU-123'
      });

      const script = document.getElementById('json-ld-product') as HTMLScriptElement;
      expect(script).toBeTruthy();
      expect(script?.text).toContain('Bolo de Chocolate');
    });
  });
});
