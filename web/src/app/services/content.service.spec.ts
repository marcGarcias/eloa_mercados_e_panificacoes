import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { ContentService } from './content.service';
import { SiteContent } from '../models/content.model';
import { environment } from '../../environments/environment';

describe('ContentService', () => {
  let service: ContentService;
  let httpMock: HttpTestingController;
  const baseUrl = environment?.apiUrl ?? '';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ContentService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ContentService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser instanciado corretamente', () => {
    expect(service).toBeTruthy();
  });

  it('deve obter o conteúdo administrativo via GET /api/admin/content', () => {
    const mockContent = {
      banner: { selo: '', titulo: 'Eloa Panificações', subtitulo: '', descricao: '', indicadores: [] }
    } as unknown as SiteContent;

    service.getContent().subscribe(content => {
      expect(content).toEqual(mockContent);
    });

    const req = httpMock.expectOne(`${baseUrl}/api/admin/content`);
    expect(req.request.method).toBe('GET');
    req.flush(mockContent);
  });

  it('deve obter o conteúdo público via GET /api/public/content com timeout', () => {
    const mockContent = {
      banner: { selo: '', titulo: 'Eloa Público', subtitulo: '', descricao: '', indicadores: [] }
    } as unknown as SiteContent;

    service.getContentPublic().subscribe(content => {
      expect(content).toEqual(mockContent);
    });

    const req = httpMock.expectOne(`${baseUrl}/api/public/content`);
    expect(req.request.method).toBe('GET');
    req.flush(mockContent);
  });

  it('deve salvar o conteúdo via PATCH /api/admin/content', () => {
    const newContent = {
      banner: { selo: '', titulo: 'Novo Banner Atualizado', subtitulo: '', descricao: '', indicadores: [] }
    } as unknown as SiteContent;

    service.saveContent(newContent).subscribe(response => {
      expect(response).toEqual(newContent);
    });

    const req = httpMock.expectOne(`${baseUrl}/api/admin/content`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual(newContent);
    req.flush(newContent);
  });
});
