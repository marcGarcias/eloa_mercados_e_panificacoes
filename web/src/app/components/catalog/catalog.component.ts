import { Component, signal, computed, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProductService } from '../../services/product.service';
import { Product } from '../../models/product.model';

@Component({
  selector: 'app-catalog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './catalog.component.html',
  styleUrl: './catalog.component.css'
})
export class CatalogComponent implements OnInit {
  private productService = inject(ProductService);

  products = signal<Product[]>([]);
  categories: { id: string, label: string }[] = [];

  activeCategory = signal<string>('Todos');
  filtersOpen = signal<boolean>(false);

  filteredProducts = computed(() => {
    const category = this.activeCategory();
    const allProducts = this.products();
    if (category === 'Todos') return allProducts;
    return allProducts.filter(p => p.categoria === category);
  });

  ngOnInit() {
    this.productService.getAll().subscribe(data => {
      this.products.set(data);
      // Gera as categorias unicas de forma dinamica baseada nos produtos retornados
      const uniqueCategories = Array.from(new Set(data.map(p => p.categoria)));
      this.categories = [
        { id: 'Todos', label: 'Todos' },
        ...uniqueCategories.map(c => ({ id: c, label: c }))
      ];
    });

    this.productService.getPublicCategories().subscribe(cats => {
      this.categories = [
        { id: 'Todos', label: 'Todos' },
        ...cats.map(c => ({ id: c, label: c }))
      ];
    });

    this.productService.getPublicCategories().subscribe(cats => {
      this.categories = [
        { id: 'Todos', label: 'Todos' },
        ...cats.map(c => ({ id: c, label: c }))
      ];
    });
  }

  setCategory(category: string) {
    if (window.innerWidth <= 900) {
      if (this.activeCategory() === category && !this.filtersOpen()) {
        this.filtersOpen.set(true);
        return;
      }
    }
    this.activeCategory.set(category);
    this.filtersOpen.set(false);
  }
}
