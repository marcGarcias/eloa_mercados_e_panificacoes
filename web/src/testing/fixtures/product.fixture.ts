import { Product, ProductAdminResponse, ProductStatus, CategoryAdminResponse } from '../../app/models/product.model';

export function createMockProduct(overrides: Partial<Product> = {}): Product {
  return {
    id: 1,
    nome: 'Pão Francês',
    categoria: 'Padaria',
    peso: '50g',
    status: 'ativo',
    imagem: 'https://exemplo.com/pao.webp',
    order: 1,
    ...overrides
  };
}

export function createMockProductAdmin(overrides: Partial<ProductAdminResponse> = {}): ProductAdminResponse {
  return {
    id: 1,
    name: 'Pão de Queijo',
    weight: 100,
    position: 1,
    photo: 'https://exemplo.com/pao-queijo.webp',
    categoryName: 'Padaria',
    status: ProductStatus.ACTIVE,
    ...overrides
  };
}

export function createMockCategory(overrides: Partial<CategoryAdminResponse> = {}): CategoryAdminResponse {
  return {
    id: 10,
    name: 'Padaria e Confeitaria',
    ...overrides
  };
}

