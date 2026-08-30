import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of, map } from 'rxjs';
import {
  Product,
  ProductAdminResponse,
  ProductPublicResponse,
  ProductStatus,
  CreateProductPayload,
  UpdateProductPayload,
  SpringPage,
} from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/admin/products';

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Product[]> {
    return this.searchPublic().pipe(
      map(page => page.content.map(p => ({
        nome: p.name,
        categoria: p.categoryName,
        peso: p.weight ? `${p.weight.toString().replace('.', ',')} kg` : '',
        imagem: this.getProductImageUrl(p.photoUrl),
        order: Number(p.position)
      })))
    );
  }

  getCategories(): string[] {
    return [];
  }

  searchPublic(filters: {
    name?: string;
    categoryName?: string;
    page?: number;
    size?: number;
  } = {}): Observable<SpringPage<ProductPublicResponse>> {
    const publicUrl = (environment?.apiUrl ?? '') + '/api/public/products';
    let params = new HttpParams()
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 999));
    if (filters.name)         params = params.set('name', filters.name);
    if (filters.categoryName) params = params.set('categoryName', filters.categoryName);
    return this.http.get<SpringPage<ProductPublicResponse>>(publicUrl, { params });
  }

  getPublicCategories(): Observable<string[]> {
    const categoriesUrl = (environment?.apiUrl ?? '') + '/api/public/categories';
    return this.http.get<{ name: string }[]>(categoriesUrl).pipe(
      map(list => list.map(c => c.name))
    );
  }

  // ----------------------------------------------------------------
  // Metodos Admin — alinhados com a API
  // ----------------------------------------------------------------

  /**
   * Lista produtos com filtros e paginacao.
   * GET /api/admin/products?name=&categoryId=&status=&page=&size=
   *
   * @param filters - Filtros opcionais de busca
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  searchAdmin(filters: {
    name?: string;
    categoryId?: number;
    status?: ProductStatus;
    page?: number;
    size?: number;
  } = {}): Observable<SpringPage<ProductAdminResponse>> {
    let params = new HttpParams()
      .set('page', String(filters.page ?? 0))
      .set('size', String(filters.size ?? 10));
    if (filters.name)       params = params.set('name', filters.name);
    if (filters.categoryId) params = params.set('categoryId', String(filters.categoryId));
    if (filters.status)     params = params.set('status', filters.status);
    return this.http.get<SpringPage<ProductAdminResponse>>(this.apiUrl, { params });
  }

  /**
   * Cria um novo produto.
   * POST /api/admin/products (multipart/form-data)
   *
   * @param payload - Dados do produto com foto WebP obrigatoria
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  create(payload: CreateProductPayload): Observable<ProductAdminResponse> {
    const formData = this.buildCreateFormData(payload);
    return this.http.post<ProductAdminResponse>(this.apiUrl, formData);
  }

  /**
   * Atualiza campos de um produto existente.
   * PATCH /api/admin/products/{id} (multipart/form-data)
   * Todos os campos sao opcionais.
   *
   * @param id - ID do produto
   * @param payload - Campos a atualizar (parcial)
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  update(id: number, payload: UpdateProductPayload): Observable<ProductAdminResponse> {
    const formData = this.buildUpdateFormData(payload);
    return this.http.patch<ProductAdminResponse>(`${this.apiUrl}/${id}`, formData);
  }

  /**
   * Remove um produto.
   * DELETE /api/admin/products/{id}
   *
   * @param id - ID do produto
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  /**
   * Remove múltiplos produtos em lote.
   * Exclui os produtos cujos IDs foram informados.
   */
  deleteProducts(ids: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/batch-delete`, { ids });
  }

  /**
   * Atualiza a ordem de exibição dos produtos em lote.
   */
  updateOrder(productIds: number[]): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/reorder`, { order: productIds });
  }

  // ----------------------------------------------------------------
  // Helpers Gerais
  // ----------------------------------------------------------------

  /**
   * Converte o caminho da foto (/uploads/products/filename) para a URL de visualizacao.
   */
  getProductImageUrl(photoPath: string | null | undefined): string | null {
    if (!photoPath) return null;
    const filename = photoPath.substring(photoPath.lastIndexOf('/') + 1);
    const apiBase = environment?.apiUrl ?? '';
    return `${apiBase}/api/storage/images/${filename}`;
  }

  // ----------------------------------------------------------------
  // Helpers de FormData
  // ----------------------------------------------------------------

  /**
   * Constroi FormData para criacao de produto (POST).
   * Todos os campos sao obrigatorios.
   */
  buildCreateFormData(payload: CreateProductPayload): FormData {
    const fd = new FormData();
    fd.append('name',       payload.name);
    fd.append('weight',     payload.weight.toString());
    fd.append('photo',      payload.photo, payload.photo.name);
    fd.append('categoryId', payload.categoryId.toString());
    return fd;
  }

  /**
   * Constroi FormData para edicao de produto (PATCH).
   * Apenas os campos presentes no payload serao incluidos.
   */
  buildUpdateFormData(payload: UpdateProductPayload): FormData {
    const fd = new FormData();
    if (payload.name        != null) fd.append('name',       payload.name);
    if (payload.weight      != null) fd.append('weight',     payload.weight.toString());
    if (payload.photo       != null) fd.append('photo',      payload.photo, payload.photo.name);
    if (payload.categoryId  != null) fd.append('categoryId', payload.categoryId.toString());
    if (payload.status      != null) fd.append('status',     payload.status);
    if (payload.position    != null) fd.append('position',   payload.position.toString());
    return fd;
  }
}
