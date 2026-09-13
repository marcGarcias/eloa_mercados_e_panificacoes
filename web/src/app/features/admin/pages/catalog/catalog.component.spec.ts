import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CatalogComponent } from './catalog.component';
import { ProductService } from '../../../../services/product.service';
import { CategoryAdminService } from '../../../../services/category-admin.service';
import { ToastService } from '../../../../services/toast.service';
import { of, throwError } from 'rxjs';
import { ProductStatus } from '../../../../models/product.model';
import { CdkDragDrop } from '@angular/cdk/drag-drop';

describe('CatalogComponent (Admin)', () => {
  let component: CatalogComponent;
  let fixture: ComponentFixture<CatalogComponent>;
  let mockProductService: any;
  let mockCategoryAdminService: any;
  let mockToastService: any;

  const mockAdminCats = [
    { id: 1, name: 'Pães' },
    { id: 2, name: 'Doces' }
  ];

  const mockProductResponse = {
    content: [
      { id: 10, name: 'Pão Francês', categoryName: 'Pães', weight: 0.05, status: ProductStatus.ACTIVE, photo: 'pao.webp', position: 0 },
      { id: 20, name: 'Bolo de Rolo', categoryName: 'Doces', weight: 1.2, status: ProductStatus.INACTIVE, photo: 'bolo.webp', position: 1 }
    ],
    totalElements: 2,
    totalPages: 1
  };

  const mockPagedCatResponse = {
    content: mockAdminCats,
    totalElements: 2,
    totalPages: 1
  };

  beforeEach(async () => {
    vi.useFakeTimers();

    mockProductService = {
      searchAdmin: vi.fn().mockReturnValue(of(mockProductResponse)),
      getProductImageUrl: vi.fn().mockImplementation((url: string | null) => url || '/placeholder.webp'),
      deleteProducts: vi.fn().mockReturnValue(of(null)),
      updateOrder: vi.fn().mockReturnValue(of(null))
    };

    mockCategoryAdminService = {
      getAll: vi.fn().mockReturnValue(of(mockAdminCats)),
      search: vi.fn().mockReturnValue(of(mockPagedCatResponse)),
      delete: vi.fn().mockReturnValue(of(null)),
      deleteCategories: vi.fn().mockReturnValue(of(null))
    };

    mockToastService = {
      success: vi.fn(),
      error: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [CatalogComponent],
      providers: [
        { provide: ProductService, useValue: mockProductService },
        { provide: CategoryAdminService, useValue: mockCategoryAdminService },
        { provide: ToastService, useValue: mockToastService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CatalogComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    vi.useRealTimers();
  });

  it('deve inicializar e carregar produtos e categorias no ngOnInit', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(mockProductService.searchAdmin).toHaveBeenCalled();
    expect(mockCategoryAdminService.getAll).toHaveBeenCalled();
    expect(mockCategoryAdminService.search).toHaveBeenCalled();
    expect(component.products.length).toBe(2);
    expect(component.products[0].nome).toBe('Pão Francês');
    expect(component.products[0].peso).toBe('50g');
    expect(component.products[1].peso).toBe('1.2kg');
  });

  it('deve alternar entre abas de produtos e categorias', () => {
    component.setActiveTab('categories');
    expect(component.activeTab).toBe('categories');

    component.setActiveTab('products');
    expect(component.activeTab).toBe('products');
  });

  it('deve filtrar produtos por busca com debounce', () => {
    fixture.detectChanges();
    component.searchTerm = 'Bolo';
    component.onSearchChange();

    vi.advanceTimersByTime(400);

    expect(mockProductService.searchAdmin).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Bolo'
    }));
  });

  it('deve filtrar categorias por busca com debounce', () => {
    fixture.detectChanges();
    component.categorySearchTerm = 'Doc';
    component.onCategorySearchChange();

    vi.advanceTimersByTime(350);

    expect(mockCategoryAdminService.search).toHaveBeenCalledWith(0, 10, 'Doc');
  });

  it('deve paginar produtos corretamente', () => {
    component.totalPages = 3;
    component.page = 1;

    component.nextPage();
    expect(component.page).toBe(2);

    component.prevPage();
    expect(component.page).toBe(1);

    component.goToPage(0);
    expect(component.page).toBe(0);

    const pageNumbers = component.getPageNumbers(1, 3);
    expect(pageNumbers).toEqual([0, 1, 2]);
  });

  it('deve paginar categorias corretamente', () => {
    component.categoryTotalPages = 3;
    component.categoryPage = 1;

    component.nextCategoryPage();
    expect(component.categoryPage).toBe(2);

    component.prevCategoryPage();
    expect(component.categoryPage).toBe(1);

    component.goToCategoryPage(0);
    expect(component.categoryPage).toBe(0);
  });

  it('deve gerenciar dropdown de filtros de categoria', () => {
    fixture.detectChanges();
    expect(component.isCategoryDropdownOpen).toBeFalsy();

    component.toggleCategoryDropdown();
    expect(component.isCategoryDropdownOpen).toBeTruthy();

    component.categoryFilterSearch = 'Pã';
    expect(component.filteredAdminCategoriesForDropdown.length).toBe(1);
    expect(component.filteredAdminCategoriesForDropdown[0].name).toBe('Pães');

    component.setFilter('Pães');
    expect(component.activeFilter).toBe('Pães');
    expect(component.isCategoryDropdownOpen).toBeFalsy();

    // Fechar ao clicar fora
    component.isCategoryDropdownOpen = true;
    const dummyEvent = { target: document.createElement('div') } as any;
    component.onDocumentClick(dummyEvent);
    expect(component.isCategoryDropdownOpen).toBeFalsy();
  });

  it('deve abrir e fechar modais de criação e edição de produtos', () => {
    fixture.detectChanges();
    component.openCreateModal();
    expect(component.isProductModalOpen).toBeTruthy();
    expect(component.editingProduct).toBeNull();

    component.openEditModal(component.products[0]);
    expect(component.editingProduct?.name).toBe('Pão Francês');

    component.onProductSaved({ id: 10, name: 'Pão Francês', categoryName: 'Pães', weight: 0.05, photo: '', position: 0, status: ProductStatus.ACTIVE });
    expect(mockToastService.success).toHaveBeenCalledWith(expect.stringContaining('Pão Francês'), 'Produto Salvo');
    expect(component.isProductModalOpen).toBeFalsy();

    component.openCreateModal();
    component.onProductModalClosed();
    expect(component.isProductModalOpen).toBeFalsy();
  });

  it('deve abrir e fechar modais de categoria e salvar nova categoria', () => {
    component.openCreateCategoryModal();
    expect(component.isCategoryModalOpen).toBeTruthy();

    const newCat = { id: 3, name: 'Salgados' };
    component.onCategorySaved(newCat);
    expect(mockToastService.success).toHaveBeenCalledWith(expect.stringContaining('Salgados'), 'Categoria Criada');
    expect(component.isCategoryModalOpen).toBeFalsy();

    component.openCreateCategoryModal();
    component.onCategoryModalClosed();
    expect(component.isCategoryModalOpen).toBeFalsy();
  });

  it('deve excluir categoria diretamente com confirmação positiva', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);

    component.deleteCategoryDirectly(mockAdminCats[0]);

    expect(mockCategoryAdminService.delete).toHaveBeenCalledWith(1);
    expect(mockToastService.success).toHaveBeenCalledWith(expect.stringContaining('Pães'), 'Categoria Excluída');
  });

  it('não deve excluir categoria se o usuário cancelar o confirm', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(false);

    component.deleteCategoryDirectly(mockAdminCats[0]);

    expect(mockCategoryAdminService.delete).not.toHaveBeenCalled();
  });

  it('deve alternar o modo de edição e gerenciar alterações em lote', () => {
    fixture.detectChanges();
    expect(component.isEditMode).toBeFalsy();

    component.toggleEditMode();
    expect(component.isEditMode).toBeTruthy();

    component.markProductForDeletion(10);
    expect(component.deletedProductIds.has(10)).toBeTruthy();
    expect(component.hasChanges).toBeTruthy();

    // Tentar excluir categoria que possui produto vinculado (Bolo de Rolo na categoria 'Doces')
    vi.spyOn(window, 'alert').mockImplementation(() => {});
    const stopPropagationMock = { stopPropagation: vi.fn() } as any;
    component.markCategoryForDeletion('Doces', stopPropagationMock);
    expect(component.deletedCategoryNames.has('Doces')).toBeFalsy();

    // Excluir categoria sem produtos vinculados
    component.markCategoryForDeletion('Vazia', stopPropagationMock);
    expect(component.deletedCategoryNames.has('Vazia')).toBeTruthy();

    // Reordenação via botões
    component.moveProductDown(0, stopPropagationMock);
    expect(component.hasOrderChanges).toBeTruthy();

    component.moveProductUp(1, stopPropagationMock);

    // Salvar alterações
    component.saveChanges();
    expect(mockProductService.deleteProducts).toHaveBeenCalledWith([10]);
    expect(mockProductService.updateOrder).toHaveBeenCalled();
    expect(mockToastService.success).toHaveBeenCalledWith(expect.any(String), 'Catálogo Atualizado');
  });

  it('deve reordenar itens via onDrop quando em modo de edição', () => {
    fixture.detectChanges();
    component.isEditMode = true;
    component.activeFilter = 'Todos';

    const dropEvent = {
      previousIndex: 0,
      currentIndex: 1
    } as any;

    component.onDrop(dropEvent);

    expect(component.hasOrderChanges).toBe(true);
    expect(component.products[0].nome).toBe('Bolo de Rolo');
    expect(component.products[1].nome).toBe('Pão Francês');
  });

  it('deve tratar erro ao salvar alterações do catálogo', () => {
    fixture.detectChanges();
    component.isEditMode = true;
    component.markProductForDeletion(10);
    mockProductService.deleteProducts.mockReturnValue(throwError(() => ({ error: { message: 'Erro ao deletar' } })));

    component.saveChanges();

    expect(mockToastService.error).toHaveBeenCalledWith('Erro ao deletar', 'Erro ao Salvar');
  });

  it('deve tratar erro na exclusão direta de categoria', () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockCategoryAdminService.delete.mockReturnValue(throwError(() => ({ error: { message: 'Categoria vinculada' } })));

    component.deleteCategoryDirectly(mockAdminCats[0]);

    expect(mockToastService.error).toHaveBeenCalledWith('Categoria vinculada', 'Erro ao Excluir');
  });

  it('deve reagir ao redimensionamento de tela para mobile', () => {
    component.onResize();
    expect(component.isMobile).toBeDefined();
  });

  it('deve desinscrever no ngOnDestroy', () => {
    fixture.detectChanges();
    const unsubSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });
});
