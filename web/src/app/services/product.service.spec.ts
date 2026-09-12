import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { ProductService } from './product.service';
import { ProductStatus, CreateProductPayload, UpdateProductPayload } from '../models/product.model';
import { createMockProductAdmin } from '../../testing';

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        ProductService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(ProductService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('searchAdmin() - Listagem com Filtros', () => {
    it('deve realizar GET com parâmetros de busca completos', () => {
      // Act
      service.searchAdmin({
        name: 'Pão',
        categoryId: 3,
        status: ProductStatus.ACTIVE,
        page: 1,
        size: 20
      }).subscribe(res => {
        expect(res.content.length).toBe(1);
      });

      // Assert
      const req = httpMock.expectOne(request => 
        request.url === '/api/admin/products' &&
        request.params.get('name') === 'Pão' &&
        request.params.get('categoryId') === '3' &&
        request.params.get('status') === 'ACTIVE' &&
        request.params.get('page') === '1' &&
        request.params.get('size') === '20'
      );
      expect(req.request.method).toBe('GET');

      req.flush({
        content: [createMockProductAdmin()],
        totalElements: 1,
        totalPages: 1,
        size: 20,
        number: 1,
        first: false,
        last: true,
        empty: false
      });
    });
  });

  describe('create() - Criação com Multipart/FormData', () => {
    it('deve realizar POST com FormData e retornar o produto criado', () => {
      // Arrange
      const fakeFile = new File(['fake-image'], 'foto.webp', { type: 'image/webp' });
      const payload: CreateProductPayload = {
        name: 'Baguete Tradicional',
        weight: 250,
        photo: fakeFile,
        categoryId: 2
      };

      // Act
      service.create(payload).subscribe(product => {
        expect(product.id).toBe(10);
        expect(product.name).toBe('Baguete Tradicional');
      });

      // Assert
      const req = httpMock.expectOne('/api/admin/products');
      expect(req.request.method).toBe('POST');
      expect(req.request.body instanceof FormData).toBe(true);

      const fd = req.request.body as FormData;
      expect(fd.get('name')).toBe('Baguete Tradicional');
      expect(fd.get('weight')).toBe('250');
      expect(fd.get('categoryId')).toBe('2');

      req.flush(createMockProductAdmin({ id: 10, name: 'Baguete Tradicional' }));
    });

    it('deve propagar erro 400 em caso de validação falha no backend', () => {
      const fakeFile = new File(['fake-image'], 'foto.png', { type: 'image/png' });
      let caughtError: any = null;

      service.create({
        name: '',
        weight: -10,
        photo: fakeFile,
        categoryId: 0
      }).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/products');
      req.flush({ message: 'Campos inválidos' }, { status: 400, statusText: 'Bad Request' });

      expect(caughtError.status).toBe(400);
    });
  });

  describe('update() - Atualização Parcial com PATCH', () => {
    it('deve realizar PATCH em /api/admin/products/{id} com FormData dos campos alterados', () => {
      const payload: UpdateProductPayload = {
        name: 'Pão de Queijo Mineiro',
        status: ProductStatus.INACTIVE
      };

      service.update(5, payload).subscribe(res => {
        expect(res.status).toBe(ProductStatus.INACTIVE);
      });

      const req = httpMock.expectOne('/api/admin/products/5');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body instanceof FormData).toBe(true);

      const fd = req.request.body as FormData;
      expect(fd.get('name')).toBe('Pão de Queijo Mineiro');
      expect(fd.get('status')).toBe('INACTIVE');
      expect(fd.has('weight')).toBe(false);

      req.flush(createMockProductAdmin({ id: 5, status: ProductStatus.INACTIVE }));
    });

    it('deve propagar erro 404 caso o produto não exista', () => {
      let caughtError: any = null;

      service.update(999, { name: 'Inexistente' }).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/products/999');
      req.flush('Produto não encontrado', { status: 404, statusText: 'Not Found' });

      expect(caughtError.status).toBe(404);
    });
  });

  describe('delete() e deleteProducts()', () => {
    it('deve realizar DELETE individual em /api/admin/products/{id}', () => {
      service.delete(7).subscribe();

      const req = httpMock.expectOne('/api/admin/products/7');
      expect(req.request.method).toBe('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    });

    it('deve realizar POST em /api/admin/products/batch-delete para exclusão em lote', () => {
      service.deleteProducts([1, 2, 3]).subscribe();

      const req = httpMock.expectOne('/api/admin/products/batch-delete');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ ids: [1, 2, 3] });
      req.flush(null, { status: 204, statusText: 'No Content' });
    });

    it('deve propagar erro 409 em caso de conflito de integridade referencial', () => {
      let caughtError: any = null;

      service.delete(1).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/products/1');
      req.flush({ message: 'Produto vinculado a pedidos' }, { status: 409, statusText: 'Conflict' });

      expect(caughtError.status).toBe(409);
    });
  });

  describe('updateOrder() - Reordenação', () => {
    it('deve realizar PUT em /api/admin/products/reorder com a lista ordenada', () => {
      service.updateOrder([3, 1, 2]).subscribe();

      const req = httpMock.expectOne('/api/admin/products/reorder');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({ order: [3, 1, 2] });
      req.flush(null);
    });
  });

  describe('searchPublic() - Catálogo Público', () => {
    it('deve realizar GET em /api/public/products com parâmetros padrão', () => {
      service.searchPublic({ page: 0, size: 12 }).subscribe();

      const req = httpMock.expectOne(request => 
        request.url === '/api/public/products' &&
        request.params.get('page') === '0' &&
        request.params.get('size') === '12'
      );
      expect(req.request.method).toBe('GET');
      req.flush({ content: [] });
    });
  });
});
