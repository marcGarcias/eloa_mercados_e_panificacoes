import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import {
  Product,
  ProductAdminResponse,
  ProductStatus,
  CreateProductPayload,
  UpdateProductPayload,
  SpringPage,
} from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly apiUrl = (environment?.apiUrl ?? '') + '/api/admin/products';

  // ----------------------------------------------------------------
  // Dados mock legados (mantidos para compatibilidade com CatalogoComponent)
  // ----------------------------------------------------------------
  private readonly products: Product[] = [
    { id: 1,  imagem: '', nome: 'Pao Frances',             categoria: 'Paes',     peso: '50g',   status: 'ativo',   order: 0  },
    { id: 2,  imagem: '', nome: 'Pao de Forma Integral',   categoria: 'Paes',     peso: '500g',  status: 'ativo',   order: 1  },
    { id: 3,  imagem: '', nome: 'Croissant Amanteigado',   categoria: 'Paes',     peso: '80g',   status: 'ativo',   order: 2  },
    { id: 4,  imagem: '', nome: 'Brigadeiro Gourmet',      categoria: 'Doces',    peso: '25g',   status: 'ativo',   order: 3  },
    { id: 5,  imagem: '', nome: 'Bolo de Chocolate',       categoria: 'Bolos',    peso: '1kg',   status: 'ativo',   order: 4  },
    { id: 6,  imagem: '', nome: 'Bolo de Cenoura',         categoria: 'Bolos',    peso: '900g',  status: 'inativo', order: 5  },
    { id: 7,  imagem: '', nome: 'Coxinha de Frango',       categoria: 'Salgados', peso: '120g',  status: 'ativo',   order: 6  },
    { id: 8,  imagem: '', nome: 'Esfiha de Carne',         categoria: 'Salgados', peso: '100g',  status: 'ativo',   order: 7  },
    { id: 9,  imagem: '', nome: 'Suco Natural',            categoria: 'Bebidas',  peso: '300ml', status: 'ativo',   order: 8  },
    { id: 10, imagem: '', nome: 'Refrigerante Lata',       categoria: 'Bebidas',  peso: '350ml', status: 'ativo',   order: 9  },
    { id: 11, imagem: '', nome: 'Sonho de Doce de Leite',  categoria: 'Doces',    peso: '60g',   status: 'inativo', order: 10 },
    { id: 12, imagem: '', nome: 'Pao Doce com Coco',       categoria: 'Paes',     peso: '70g',   status: 'ativo',   order: 11 },
  ];

  constructor(private readonly http: HttpClient) {}

  // ----------------------------------------------------------------
  // Metodos legados (mantidos para compatibilidade)
  // ----------------------------------------------------------------

  /** @deprecated Usar searchAdmin() para integracao com a API */
  getAll(): Observable<Product[]> {
    return of([...this.products].sort((a, b) => (a.order ?? 0) - (b.order ?? 0)));
  }

  /** @deprecated Categorias fixas; usar CategoryAdminService.getAll() */
  getCategories(): string[] {
    return ['Todos', ...new Set(this.products.map(p => p.categoria))];
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
    // TODO: Substituir por chamada HTTP real:
    // let params = new HttpParams()
    //   .set('page', String(filters.page ?? 0))
    //   .set('size', String(filters.size ?? 10));
    // if (filters.name)       params = params.set('name', filters.name);
    // if (filters.categoryId) params = params.set('categoryId', String(filters.categoryId));
    // if (filters.status)     params = params.set('status', filters.status);
    // return this.http.get<SpringPage<ProductAdminResponse>>(this.apiUrl, { params });

    // Mock temporario
    const mockPage: SpringPage<ProductAdminResponse> = {
      content: [], totalElements: 0, totalPages: 0, number: 0, size: 10
    };
    return of(mockPage);
  }

  /**
   * Cria um novo produto.
   * POST /api/admin/products (multipart/form-data)
   *
   * @param payload - Dados do produto com foto WebP obrigatoria
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  create(payload: CreateProductPayload): Observable<ProductAdminResponse> {
    // TODO: Substituir por chamada HTTP real:
    // const formData = this.buildCreateFormData(payload);
    // return this.http.post<ProductAdminResponse>(this.apiUrl, formData);
    throw new Error('ProductService.create() nao implementado — integracao futura');
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
    // TODO: Substituir por chamada HTTP real:
    // const formData = this.buildUpdateFormData(payload);
    // return this.http.patch<ProductAdminResponse>(`${this.apiUrl}/${id}`, formData);
    throw new Error('ProductService.update() nao implementado — integracao futura');
  }

  /**
   * Remove um produto.
   * DELETE /api/admin/products/{id}
   *
   * @param id - ID do produto
   * TODO: Descomentar chamada HTTP quando integrar o backend
   */
  delete(id: number): Observable<void> {
    // TODO: Substituir por chamada HTTP real:
    // return this.http.delete<void>(`${this.apiUrl}/${id}`);
    throw new Error('ProductService.delete() nao implementado — integracao futura');
  }

  /**
   * Remove múltiplos produtos em lote.
   * Exclui os produtos cujos IDs foram informados.
   */
  deleteProducts(ids: number[]): Observable<void> {
    // TODO: Substituir por chamada HTTP real:
    // return this.http.post<void>(`${this.apiUrl}/batch-delete`, { ids });
    return of(undefined);
  }

  /**
   * Atualiza a ordem de exibição dos produtos em lote.
   */
  updateOrder(products: {id: number, position: number}[]): Observable<void> {
    // TODO: Substituir por chamada HTTP real:
    // return this.http.put<void>(`${this.apiUrl}/order`, products);
    return of(undefined);
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
