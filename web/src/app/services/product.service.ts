import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { Product } from '../models/product.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly apiUrl = environment.apiUrl;

  private readonly products: Product[] = [
    { id: 1, imagem: '', nome: 'Pão de Sal', categoria: 'Pão de sal', peso: '50g' },
    { id: 2, imagem: '', nome: 'Pão Doce', categoria: 'Pão doce', peso: '60g' },
    { id: 3, imagem: '', nome: 'Rosquinha', categoria: 'Rosquinha', peso: '40g' },
    { id: 4, imagem: '', nome: 'Broa de Milho', categoria: 'Broa', peso: '300g' },
    { id: 5, imagem: '', nome: 'Rosca Doce', categoria: 'Rosca', peso: '400g' },
    { id: 6, imagem: '', nome: 'Língua de Sogra', categoria: 'Língua de sogra', peso: '45g' },
    { id: 7, imagem: '', nome: 'Sonho de Creme', categoria: 'Sonho', peso: '80g' },
    { id: 8, imagem: '', nome: 'Bauru', categoria: 'Bauru', peso: '250g' },
    { id: 9, imagem: '', nome: 'Esfiha de Carne', categoria: 'Esfiha', peso: '70g' },
    { id: 10, imagem: '', nome: 'Hamburgão', categoria: 'Hamburgao', peso: '220g' },
    { id: 11, imagem: '', nome: 'Botões', categoria: 'Botões', peso: '150g' },
  ];

  getAll(): Observable<Product[]> {
    return of(this.products);
  }

  getCategories(): string[] {
    const cats = ['Todos', ...new Set(this.products.map(p => p.categoria))];
    return cats;
  }
}
