import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { CategoryAdminService } from './category-admin.service';
import { createMockCategory } from '../../testing';

describe('CategoryAdminService', () => {
  let service: CategoryAdminService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CategoryAdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(CategoryAdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getAll() e search()', () => {
    it('deve realizar GET em /api/admin/categories', () => {
      service.getAll().subscribe(categories => {
        expect(categories.length).toBe(1);
        expect(categories[0].name).toBe('Padaria');
      });

      const req = httpMock.expectOne('/api/admin/categories');
      expect(req.request.method).toBe('GET');
      req.flush([createMockCategory({ name: 'Padaria' })]);
    });

    it('deve realizar GET com parâmetros de paginação e busca por nome', () => {
      service.search(0, 5, 'Confeitaria').subscribe();

      const req = httpMock.expectOne(request => 
        request.url === '/api/admin/categories' &&
        request.params.get('page') === '0' &&
        request.params.get('size') === '5' &&
        request.params.get('name') === 'Confeitaria'
      );
      expect(req.request.method).toBe('GET');
      req.flush({ content: [] });
    });
  });

  describe('create() e update()', () => {
    it('deve realizar POST com o payload da categoria e retornar o objeto criado', () => {
      service.create('Açougue e Frios').subscribe(category => {
        expect(category.id).toBe(12);
        expect(category.name).toBe('Açougue e Frios');
      });

      const req = httpMock.expectOne('/api/admin/categories');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ name: 'Açougue e Frios' });

      req.flush(createMockCategory({ id: 12, name: 'Açougue e Frios' }));
    });

    it('deve realizar PUT em /api/admin/categories/{id} com o nome atualizado', () => {
      service.update(12, 'Carnes Nobres').subscribe(category => {
        expect(category.name).toBe('Carnes Nobres');
      });

      const req = httpMock.expectOne('/api/admin/categories/12');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({ name: 'Carnes Nobres' });

      req.flush(createMockCategory({ id: 12, name: 'Carnes Nobres' }));
    });

    it('deve propagar erro 400 em caso de nome inválido ou em branco', () => {
      let caughtError: any = null;

      service.create('').subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/categories');
      req.flush({ message: 'Nome não pode ser vazio' }, { status: 400, statusText: 'Bad Request' });

      expect(caughtError.status).toBe(400);
    });
  });

  describe('delete() e deleteCategories()', () => {
    it('deve realizar DELETE individual em /api/admin/categories/{id}', () => {
      service.delete(12).subscribe();

      const req = httpMock.expectOne('/api/admin/categories/12');
      expect(req.request.method).toBe('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    });

    it('deve realizar POST em /api/admin/categories/batch-delete para exclusão em lote', () => {
      service.deleteCategories([10, 11]).subscribe();

      const req = httpMock.expectOne('/api/admin/categories/batch-delete');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ ids: [10, 11] });
      req.flush(null, { status: 204, statusText: 'No Content' });
    });

    it('deve propagar erro 409 caso a categoria contenha produtos vinculados', () => {
      let caughtError: any = null;

      service.delete(10).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/categories/10');
      req.flush(
        { message: 'Não é possível excluir categoria com produtos associados' },
        { status: 409, statusText: 'Conflict' }
      );

      expect(caughtError.status).toBe(409);
    });
  });
});
