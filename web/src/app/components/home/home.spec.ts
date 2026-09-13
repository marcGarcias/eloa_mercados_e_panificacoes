import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Home } from './home';
import { ContentService } from '../../services/content.service';
import { SeoService } from '../../services/seo.service';
import { of, throwError } from 'rxjs';
import { SiteContent } from '../../models/content.model';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { DEFAULT_SITE_CONTENT } from '../../core/constants/content-fallbacks';

describe('Home Component', () => {
  let component: Home;
  let fixture: ComponentFixture<Home>;
  let mockContentService: { getContentPublic: ReturnType<typeof vi.fn> };
  let mockSeoService: { updateMetaTags: ReturnType<typeof vi.fn>; updateFromSiteContent: ReturnType<typeof vi.fn> };

  const fakeSiteContent: SiteContent = {
    ...DEFAULT_SITE_CONTENT,
    banner: { ...DEFAULT_SITE_CONTENT.banner, titulo: 'Banner Customizado' }
  };

  beforeEach(async () => {
    mockContentService = {
      getContentPublic: vi.fn().mockReturnValue(of(fakeSiteContent))
    };

    mockSeoService = {
      updateMetaTags: vi.fn(),
      updateFromSiteContent: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: ContentService, useValue: mockContentService },
        { provide: SeoService, useValue: mockSeoService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(Home);
    component = fixture.componentInstance;
  });

  it('deve ser instanciado com o conteudo padrao inicial', () => {
    expect(component).toBeTruthy();
    expect(component.content).toEqual(DEFAULT_SITE_CONTENT);
  });

  it('deve configurar meta tags de SEO e carregar conteudo com sucesso no ngOnInit', () => {
    fixture.detectChanges();

    expect(mockSeoService.updateMetaTags).toHaveBeenCalledWith(
      expect.objectContaining({
        title: expect.stringContaining('Catálogo de Panificação'),
        canonicalUrl: 'https://eloapanificacoes.com.br/'
      })
    );
    expect(mockContentService.getContentPublic).toHaveBeenCalled();
    expect(component.content.banner.titulo).toBe('Banner Customizado');
    expect(mockSeoService.updateFromSiteContent).toHaveBeenCalledWith(component.content);
  });

  it('deve usar o conteudo fallback padrao caso a requisicao falhe', () => {
    mockContentService.getContentPublic.mockReturnValue(throwError(() => new Error('Falha de rede')));

    fixture.detectChanges();

    expect(component.content).toBeDefined();
    expect(component.content.banner.titulo).toBe(DEFAULT_SITE_CONTENT.banner.titulo);
  });

  it('deve cancelar inscricoes ao destruir o componente (ngOnDestroy)', () => {
    fixture.detectChanges();
    const unsubscribeSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');

    component.ngOnDestroy();

    expect(unsubscribeSpy).toHaveBeenCalled();
  });
});
