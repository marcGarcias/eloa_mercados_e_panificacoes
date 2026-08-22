import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { CategoryAdminService } from '../../services/category-admin.service';
import { Product, ProductAdminResponse, CategoryAdminResponse } from '../../models/product.model';
import { ModalProdutoComponent } from '../../shared/modal-produto/modal-produto.component';
import { ModalCategoriaComponent } from '../../shared/modal-categoria/modal-categoria.component';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalProdutoComponent, ModalCategoriaComponent, DragDropModule],
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
  // Estado do modal de produto e categoria
  // ----------------------------------------------------------------

  /** Controla visibilidade do modal de produto */
  isProductModalOpen: boolean = false;

  /** Controla visibilidade do modal de categoria */
  isCategoryModalOpen: boolean = false;

  /** Produto sendo editado; null = modo criacao */
  editingProduct: ProductAdminResponse | null = null;

  /** Categorias para o select do modal (com id+name, da API admin) */
  adminCategories: CategoryAdminResponse[] = [];

  // ----------------------------------------------------------------
  // Estado do Modo de Edição
  // ----------------------------------------------------------------

  isEditMode: boolean = false;
  deletedProductIds: Set<number> = new Set();
  deletedCategoryNames: Set<string> = new Set();
  hasOrderChanges: boolean = false;

  get hasChanges(): boolean {
    return this.deletedProductIds.size > 0 || this.deletedCategoryNames.size > 0 || this.hasOrderChanges;
  }

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

  get visibleCategories(): string[] {
    return this.categories.filter(c => !this.deletedCategoryNames.has(c));
  }

  get filteredProducts(): Product[] {
    let result = this.products.filter(p => p.id && !this.deletedProductIds.has(p.id));
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
  // Acoes do modal de categoria
  // ----------------------------------------------------------------

  openCreateCategoryModal(): void {
    this.isCategoryModalOpen = true;
    this.cdr.markForCheck();
  }

  onCategorySaved(category: CategoryAdminResponse): void {
    console.log('[CatalogoComponent] Categoria salva:', category);
    this.isCategoryModalOpen = false;
    this.loadAdminCategories(); // Recarrega categorias da API
    this.loadProducts(); // No mundo real, caso mude algo global
    this.cdr.markForCheck();
  }

  onCategoryModalClosed(): void {
    this.isCategoryModalOpen = false;
    this.cdr.markForCheck();
  }

  // ----------------------------------------------------------------
  // Modo de Edição e Salvamento em Lote
  // ----------------------------------------------------------------

  toggleEditMode(): void {
    if (this.isEditMode && this.hasChanges) {
      if (!confirm('Existem alterações não salvas. Deseja descartá-las?')) {
        return;
      }
    }
    this.isEditMode = !this.isEditMode;
    if (!this.isEditMode) {
      // Descarta alterações e recarrega tudo
      this.deletedProductIds.clear();
      this.deletedCategoryNames.clear();
      this.hasOrderChanges = false;
      this.loadProducts();
    }
    this.cdr.markForCheck();
  }

  markProductForDeletion(id: number): void {
    this.deletedProductIds.add(id);
    this.cdr.markForCheck();
  }

  markCategoryForDeletion(name: string, event: Event): void {
    event.stopPropagation();

    // Bloquear exclusão se houver produtos vinculados à categoria
    const hasLinkedProducts = this.products.some(
      p => p.categoria === name && p.id && !this.deletedProductIds.has(p.id)
    );

    if (hasLinkedProducts) {
      alert(`Não é possível excluir a categoria "${name}" pois ela possui produtos vinculados. Exclua ou mova os produtos antes de remover a categoria.`);
      return;
    }

    this.deletedCategoryNames.add(name);
    // Se excluiu o filtro ativo, muda para 'Todos'
    if (this.activeFilter === name) {
      this.activeFilter = 'Todos';
    }
    this.cdr.markForCheck();
  }

  saveChanges(): void {
    if (!this.hasChanges) return;

    // 1. Produtos para deletar
    const productsToDelete = Array.from(this.deletedProductIds);
    // 2. Categorias para deletar (encontrar o ID a partir do nome)
    const categoryIdsToDelete = Array.from(this.deletedCategoryNames)
      .map(name => this.adminCategories.find(c => c.name === name)?.id)
      .filter(id => id != null) as number[];
    // 3. Produtos atualizados (ordem) - exclui os que foram deletados
    const productsToUpdateOrder = this.products
      .filter(p => p.id && !this.deletedProductIds.has(p.id))
      .map(p => ({ id: p.id!, position: p.order ?? 0 }));

    const requests = [];

    if (productsToDelete.length > 0) {
      requests.push(this.productService.deleteProducts(productsToDelete));
    }
    if (categoryIdsToDelete.length > 0) {
      requests.push(this.categoryAdminService.deleteCategories(categoryIdsToDelete));
    }
    if (this.hasOrderChanges) {
      requests.push(this.productService.updateOrder(productsToUpdateOrder));
    }

    if (requests.length === 0) return;

    forkJoin(requests).subscribe(() => {
      console.log('[CatalogoComponent] Alterações em lote salvas com sucesso.');
      this.isEditMode = false;
      this.deletedProductIds.clear();
      this.deletedCategoryNames.clear();
      this.hasOrderChanges = false;
      
      this.loadProducts();
      this.loadAdminCategories();
      this.cdr.markForCheck();
    });
  }

  // ----------------------------------------------------------------
  // Drag and Drop (Ordenacao)
  // ----------------------------------------------------------------

  onDrop(event: CdkDragDrop<Product[]>): void {
    // Só permite reordenar se estiver visualizando "Todos" e em modo edição
    if (!this.isEditMode || this.activeFilter !== 'Todos') return;

    moveItemInArray(this.products, event.previousIndex, event.currentIndex);
    
    // Atualiza a propriedade 'order' localmente para todos
    this.products.forEach((p, index) => {
      p.order = index;
    });
    
    this.hasOrderChanges = true;
    this.cdr.markForCheck();
  }
}
