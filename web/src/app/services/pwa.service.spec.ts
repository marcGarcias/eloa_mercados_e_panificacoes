import { TestBed } from '@angular/core/testing';
import { PwaService } from './pwa.service';
import { Router, NavigationEnd } from '@angular/router';
import { Subject } from 'rxjs';
import { DOCUMENT } from '@angular/common';

describe('PwaService', () => {
  let service: PwaService;
  let routerEvents$: Subject<any>;
  let mockRouter: any;
  let doc: Document;

  beforeEach(() => {
    routerEvents$ = new Subject<any>();
    mockRouter = {
      url: '/',
      events: routerEvents$.asObservable()
    };

    TestBed.configureTestingModule({
      providers: [
        PwaService,
        { provide: Router, useValue: mockRouter }
      ]
    });

    doc = TestBed.inject(DOCUMENT);
    // Cleanup any existing link
    const existing = doc.getElementById('pwa-manifest');
    if (existing) existing.remove();

    service = TestBed.inject(PwaService);
  });

  afterEach(() => {
    service.ngOnDestroy();
    const existing = doc.getElementById('pwa-manifest');
    if (existing) existing.remove();
    TestBed.resetTestingModule();
  });

  it('não deve injetar manifest na rota raiz pública', () => {
    const manifestLink = doc.getElementById('pwa-manifest');
    expect(manifestLink).toBeNull();
  });

  it('deve injetar manifest ao navegar para /admin/catalog', () => {
    routerEvents$.next(new NavigationEnd(1, '/admin/catalog', '/admin/catalog'));
    const manifestLink = doc.getElementById('pwa-manifest') as HTMLLinkElement;
    expect(manifestLink).not.toBeNull();
    expect(manifestLink.getAttribute('href')).toBe('/manifest.webmanifest');
  });

  it('deve injetar manifest ao navegar para /login-cms', () => {
    routerEvents$.next(new NavigationEnd(1, '/login-cms', '/login-cms'));
    const manifestLink = doc.getElementById('pwa-manifest');
    expect(manifestLink).not.toBeNull();
  });

  it('deve remover manifest ao sair do admin para a página pública', () => {
    routerEvents$.next(new NavigationEnd(1, '/admin', '/admin'));
    expect(doc.getElementById('pwa-manifest')).not.toBeNull();

    routerEvents$.next(new NavigationEnd(2, '/', '/'));
    expect(doc.getElementById('pwa-manifest')).toBeNull();
  });

  it('deve retornar "unavailable" se promptInstall for chamado sem deferredPrompt', async () => {
    const result = await service.promptInstall();
    expect(result).toBe('unavailable');
  });
});
