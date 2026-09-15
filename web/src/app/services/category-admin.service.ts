import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, Subject, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategoryAdminResponse, SpringPage } from '../models/product.model';

@Injectable({ providedIn: 'root' })
export class CategoryAdminService {
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/admin/categories';

  private readonly _categoriesUpdated$ = new Subject<void>();
  readonly categoriesUpdated$ = this._categoriesUpdated$.asObservable();

  constructor(private readonly http: HttpClient) {}

  notifyCategoriesUpdated(): void {
    this._categoriesUpdated$.next();
  }

  getAll(): Observable<CategoryAdminResponse[]> {
    return this.http.get<CategoryAdminResponse[]>(this.apiUrl);
  }

  search(page: number = 0, size: number = 10, name?: string): Observable<SpringPage<CategoryAdminResponse>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size));
    if (name && name.trim()) {
      params = params.set('name', name.trim());
    }
    return this.http.get<SpringPage<CategoryAdminResponse>>(this.apiUrl, { params });
  }

  create(name: string): Observable<CategoryAdminResponse> {
    return this.http.post<CategoryAdminResponse>(this.apiUrl, { name }).pipe(
      tap(() => this.notifyCategoriesUpdated())
    );
  }

  update(id: number, name: string): Observable<CategoryAdminResponse> {
    return this.http.put<CategoryAdminResponse>(`${this.apiUrl}/${id}`, { name }).pipe(
      tap(() => this.notifyCategoriesUpdated())
    );
  }

  /**
   * Remove uma categoria individualmente.
   * Endpoint: DELETE /api/admin/categories/{id}
   */
  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`).pipe(
      tap(() => this.notifyCategoriesUpdated())
    );
  }

  /**
   * Remove múltiplas categorias em lote.
   * Endpoint: POST /api/admin/categories/batch-delete
   */
  deleteCategories(ids: number[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/batch-delete`, { ids }).pipe(
      tap(() => this.notifyCategoriesUpdated())
    );
  }
}

