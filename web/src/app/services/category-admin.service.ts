import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategoryAdminResponse, SpringPage } from '../models/product.model';

/**
 * Service para operacoes de categoria no contexto Admin.
 * Consome:
 * GET /api/admin/categories
 * POST /api/admin/categories
 * PUT /api/admin/categories/{id}
 * DELETE /api/admin/categories/{id}
 * POST /api/admin/categories/batch-delete
 */
@Injectable({ providedIn: 'root' })
export class CategoryAdminService {
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/admin/categories';

  constructor(private readonly http: HttpClient) {}

  /**
   * Lista todas as categorias.
   * Endpoint: GET /api/admin/categories
   */
  getAll(): Observable<CategoryAdminResponse[]> {
    return this.http.get<CategoryAdminResponse[]>(this.apiUrl);
  }

  /**
   * Busca categorias de forma paginada com filtro opcional por nome.
   * Endpoint: GET /api/admin/categories?page=0&size=10&name=
   */
  search(page: number = 0, size: number = 10, name?: string): Observable<SpringPage<CategoryAdminResponse>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
    if (name && name.trim()) {
      params = params.set('name', name.trim());
    }
    return this.http.get<SpringPage<CategoryAdminResponse>>(this.apiUrl, { params });
  }

  /**
   * Cria uma nova categoria.
   * Endpoint: POST /api/admin/categories
   */
  create(name: string): Observable<CategoryAdminResponse> {
    return this.http.post<CategoryAdminResponse>(this.apiUrl, { name });
  }

  /**
   * Atualiza uma categoria.
   * Endpoint: PUT /api/admin/categories/{id}
   */
  update(id: number, name: string): Observable<CategoryAdminResponse> {
    return this.http.put<CategoryAdminResponse>(`${this.apiUrl}/${id}`, { name });
  }

  /**
   * Remove uma categoria individualmente.
   * Endpoint: DELETE /api/admin/categories/{id}
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Remove múltiplas categorias em lote.
   * Endpoint: POST /api/admin/categories/batch-delete
   */
  deleteCategories(ids: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/batch-delete`, { ids });
  }
}
