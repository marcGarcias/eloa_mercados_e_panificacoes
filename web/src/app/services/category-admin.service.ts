import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';
import { CategoryAdminResponse } from '../models/product.model';

/**
 * Service para operacoes de categoria no contexto Admin.
 * Consome GET /api/admin/categories => CategoryAdmResponse { id, name }.
 *
 * Por enquanto retorna dados mock para desenvolvimento do front-end.
 * A integracao real com o backend sera feita em iteracao futura.
 */
@Injectable({ providedIn: 'root' })
export class CategoryAdminService {
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/admin/categories';

  // Dados mock para desenvolvimento — remover quando integrar com a API real
  private readonly mockCategories: CategoryAdminResponse[] = [
    { id: 1, name: 'Paes' },
    { id: 2, name: 'Doces' },
    { id: 3, name: 'Bolos' },
    { id: 4, name: 'Salgados' },
    { id: 5, name: 'Bebidas' },
  ];

  constructor(private readonly http: HttpClient) {}

  /**
   * Lista todas as categorias disponiveis para o formulario de produto.
   * Endpoint: GET /api/admin/categories
   */
  getAll(): Observable<CategoryAdminResponse[]> {
    // TODO: Substituir por chamada HTTP real quando integrar o backend:
    // return this.http.get<CategoryAdminResponse[]>(this.apiUrl);
    return of(this.mockCategories);
  }
}
