import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { UserService } from './user.service';
import { CreateUserPayload, UpdateUserPayload } from '../models/user.model';
import { createMockUser } from '../../testing';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  describe('getAll()', () => {
    it('deve realizar GET em /api/admin/users com parâmetros de paginação', () => {
      service.getAll(0, 10).subscribe(res => {
        expect(res.content.length).toBe(1);
      });

      const req = httpMock.expectOne(request =>
        request.url === '/api/admin/users' &&
        request.params.get('page') === '0' &&
        request.params.get('size') === '10'
      );
      expect(req.request.method).toBe('GET');

      req.flush({
        content: [createMockUser()],
        totalElements: 1,
        totalPages: 1,
        size: 10,
        number: 0
      });
    });
  });

  describe('create()', () => {
    it('deve realizar POST com os dados do usuário a ser criado', () => {
      const payload: CreateUserPayload = {
        name: 'Operador Padaria',
        role: 'EDITOR',
        status: 'ACTIVE',
        password: 'SenhaForte123!'
      };

      service.create(payload).subscribe();

      const req = httpMock.expectOne('/api/admin/users');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(payload);
      req.flush(null, { status: 201, statusText: 'Created' });
    });

    it('deve propagar erro 409 em caso de usuário duplicado', () => {
      let caughtError: any = null;

      service.create({
        name: 'Duplicado',
        role: 'EDITOR',
        status: 'ACTIVE'
      }).subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/users');
      req.flush({ message: 'Usuário já cadastrado' }, { status: 409, statusText: 'Conflict' });

      expect(caughtError.status).toBe(409);
    });
  });

  describe('updateData() com PATCH', () => {
    it('deve realizar PATCH em /api/admin/users/{id} atualizando nome, role e status', () => {
      const payload: UpdateUserPayload = {
        name: 'Admin Alterado',
        role: 'ADMIN',
        status: 'INACTIVE'
      };

      service.updateData('usr-100', payload).subscribe();

      const req = httpMock.expectOne('/api/admin/users/usr-100');
      expect(req.request.method).toBe('PATCH');
      expect(req.request.body).toEqual(payload);
      req.flush(null, { status: 200, statusText: 'OK' });
    });
  });

  describe('changePassword() com PUT', () => {
    it('deve realizar PUT em /api/admin/users/{id}/password com a nova senha', () => {
      service.changePassword('usr-100', 'NovaSenha123!').subscribe();

      const req = httpMock.expectOne('/api/admin/users/usr-100/password');
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({ newPassword: 'NovaSenha123!' });
      req.flush(null, { status: 200, statusText: 'OK' });
    });
  });

  describe('delete()', () => {
    it('deve realizar DELETE em /api/admin/users/{id}', () => {
      service.delete('usr-100').subscribe();

      const req = httpMock.expectOne('/api/admin/users/usr-100');
      expect(req.request.method).toBe('DELETE');
      req.flush(null, { status: 204, statusText: 'No Content' });
    });

    it('deve propagar erro 403 se o usuário atual não tiver permissão para excluir', () => {
      let caughtError: any = null;

      service.delete('usr-admin').subscribe({
        error: (err) => (caughtError = err)
      });

      const req = httpMock.expectOne('/api/admin/users/usr-admin');
      req.flush({ message: 'Acesso negado' }, { status: 403, statusText: 'Forbidden' });

      expect(caughtError.status).toBe(403);
    });
  });
});

