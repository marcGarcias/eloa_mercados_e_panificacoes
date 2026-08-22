import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { CategoryAdminService } from '../../services/category-admin.service';
import { Product, ProductAdminResponse, CategoryAdminResponse } from '../../models/product.model';
import { ModalProdutoComponent } from '../../shared/modal-produto/modal-produto.component';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalProdutoComponent, DragDropModule],
  templateUrl: './catalogo.component.html',
  styleUrls: ['./catalogo.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CatalogoComponent implements OnInit {

  // ----------------------------------------------------------------
  // Estado de listagem (legado mock — substituir por API futuramente)
  // ----------------------------------------------------------------

  /** Produtos carregados do mock (legado). Substituir por ProductAdminResponse[] na integracao */
  products: Product[] = [];

  /** Categorias para filtros de listagem (legado string) */
  categories: string[] = [];

  /** Filtro ativo na listagem */
  activeFilter: string = 'Todos';

  /** Termo de busca por nome */
  searchTerm: string = '';

  // ----------------------------------------------------------------
  // Estado do modal de produto
  // ----------------------------------------------------------------

  /** Controla visibilidade do modal de produto */
  isProductModalOpen: boolean = false;

  /** Produto sendo editado; null = modo criacao */
  editingProduct: ProductAdminResponse | null = null;

  /** Categorias para o select do modal (com id+name, da API admin) */
  adminCategories: CategoryAdminResponse[] = [];

  constructor(
    private readonly productService: ProductService,
    private readonly categoryAdminService: CategoryAdminService,
    private readonly cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadAdminCategories();
  }

  // ----------------------------------------------------------------
  // Carregamento de dados
  // ----------------------------------------------------------------

  private loadProducts(): void {
    this.productService.getAll().subscribe(data => {
      this.products  = data;
      this.categories = this.productService.getCategories();
      this.cdr.markForCheck();
    });
  }

  private loadAdminCategories(): void {
    this.categoryAdminService.getAll().subscribe(cats => {
      this.adminCategories = cats;
      this.cdr.markForCheck();
    });
  }

  // ----------------------------------------------------------------
  // Filtros de listagem
  // ----------------------------------------------------------------

  setFilter(cat: string): void {
    this.activeFilter = cat;
  }

  get filteredProducts(): Product[] {
    let result = this.products;
    if (this.activeFilter !== 'Todos') {
      result = result.filter(p => p.categoria === this.activeFilter);
    }
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.trim().toLowerCase();
      result = result.filter(p => p.nome.toLowerCase().includes(term));
    }
    return result;
  }

  // ----------------------------------------------------------------
  // Acoes do modal de produto
  // ----------------------------------------------------------------

  /** Abre modal em modo criacao */
  openCreateModal(): void {
    this.editingProduct       = null;
    this.isProductModalOpen   = true;
    this.cdr.markForCheck();
  }

  /**
   * Abre modal em modo edicao com o produto selecionado.
   * Por ora converte Product (legado) para ProductAdminResponse parcial.
   * Substituir por chamada direta com ProductAdminResponse na integracao real.
   */
  openEditModal(product: Product): void {
    // Converte o model legado para ProductAdminResponse parcial ate a integracao
    this.editingProduct = {
      id:           product.id ?? 0,
      name:         product.nome,
      weight:       parseFloat(product.peso) || 0,
      position:     product.order ?? 0,
      photo:        product.imagem ?? '',
      categoryName: product.categoria,
      status:       product.status === 'ativo'
                      ? ('' as any)  // mapeado como ProductStatus.ACTIVE
                      : ('' as any), // mapeado como ProductStatus.INACTIVE
    };
    this.isProductModalOpen = true;
    this.cdr.markForCheck();
  }

  /** Chamado apos salvar com sucesso */
  onProductSaved(product: ProductAdminResponse): void {
    console.log('[CatalogoComponent] Produto salvo:', product);
    this.isProductModalOpen = false;
    this.editingProduct     = null;
    // TODO: recarregar lista via ProductService.searchAdmin() na integracao real
    this.loadProducts();
    this.cdr.markForCheck();
  }

  /** Chamado ao fechar o modal sem salvar */
  onProductModalClosed(): void {
    this.isProductModalOpen = false;
    this.editingProduct     = null;
    this.cdr.markForCheck();
  }

  // ----------------------------------------------------------------
  // Drag and Drop (Ordenacao)
  // ----------------------------------------------------------------

  onDrop(event: CdkDragDrop<Product[]>): void {
    // Só permite reordenar se estiver visualizando "Todos"
    if (this.activeFilter !== 'Todos') return;

    moveItemInArray(this.products, event.previousIndex, event.currentIndex);
    
    // Atualiza a propriedade 'order' localmente para todos
    this.products.forEach((p, index) => {
      p.order = index;
    });
    
    // TODO: Na integracao real, chamar o backend para salvar a nova ordem
    // this.productService.updateOrder(this.products.map(p => p.id)).subscribe();
    
    this.cdr.markForCheck();
  }
}
