import { Component, OnInit, OnDestroy, Input, HostListener, ElementRef, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject, Subscription } from 'rxjs';
import { debounceTime, distinctUntilChanged, finalize } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { SeoService } from '../../services/seo.service';
import { Product } from '../../models/product.model';
import { ContentCatalogo } from '../../models/content.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent implements OnInit, OnDestroy {
  @Input() catalogo?: ContentCatalogo | null;

  products: Product[] = [];
  categories: string[] = [];
  filteredCategories: string[] = [];
  activeCategory: string = 'Todos';

  searchQuery: string = '';
  categorySearchQuery: string = '';
  isCategoryDropdownOpen: boolean = false;

  page: number = 0;
  size: number = 12;
  totalPages: number = 0;
  totalElements: number = 0;
  isLoading: boolean = false;

  private readonly searchSubject = new Subject<string>();
  private readonly subs = new Subscription();
  private productSub?: Subscription;

  get totalCategoriesCount(): number {
    return this.categories.length;
  }

  constructor(
    private readonly productService: ProductService,
    private readonly seoService: SeoService,
    private readonly elementRef: ElementRef,
    private readonly cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();

    this.subs.add(
      this.searchSubject.pipe(
        debounceTime(350),
        distinctUntilChanged()
      ).subscribe(() => {
        this.page = 0;
        this.loadProducts();
      })
    );
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
    if (this.productSub) {
      this.productSub.unsubscribe();
    }
  }

  loadCategories(): void {
    this.subs.add(
      this.productService.getPublicCategories().subscribe({
        next: (cats) => {
          this.categories = cats;
          this.filteredCategories = [...cats];
          this.cdr.markForCheck();
        },
        error: () => {
          this.categories = [];
          this.filteredCategories = [];
          this.cdr.markForCheck();
        }
      })
    );
  }

  loadProducts(): void {
    if (this.productSub) {
      this.productSub.unsubscribe();
    }

    this.isLoading = true;
    this.cdr.markForCheck();

    this.productSub = this.productService.searchPublic({
      name: this.searchQuery,
      categoryName: this.activeCategory === 'Todos' ? undefined : this.activeCategory,
      page: this.page,
      size: this.size
    }).pipe(
      finalize(() => {
        this.isLoading = false;
        this.cdr.markForCheck();
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (pageData) => {
        this.totalElements = pageData.totalElements;
        this.totalPages = pageData.totalPages;
        this.products = (pageData.content || []).map(p => ({
          nome: p.name,
          categoria: p.categoryName,
          peso: p.weight ? `${p.weight.toString().replace('.', ',')} kg` : '',
          imagem: this.productService.getProductImageUrl(p.photoUrl),
          order: Number(p.position)
        }));
        this.seoService.updateCatalogStructuredData(this.products, this.categories);
        this.cdr.markForCheck();
      },
      error: () => {
        this.products = [];
        this.totalPages = 0;
        this.totalElements = 0;
        this.cdr.markForCheck();
      }
    });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchQuery);
  }

  clearSearch(): void {
    this.searchQuery = '';
    this.page = 0;
    this.loadProducts();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.activeCategory = 'Todos';
    this.categorySearchQuery = '';
    this.isCategoryDropdownOpen = false;
    this.page = 0;
    this.loadProducts();
  }

  selectCategory(cat: string): void {
    this.activeCategory = cat;
    this.isCategoryDropdownOpen = false;
    this.page = 0;
    this.loadProducts();
  }

  filterCategoryDropdown(): void {
    const q = this.categorySearchQuery.toLowerCase().trim();
    if (!q) {
      this.filteredCategories = [...this.categories];
    } else {
      this.filteredCategories = this.categories.filter(c => 
        c.toLowerCase().includes(q)
      );
    }
    this.cdr.markForCheck();
  }

  toggleCategoryDropdown(event?: Event): void {
    if (event) event.stopPropagation();
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
    if (this.isCategoryDropdownOpen) {
      this.categorySearchQuery = '';
      this.filteredCategories = [...this.categories];
    }
    this.cdr.markForCheck();
  }

  goToPage(p: number): void {
    if (p < 0 || p >= this.totalPages || p === this.page) return;
    this.page = p;
    this.loadProducts();
    this.scrollToTop();
  }

  nextPage(): void {
    if (this.page < this.totalPages - 1) {
      this.goToPage(this.page + 1);
    }
  }

  prevPage(): void {
    if (this.page > 0) {
      this.goToPage(this.page - 1);
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

  private scrollToTop(): void {
    const scrollWrapper = this.elementRef.nativeElement.querySelector('.catalog-scroll-wrapper');
    if (scrollWrapper) {
      scrollWrapper.scrollTop = 0;
    }
    const section = this.elementRef.nativeElement.querySelector('#catalog');
    if (section) {
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.category-dropdown-wrapper')) {
      if (this.isCategoryDropdownOpen) {
        this.isCategoryDropdownOpen = false;
        this.cdr.markForCheck();
      }
    }
  }

  @HostListener('keydown.escape')
  onEscapeKey(): void {
    if (this.isCategoryDropdownOpen) {
      this.isCategoryDropdownOpen = false;
      this.cdr.markForCheck();
    }
  }

  getProductSlug(produto: Product, index: number): string {
    if (!produto.nome) return `item-${index + 1}`;
    const clean = produto.nome.toLowerCase().trim().normalize('NFD').replace(/[\u0300-\u036f]/g, '').replace(/[^a-z0-9]+/g, '-').replace(/^-+|-+$/g, '');
    return clean || `item-${index + 1}`;
  }
}
