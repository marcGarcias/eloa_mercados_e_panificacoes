import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { CategoryAdminService } from '../../services/category-admin.service';
import { ToastService } from '../../services/toast.service';
import { Product, ProductAdminResponse, CategoryAdminResponse, ProductStatus } from '../../models/product.model';
import { ModalProdutoComponent } from '../../shared/modal-produto/modal-produto.component';
import { ModalCategoriaComponent } from '../../shared/modal-categoria/modal-categoria.component';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { forkJoin, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

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

  // Atributos de paginação e loading
  isLoading: boolean = false;
  page: number = 0;
  size: number = 12;
  totalPages: number = 0;

  private readonly searchSubject = new Subject<string>();

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
    private readonly toastService: ToastService,
    private readonly cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadAdminCategories();

    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.page = 0;
      this.loadProducts();
    });
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.page++;
      this.loadProducts();
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm);
  }

  // ----------------------------------------------------------------
  // Carregamento de dados
  // ----------------------------------------------------------------

  private loadProducts(): void {
    this.isLoading = true;
    this.cdr.markForCheck();

    const categoryId = this.adminCategories.find(c => c.name === this.activeFilter)?.id;
    const name = this.searchTerm.trim() || undefined;

    this.productService.searchAdmin({ 
      page: this.page, 
      size: this.size,
      categoryId,
      name
    }).subscribe({
      next: (page) => {
        this.products = page.content.map(p => ({
          id: p.id,
          nome: p.name,
          categoria: p.categoryName,
          peso: this.formatWeight(p.weight),
          status: p.status === ProductStatus.ACTIVE ? 'ativo' : 'inativo',
          imagem: this.productService.getProductImageUrl(p.photo),
          order: p.position
        }));
        
        // Ordena os produtos pela posicao
        this.products.sort((a, b) => (a.order ?? 0) - (b.order ?? 0));
        this.totalPages = page.totalPages;
        this.isLoading = false;
        this.updateFilterCategories();
        this.cdr.markForCheck();
      },
      error: () => {
        this.isLoading = false;
        this.toastService.error('Falha ao carregar os produtos do catálogo.', 'Erro');
        this.cdr.markForCheck();
      }
    });
  }

  private loadAdminCategories(): void {
    this.categoryAdminService.getAll().subscribe(cats => {
      this.adminCategories = cats;
      this.updateFilterCategories();
      this.cdr.markForCheck();
    });
  }

  private updateFilterCategories(): void {
    const catNames = this.adminCategories.map(c => c.name);
    this.categories = ['Todos', ...catNames];
  }

  private formatWeight(weight: number): string {
    if (!weight) return '0g';
    if (weight >= 1) {
      return `${weight}kg`;
    } else {
      return `${Math.round(weight * 1000)}g`;
    }
  }

  // ----------------------------------------------------------------
  // Filtros de listagem
  // ----------------------------------------------------------------

  setFilter(cat: string): void {
    this.activeFilter = cat;
    this.page = 0;
    this.loadProducts();
  }

  get visibleCategories(): string[] {
    return this.categories.filter(c => !this.deletedCategoryNames.has(c));
  }

  get filteredProducts(): Product[] {
    return this.products.filter(p => p.id && !this.deletedProductIds.has(p.id));
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
    let weightNum = parseFloat(product.peso) || 0;
    if (product.peso.toLowerCase().endsWith('g') && !product.peso.toLowerCase().endsWith('kg')) {
      weightNum = weightNum / 1000;
    }

    this.editingProduct = {
      id:           product.id ?? 0,
      name:         product.nome,
      weight:       weightNum,
      position:     product.order ?? 0,
      photo:        product.imagem ?? '',
      categoryName: product.categoria,
      status:       product.status === 'ativo' ? ProductStatus.ACTIVE : ProductStatus.INACTIVE,
    };
    this.isProductModalOpen = true;
    this.cdr.markForCheck();
  }

  /** Chamado apos salvar com sucesso */
  onProductSaved(product: ProductAdminResponse): void {
    console.log('[CatalogoComponent] Produto salvo:', product);
    this.toastService.success(`O produto "${product.name}" foi salvo com sucesso.`, 'Produto Salvo');
    this.isProductModalOpen = false;
    this.editingProduct     = null;
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
    this.toastService.success(`A categoria "${category.name}" foi criada com sucesso.`, 'Categoria Criada');
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
      const discard = typeof window !== 'undefined' && window.confirm('Existem alterações não salvas. Deseja descartá-las?');
      if (!discard) {
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

    this.isLoading = true;
    this.cdr.markForCheck();

    // 1. Produtos para deletar
    const productsToDelete = Array.from(this.deletedProductIds);
    // 2. Categorias para deletar (encontrar o ID a partir do nome)
    const categoryIdsToDelete = Array.from(this.deletedCategoryNames)
      .map(name => this.adminCategories.find(c => c.name === name)?.id)
      .filter(id => id != null) as number[];
    // 3. Produtos atualizados (ordem) - exclui os que foram deletados e envia apenas lista ordenada de IDs
    const productsToUpdateOrder = this.products
      .filter(p => p.id && !this.deletedProductIds.has(p.id))
      .map(p => p.id!);

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

    if (requests.length === 0) {
      this.isLoading = false;
      this.cdr.markForCheck();
      return;
    }

    forkJoin(requests).subscribe({
      next: () => {
        this.toastService.success('As alterações do catálogo foram salvas com sucesso.', 'Catálogo Atualizado');
        this.isEditMode = false;
        this.deletedProductIds.clear();
        this.deletedCategoryNames.clear();
        this.hasOrderChanges = false;
        this.page = 0; // Reseta para primeira página após salvar em lote
        
        this.loadProducts();
        this.loadAdminCategories();
        this.cdr.markForCheck();
      },
      error: (err) => {
        this.isLoading = false;
        const rawMsg = err?.error?.message;
        const msg = rawMsg || 'Falha ao salvar as alterações do catálogo.';
        this.toastService.error(msg, 'Erro ao Salvar');
        this.cdr.markForCheck();
      }
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
