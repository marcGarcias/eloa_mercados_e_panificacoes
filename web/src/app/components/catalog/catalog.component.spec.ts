import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { CatalogComponent } from './catalog.component';
import { ProductService } from '../../services/product.service';
import { SeoService } from '../../services/seo.service';
import { Product } from '../../models/product.model';
import { of, throwError } from 'rxjs';

describe('CatalogComponent (Public)', () => {
  let component: CatalogComponent;
  let fixture: ComponentFixture<CatalogComponent>;
  let productServiceMock: any;
  let seoServiceMock: any;

  const sampleProducts: Product[] = [
    { id: 1, nome: 'Pão Francês', categoria: 'Padaria', peso: '500g', imagem: 'pao.webp' },
    { id: 2, nome: 'Bolo de Fubá', categoria: 'Confeitaria', peso: '1kg', imagem: 'bolo.webp' }
  ];

  beforeAll(() => {
    Element.prototype.scrollIntoView = vi.fn();
    if (!globalThis.IntersectionObserver) {
      class MockIntersectionObserver {
        observe = vi.fn();
        unobserve = vi.fn();
        disconnect = vi.fn();
      }
      globalThis.IntersectionObserver = MockIntersectionObserver as any;
    }
  });

  beforeEach(async () => {
    vi.useFakeTimers();
    productServiceMock = {
      getPublicCategories: vi.fn().mockReturnValue(of(['Padaria', 'Confeitaria'])),
      getProductImageUrl: vi.fn().mockImplementation((url: string | null) => url || '/placeholder.webp'),
      searchPublic: vi.fn().mockReturnValue(of({
        content: [
          { name: 'Pão Francês', categoryName: 'Padaria', weight: 0.5, photoUrl: 'pao.webp', position: 1 },
          { name: 'Bolo de Fubá', categoryName: 'Confeitaria', weight: 1.0, photoUrl: 'bolo.webp', position: 2 }
        ],
        totalElements: 2,
        totalPages: 1,
        page: 0,
        size: 12
      }))
    };
    seoServiceMock = {
      updateCatalogStructuredData: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [CatalogComponent, FormsModule],
      providers: [
        { provide: ProductService, useValue: productServiceMock },
        { provide: SeoService, useValue: seoServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CatalogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('deve criar o componente e carregar categorias e produtos iniciais', () => {
    expect(component).toBeTruthy();
    expect(component.categories).toEqual(['Padaria', 'Confeitaria']);
    expect(component.products.length).toBe(2);
    expect(component.products[0].nome).toBe('Pão Francês');
    expect(seoServiceMock.updateCatalogStructuredData).toHaveBeenCalled();
  });

  it('deve selecionar categoria e recarregar produtos na página 0', () => {
    component.selectCategory('Padaria');
    expect(component.activeCategory).toBe('Padaria');
    expect(component.page).toBe(0);
    expect(productServiceMock.searchPublic).toHaveBeenCalledWith(expect.objectContaining({
      categoryName: 'Padaria'
    }));

    component.selectCategory('Todos');
    expect(component.activeCategory).toBe('Todos');
    expect(productServiceMock.searchPublic).toHaveBeenCalledWith(expect.objectContaining({
      categoryName: undefined
    }));
  });

  it('deve reagir à busca textual com debounce', () => {
    component.searchQuery = 'Bolo';
    component.onSearchChange();

    vi.advanceTimersByTime(350); // Debounce time
    expect(productServiceMock.searchPublic).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Bolo'
    }));
  });

  it('deve limpar busca textual com clearSearch()', () => {
    component.searchQuery = 'Teste';
    component.clearSearch();
    expect(component.searchQuery).toBe('');
    expect(component.page).toBe(0);
  });

  it('deve resetar filtros com resetFilters()', () => {
    component.searchQuery = 'Teste';
    component.activeCategory = 'Doces';
    component.isCategoryDropdownOpen = true;

    component.resetFilters();

    expect(component.searchQuery).toBe('');
    expect(component.activeCategory).toBe('Todos');
    expect(component.isCategoryDropdownOpen).toBeFalsy();
    expect(component.page).toBe(0);
  });

  it('deve filtrar categorias no dropdown', () => {
    component.categorySearchQuery = 'Pad';
    component.filterCategoryDropdown();
    expect(component.filteredCategories).toEqual(['Padaria']);

    component.categorySearchQuery = 'Inexistente';
    component.filterCategoryDropdown();
    expect(component.filteredCategories).toEqual([]);
  });

  it('deve alternar e fechar o dropdown de categorias e fechar no Escape', () => {
    component.isCategoryDropdownOpen = false;
    component.toggleCategoryDropdown();
    expect(component.isCategoryDropdownOpen).toBeTruthy();

    component.onEscapeKey();
    expect(component.isCategoryDropdownOpen).toBeFalsy();

    component.toggleCategoryDropdown();
    expect(component.isCategoryDropdownOpen).toBeTruthy();

    // Clicar fora
    const outsideEvent = { target: document.createElement('div') } as any;
    component.onDocumentClick(outsideEvent);
    expect(component.isCategoryDropdownOpen).toBeFalsy();
  });

  it('deve navegar entre páginas', () => {
    productServiceMock.searchPublic.mockReturnValue(of({
      content: sampleProducts,
      totalElements: 36,
      totalPages: 3,
      page: 1,
      size: 12
    }));
    component.totalPages = 3;
    component.page = 1;

    component.nextPage();
    expect(component.page).toBe(2);

    component.prevPage();
    expect(component.page).toBe(1);

    component.goToPage(0);
    expect(component.page).toBe(0);

    // Tentativa inválida de ir além dos limites
    component.goToPage(-1);
    expect(component.page).toBe(0);
  });

  it('deve gerar números de páginas e slug de produtos corretamente', () => {
    const pageNumbers = component.getPageNumbers(2, 5);
    expect(pageNumbers).toEqual([0, 1, 2, 3, 4]);

    const slug = component.getProductSlug({ nome: 'Pão de Queijo & Doce!' } as Product, 0);
    expect(slug).toBe('pao-de-queijo-doce');

    const fallbackSlug = component.getProductSlug({ nome: '' } as Product, 3);
    expect(fallbackSlug).toBe('item-4');
  });

  it('deve tratar erro no carregamento de categorias e produtos', () => {
    productServiceMock.getPublicCategories.mockReturnValue(throwError(() => new Error('Erro')));
    productServiceMock.searchPublic.mockReturnValue(throwError(() => new Error('Erro')));

    component.loadCategories();
    component.loadProducts();

    expect(component.categories).toEqual([]);
    expect(component.products).toEqual([]);
    expect(component.totalElements).toBe(0);
  });
});
