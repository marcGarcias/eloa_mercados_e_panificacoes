export interface Product {
  id?: number;
  nome: string;
  categoria: string;
  peso: string;
  status?: 'ativo' | 'inativo';
  imagem: string | null;
  order?: number;
}

export enum ProductStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
}

export interface ProductAdminResponse {
  id: number;
  name: string;
  weight: number;
  position: number;
  photo: string;
  categoryName: string;
  status: ProductStatus;
}

export interface CategoryAdminResponse {
  id: number;
  name: string;
}

export interface CreateProductPayload {
  name: string;
  weight: number;
  photo: File;
  categoryId: number;
}

export interface UpdateProductPayload {
  name?: string;
  weight?: number;
  photo?: File;
  categoryId?: number;
  status?: ProductStatus;
  position?: number;
}

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

