import { Component, OnInit, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProductService } from '../../services/product.service';
import { ProductCardComponent } from '../../shared/product-card/product-card';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule, FormsModule, ProductCardComponent],
  templateUrl: './catalog.html',
  styleUrl: './catalog.css'
})
export class CatalogComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  categories: string[] = [];
  activeCategory = 'Todos';
  searchQuery = '';
  filtersOpen = false;

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.productService.getAll().subscribe(p => {
      this.products = p;
      this.filteredProducts = p;
    });
    this.categories = this.productService.getCategories();
  }

  get isMobile() { return window.innerWidth <= 900; }

  selectCategory(cat: string) {
    if (this.isMobile && cat === this.activeCategory && !this.filtersOpen) {
      this.filtersOpen = true;
      return;
    }
    this.activeCategory = cat;
    this.filtersOpen = false;
    this.filter();
  }

  filter() {
    const q = this.searchQuery.toLowerCase();
    this.filteredProducts = this.products.filter(p => {
      const matchCat = this.activeCategory === 'Todos' || p.categoria === this.activeCategory;
      const matchQ = p.nome.toLowerCase().includes(q);
      return matchCat && matchQ;
    });
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(e: MouseEvent) {
    const el = e.target as HTMLElement;
    if (this.isMobile && this.filtersOpen && !el.closest('.filters')) {
      this.filtersOpen = false;
    }
  }
}
