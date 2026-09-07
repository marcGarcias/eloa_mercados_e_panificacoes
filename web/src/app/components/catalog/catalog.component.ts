import { Component, OnInit, Input, HostListener, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';
import { ContentCatalogo } from '../../models/content.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent implements OnInit {
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

  get totalCategoriesCount(): number {
    return this.categories.length;
  }

  constructor(
    private readonly productService: ProductService,
    private readonly elementRef: ElementRef
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadProducts();

    this.searchSubject.pipe(
      debounceTime(350),
      distinctUntilChanged()
    ).subscribe(() => {
      this.page = 0;
      this.loadProducts();
    });
  }

  loadCategories(): void {
    this.productService.getPublicCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        this.filteredCategories = [...cats];
      },
      error: () => {
        this.categories = [];
        this.filteredCategories = [];
      }
    });
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.searchPublic({
      name: this.searchQuery,
      categoryName: this.activeCategory === 'Todos' ? undefined : this.activeCategory,
      page: this.page,
      size: this.size
    }).subscribe({
      next: (pageData) => {
        this.totalElements = pageData.totalElements;
        this.totalPages = pageData.totalPages;
        this.products = pageData.content.map(p => ({
          nome: p.name,
          categoria: p.categoryName,
          peso: p.weight ? `${p.weight.toString().replace('.', ',')} kg` : '',
          imagem: this.productService.getProductImageUrl(p.photoUrl),
          order: Number(p.position)
        }));
        this.isLoading = false;
      },
      error: () => {
        this.products = [];
        this.totalPages = 0;
        this.totalElements = 0;
        this.isLoading = false;
      }
    });
  }

  onSearchChange(): void {
    this.searchSubject.next(this.searchQuery);
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
  }

  toggleCategoryDropdown(event?: Event): void {
    if (event) event.stopPropagation();
    this.isCategoryDropdownOpen = !this.isCategoryDropdownOpen;
    if (this.isCategoryDropdownOpen) {
      this.categorySearchQuery = '';
      this.filteredCategories = [...this.categories];
    }
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
    const section = this.elementRef.nativeElement.querySelector('#catalog');
    if (section) {
      section.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.category-dropdown-wrapper')) {
      this.isCategoryDropdownOpen = false;
    }
  }
}
