// ===================================================
// Tipos legados (compatibilidade com componentes públicos)
// ===================================================

/** @deprecated Use ProductAdminResponse para o contexto admin */
export interface Product {
  id?: number;
  nome: string;
  categoria: string;
  peso: string;
  status?: 'ativo' | 'inativo';
  imagem: string | null;
  order?: number;
}

// ===================================================
// Tipos Admin — alinhados com a API (ProductAdminResponse)
// ===================================================

/** Espelho do enum ProductStatus do backend */
export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
}

/** Espelho de ProductAdminResponse */
export interface ProductAdminResponse {
  id: number;
  name: string;
  weight: number;
  position: number;
  photo: string;
  categoryName: string;
  status: ProductStatus;
}

/** Espelho de CategoryAdmResponse */
export interface CategoryAdminResponse {
  id: number;
  name: string;
}

/**
 * Payload de criação de produto.
 * Todos os campos são obrigatórios para o POST.
 * Será serializado como multipart/form-data.
 */
export interface CreateProductPayload {
  name: string;
  weight: number;
  photo: File; // WebP obrigatório — validado na API
  categoryId: number;
}

/**
 * Payload de edição de produto.
 * Todos os campos são opcionais para o PATCH.
 * Será serializado como multipart/form-data.
 */
export interface UpdateProductPayload {
  name?: string;
  weight?: number;
  photo?: File; // WebP se enviado — validado na API
  categoryId?: number;
  status?: ProductStatus;
  position?: number;
}

/** Wrapper de paginação do Spring (Page<T>) */
export interface SpringPage<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ProductPublicResponse {
  name: string;
  weight: number;
  photoUrl: string | null;
  categoryName: string;
  position: number;
}
