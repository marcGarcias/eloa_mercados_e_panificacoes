import { Component, OnInit, ChangeDetectionStrategy, ChangeDetectorRef, HostListener, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../../../services/product.service';
import { CategoryAdminService } from '../../../../services/category-admin.service';
import { ToastService } from '../../../../services/toast.service';
import { Product, ProductAdminResponse, CategoryAdminResponse, ProductStatus } from '../../../../models/product.model';
import { ModalProdutoComponent } from '../../../../shared/modal-produto/modal-produto.component';
import { ModalCategoriaComponent } from '../../../../shared/modal-categoria/modal-categoria.component';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { forkJoin, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalProdutoComponent, ModalCategoriaComponent, DragDropModule],
  templateUrl: './catalog.component.html',
  styleUrls: ['./catalog.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CatalogComponent implements OnInit {
  isMobile: boolean = window.innerWidth < 768;

  @HostListener('window:resize')
  onResize(): void {
    this.isMobile = window.innerWidth < 768;
    this.cdr.markForCheck();
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.category-dropdown-wrapper')) {
      this.isCategoryDropdownOpen = false;
      this.cdr.markForCheck();
    }
  }

  activeTab: 'products' | 'categories' = 'products';

  setActiveTab(tab: 'products' | 'categories'): void {
    this.activeTab = tab;
    if (tab === 'categories' && this.categoryList.length === 0) {
      this.loadPagedCategories();
    }
    this.cdr.markForCheck();
  }

  products: Product[] = [];
  categories: string[] = [];
  activeFilter: string = 'Todos';
  searchTerm: string = '';

  isLoading: boolean = false;
  page: number = 0;
  size: number = 12;
  totalPages: number = 0;
  totalElements: number = 0;

  private readonly searchSubject = new Subject<string>();

  // Dropdown de seleção de categoria na aba de produtos
  isCategoryDropdownOpen: boolean = false;
  categoryFilterSearch: string = '';

  categoryList: CategoryAdminResponse[] = [];
  categorySearchTerm: string = '';
  categoryPage: number = 0;
  categorySize: number = 10;
  categoryTotalPages: number = 0;
  categoryTotalElements: number = 0;
  isCategoryLoading: boolean = false;

  private readonly categorySubject = new Subject<string>();

  isProductModalOpen: boolean = false;
  isCategoryModalOpen: boolean = false;
  editingProduct: ProductAdminResponse | null = null;
  adminCategories: CategoryAdminResponse[] = [];

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
    this.loadPagedCategories();

    this.searchSubject.pipe(
      debounceTime(400),
      distinctUntilChanged()
    ).subscribe(() => {
      this.page = 0;
      this.loadProducts();
    });

    this.categorySubject.pipe(
      debounceTime(350),
      distinctUntilChanged()
    ).subscribe(() => {
      this.categoryPage = 0;
      this.loadPagedCategories();
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

  goToPage(p: number): void {
    if (p >= 0 && p < this.totalPages && p !== this.page) {
      this.page = p;
      this.loadProducts();
    }
  }

  getPageNumbers(currentPage: number, totalPages: number): number[] {
    const delta = 2;
    const range: number[] = [];
    for (let i = Math.max(0, currentPage - delta); i <= Math.min(totalPages - 1, currentPage + delta); i++) {
      range.push(i);
    }
    return range;
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchTerm);
  }

  nextCategoryPage(): void {
    if (this.categoryPage < this.categoryTotalPages - 1) {
      this.categoryPage++;
      this.loadPagedCategories();
    }
  }

  prevCategoryPage(): void {
    if (this.categoryPage > 0) {
      this.categoryPage--;
      this.loadPagedCategories();
    }
  }

  goToCategoryPage(p: number): void {
    if (p >= 0 && p < this.categoryTotalPages && p !== this.categoryPage) {
      this.categoryPage = p;
      this.loadPagedCategories();
    }
  }

  onCategorySearchChange(): void {
    this.categorySubject.next(this.categorySearchTerm);
  }

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
    }).pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
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
        
        this.products.sort((a, b) => (a.order ?? 0) - (b.order ?? 0));
        this.totalPages = page.totalPages;
        this.totalElements = page.totalElements;
        this.updateFilterCategories();
      },
      error: () => {
        this.toastService.error('Falha ao carregar os produtos do catálogo.', 'Erro');
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

  loadPagedCategories(): void {
    this.isCategoryLoading = true;
    this.cdr.markForCheck();

    this.categoryAdminService.search(
      this.categoryPage,
      this.categorySize,
      this.categorySearchTerm.trim() || undefined
    ).pipe(
      finalize(() => {
        this.isCategoryLoading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: (page) => {
        this.categoryList = page.content;
        this.categoryTotalPages = page.totalPages;
        this.categoryTotalElements = page.totalElements;
      },
      error: () => {
        this.toastService.error('Falha ao buscar lista paginada de categorias.', 'Erro');
      }
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

  setFilter(cat: string): void {
    this.activeFilter = cat;
    this.page = 0;
    this.isCategoryDropdownOpen = false;
    this.loadProducts();
  }

  toggleCategoryDropdown(event?: Event): void {
    if (event) event.stopPropagation();
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
    if (this.isCategoryDropdownOpen) {
      this.categoryFilterSearch = '';
    }
  }

  get filteredAdminCategoriesForDropdown(): CategoryAdminResponse[] {
    if (!this.categoryFilterSearch.trim()) {
      return this.adminCategories;
    }
    const q = this.categoryFilterSearch.toLowerCase().trim();
    return this.adminCategories.filter(c => c.name.toLowerCase().includes(q));
  }


  get visibleCategories(): string[] {
    return this.categories.filter(c => !this.deletedCategoryNames.has(c));
  }

  get filteredProducts(): Product[] {
    return this.products.filter(p => p.id && !this.deletedProductIds.has(p.id));
  }

  openCreateModal(): void {
    this.loadAdminCategories();
    this.editingProduct       = null;
    this.isProductModalOpen   = true;
    this.cdr.markForCheck();
  }

  openEditModal(product: Product): void {
    this.loadAdminCategories();
    let weightNum = parseFloat(product.peso) || 0;
    if (product.peso.toLowerCase().endsWith('g') && !product.peso.toLowerCase().endsWith('kg')) {
      weightNum = weightNum / 1000;
    }

    this.editingProduct = {
      id: product.id ?? 0,
      name: product.nome,
      weight: weightNum,
      position: product.order ?? 0,
      photo: product.imagem ?? '',
      categoryName: product.categoria,
      status: product.status === 'ativo' ? ProductStatus.ACTIVE : ProductStatus.INACTIVE,
    };
    this.isProductModalOpen = true;
    this.cdr.markForCheck();
  }

  onProductSaved(product: ProductAdminResponse): void {
    this.toastService.success(`O produto "${product.name}" foi salvo com sucesso.`, 'Produto Salvo');
    this.isProductModalOpen = false;
    this.editingProduct = null;
    this.loadProducts();
    this.cdr.markForCheck();
  }

  onProductModalClosed(): void {
    this.isProductModalOpen = false;
    this.editingProduct = null;
    this.cdr.markForCheck();
  }

  openCreateCategoryModal(): void {
    this.isCategoryModalOpen = true;
    this.cdr.markForCheck();
  }

  onCategorySaved(category: CategoryAdminResponse): void {
    this.toastService.success(`A categoria "${category.name}" foi criada com sucesso.`, 'Categoria Criada');
    this.isCategoryModalOpen = false;
    if (!this.adminCategories.some(c => c.id === category.id)) {
      this.adminCategories = [...this.adminCategories, category];
      this.updateFilterCategories();
    }
    this.loadAdminCategories();
    this.loadProducts();
    this.cdr.markForCheck();
  }

  onCategoryModalClosed(): void {
    this.isCategoryModalOpen = false;
    this.cdr.markForCheck();
  }

  deleteCategoryDirectly(cat: CategoryAdminResponse): void {
    const confirmDelete = window.confirm(`Deseja realmente excluir a categoria "${cat.name}"?`);
    if (!confirmDelete) return;

    this.isCategoryLoading = true;
    this.cdr.markForCheck();

    this.categoryAdminService.delete(cat.id).pipe(
      finalize(() => {
        this.isCategoryLoading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toastService.success(`Categoria "${cat.name}" removida com sucesso.`, 'Categoria Excluída');
        if (this.activeFilter === cat.name) {
          this.activeFilter = 'Todos';
        }
        this.loadAdminCategories();
        this.loadPagedCategories();
        this.loadProducts();
      },
      error: (err) => {
        const rawMsg = err?.error?.message;
        const msg = rawMsg || `Não foi possível excluir a categoria "${cat.name}". Verifique se há produtos vinculados a ela.`;
        this.toastService.error(msg, 'Erro ao Excluir');
      }
    });
  }

  toggleEditMode(): void {
    if (this.isEditMode && this.hasChanges) {
      const discard = typeof window !== 'undefined' && window.confirm('Existem alterações não salvas. Deseja descartá-las?');
      if (!discard) {
        return;
      }
    }
    this.isEditMode = !this.isEditMode;
    if (!this.isEditMode) {
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

    const hasLinkedProducts = this.products.some(
      p => p.categoria === name && p.id && !this.deletedProductIds.has(p.id)
    );

    if (hasLinkedProducts) {
      alert(`Não é possível excluir a categoria "${name}" pois ela possui produtos vinculados. Exclua ou mova os produtos antes de remover a categoria.`);
      return;
    }

    this.deletedCategoryNames.add(name);
    if (this.activeFilter === name) {
      this.activeFilter = 'Todos';
    }
    this.cdr.markForCheck();
  }

  saveChanges(): void {
    if (!this.hasChanges) return;

    this.isLoading = true;
    this.cdr.markForCheck();

    const productsToDelete = Array.from(this.deletedProductIds);
    const categoryIdsToDelete = Array.from(this.deletedCategoryNames)
      .map(name => this.adminCategories.find(c => c.name === name)?.id)
      .filter(id => id != null) as number[];
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

    forkJoin(requests).pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
      })
    ).subscribe({
      next: () => {
        this.toastService.success('As alterações do catálogo foram salvas com sucesso.', 'Catálogo Atualizado');
        this.isEditMode = false;
        this.deletedProductIds.clear();
        this.deletedCategoryNames.clear();
        this.hasOrderChanges = false;
        this.page = 0;
        
        this.loadProducts();
        this.loadAdminCategories();
        this.loadPagedCategories();
      },
      error: (err) => {
        const rawMsg = err?.error?.message;
        const msg = rawMsg || 'Falha ao salvar as alterações do catálogo.';
        this.toastService.error(msg, 'Erro ao Salvar');
      }
    });
  }

  onDrop(event: CdkDragDrop<Product[]>): void {
    if (!this.isEditMode || this.activeFilter !== 'Todos') return;

    moveItemInArray(this.products, event.previousIndex, event.currentIndex);
    
    this.products.forEach((p, index) => {
      p.order = index;
    });
    
    this.hasOrderChanges = true;
    this.cdr.markForCheck();
  }

  moveProductUp(index: number, event: Event): void {
    event.stopPropagation();
    if (index > 0) {
      moveItemInArray(this.products, index, index - 1);
      this.products.forEach((p, idx) => {
        p.order = idx;
      });
      this.hasOrderChanges = true;
      this.cdr.markForCheck();
    }
  }

  moveProductDown(index: number, event: Event): void {
    event.stopPropagation();
    if (index < this.products.length - 1) {
      moveItemInArray(this.products, index, index + 1);
      this.products.forEach((p, idx) => {
        p.order = idx;
      });
      this.hasOrderChanges = true;
      this.cdr.markForCheck();
    }
  }
}
