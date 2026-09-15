import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ModalProdutoComponent } from './modal-produto.component';
import { ProductService } from '../../services/product.service';
import { CategoryAdminService } from '../../services/category-admin.service';
import { ToastService } from '../../services/toast.service';
import { ProductAdminResponse, ProductStatus } from '../../models/product.model';
import { of, throwError } from 'rxjs';
import { SimpleChange } from '@angular/core';

describe('ModalProdutoComponent', () => {
  let component: ModalProdutoComponent;
  let fixture: ComponentFixture<ModalProdutoComponent>;
  let productServiceMock: any;
  let categoryAdminServiceMock: any;
  let toastServiceMock: any;

  const sampleCategories = [
    { id: 1, name: 'Padaria' },
    { id: 2, name: 'Confeitaria' }
  ];

  const sampleProduct: ProductAdminResponse = {
    id: 10,
    name: 'Pão de Batata',
    weight: 120,
    position: 1,
    photo: 'http://imagem.webp',
    categoryName: 'Padaria',
    status: ProductStatus.ACTIVE
  };

  beforeEach(async () => {
    productServiceMock = {
      create: vi.fn(),
      update: vi.fn()
    };
    categoryAdminServiceMock = {
      getAll: vi.fn().mockReturnValue(of(sampleCategories)),
      categoriesUpdated$: of(void 0)
    };
    toastServiceMock = {
      success: vi.fn(),
      error: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ModalProdutoComponent, ReactiveFormsModule],
      providers: [
        { provide: ProductService, useValue: productServiceMock },
        { provide: CategoryAdminService, useValue: categoryAdminServiceMock },
        { provide: ToastService, useValue: toastServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ModalProdutoComponent);
    component = fixture.componentInstance;
    component.categories = sampleCategories;
    fixture.detectChanges();
  });

  it('deve inicializar em modo de criação quando product for null', () => {
    expect(component.isEditMode).toBeFalsy();
    expect(component.modalTitle).toBe('Novo produto');
    expect(component.submitLabel).toBe('Criar produto');
  });

  it('deve alternar para modo de edição quando product for fornecido', () => {
    component.product = sampleProduct;
    component.isOpen = true;
    component.ngOnChanges({
      product: new SimpleChange(null, sampleProduct, false)
    });

    expect(component.isEditMode).toBeTruthy();
    expect(component.modalTitle).toBe('Editar produto');
    expect(component.submitLabel).toBe('Salvar alteracoes');
    expect(component.form.get('name')?.value).toBe('Pão de Batata');
  });

  it('deve validar formato e tamanho de arquivo de foto suportando WebP, PNG e JPEG', async () => {
    // Arquivo não suportado (ex: PDF ou GIF)
    const invalidFile = new File(['conteudo'], 'documento.pdf', { type: 'application/pdf' });
    await component.onPhotoChange({ target: { files: [invalidFile] } } as any);
    expect(component.photoError).toContain('Formato não suportado');
    expect(component.selectedPhoto).toBeNull();

    // Arquivo muito grande (> 10MB)
    const largeFile = new File([new Uint8Array(11 * 1024 * 1024)], 'grande.png', { type: 'image/png' });
    await component.onPhotoChange({ target: { files: [largeFile] } } as any);
    expect(component.photoError).toContain('10MB');

    // Arquivo válido WebP
    const validFile = new File(['bytes'], 'valida.webp', { type: 'image/webp' });
    await component.onPhotoChange({ target: { files: [validFile] } } as any);
    expect(component.photoError).toBeNull();
    expect(component.selectedPhoto).toBeDefined();

    // Limpar foto
    component.clearPhoto();
    expect(component.selectedPhoto).toBeNull();
    expect(component.photoPreviewUrl).toBeNull();
  });

  it('não deve submeter criação se foto não for fornecida', () => {
    component.form.patchValue({ name: 'Bolo', weight: 500, categoryId: 1 });
    component.onSubmit();

    expect(component.photoError).toBe('A foto do produto e obrigatoria.');
    expect(productServiceMock.create).not.toHaveBeenCalled();
  });

  it('deve exibir toast de erro caso a categoria selecionada não exista mais', () => {
    component.form.patchValue({ name: 'Bolo', weight: 500, categoryId: 999 });
    component.selectedPhoto = new File(['bytes'], 'valida.webp', { type: 'image/webp' });

    component.onSubmit();

    expect(toastServiceMock.error).toHaveBeenCalledWith(
      expect.stringContaining('não foi encontrada'),
      expect.any(String)
    );
    expect(component.isSubmitting).toBeFalsy();
  });

  it('deve criar produto com sucesso no modo criação', () => {
    const savedSpy = vi.spyOn(component.saved, 'emit');
    component.form.patchValue({ name: 'Pão de Batata', weight: '0120', categoryId: 1 });
    component.selectedPhoto = new File(['bytes'], 'pao.webp', { type: 'image/webp' });

    productServiceMock.create.mockReturnValue(of(sampleProduct));

    component.onSubmit();

    expect(productServiceMock.create).toHaveBeenCalledWith(expect.objectContaining({
      name: 'Pão de Batata',
      weight: 0.12,
      categoryId: 1
    }));
    expect(savedSpy).toHaveBeenCalledWith(sampleProduct);
    expect(component.isSubmitting).toBeFalsy();
  });

  it('deve aceitar peso com 0 na frente com ponto (0.120), inteiro (1) e decimal (3.250)', () => {
    component.selectedPhoto = new File(['bytes'], 'pao.webp', { type: 'image/webp' });
    productServiceMock.create.mockReturnValue(of(sampleProduct));

    // Teste 0.120
    component.form.patchValue({ name: 'Pão', weight: '0.120', categoryId: 1 });
    component.onSubmit();
    expect(productServiceMock.create).toHaveBeenCalledWith(expect.objectContaining({ weight: 0.12 }));

    // Teste 1
    component.form.patchValue({ name: 'Bolo', weight: '1', categoryId: 1 });
    component.onSubmit();
    expect(productServiceMock.create).toHaveBeenCalledWith(expect.objectContaining({ weight: 1 }));

    // Teste 3.250
    component.form.patchValue({ name: 'Torta', weight: '3.250', categoryId: 1 });
    component.onSubmit();
    expect(productServiceMock.create).toHaveBeenCalledWith(expect.objectContaining({ weight: 3.25 }));
  });

  it('deve atualizar produto com sucesso no modo edição', () => {
    component.product = sampleProduct;
    component.form.patchValue({ name: 'Pão Especial', weight: 200, categoryId: 1 });
    const savedSpy = vi.spyOn(component.saved, 'emit');

    const updatedProduct = { ...sampleProduct, name: 'Pão Especial' };
    productServiceMock.update.mockReturnValue(of(updatedProduct));

    component.onSubmit();

    expect(productServiceMock.update).toHaveBeenCalledWith(10, expect.any(Object));
    expect(savedSpy).toHaveBeenCalledWith(updatedProduct);
  });

  it('deve tratar erro na atualização e exibir toast de erro', () => {
    component.product = sampleProduct;
    component.form.patchValue({ name: 'Pão de Batata', weight: 200, categoryId: 1 });
    productServiceMock.update.mockReturnValue(throwError(() => ({ error: { message: 'Erro no servidor' } })));

    component.onSubmit();

    expect(toastServiceMock.error).toHaveBeenCalledWith('Erro no servidor', 'Erro');
    expect(component.isSubmitting).toBeFalsy();
  });

  it('deve fechar ao clicar no overlay ou pressionar Escape', () => {
    const closedSpy = vi.spyOn(component.closed, 'emit');

    component.close();
    expect(closedSpy).toHaveBeenCalledTimes(1);

    component.isOpen = true;
    component.onEscape();
    expect(closedSpy).toHaveBeenCalledTimes(2);

    const overlayEvent = { target: { classList: { contains: (cls: string) => cls === 'modal-overlay' } } } as any;
    component.onOverlayClick(overlayEvent);
    expect(closedSpy).toHaveBeenCalledTimes(3);
  });

  it('deve recarregar categorias quando o modal é aberto via ngOnChanges', () => {
    const newCats = [
      { id: 1, name: 'Padaria' },
      { id: 2, name: 'Confeitaria' },
      { id: 3, name: 'Salgados' }
    ];
    categoryAdminServiceMock.getAll.mockReturnValue(of(newCats));

    component.isOpen = true;
    component.ngOnChanges({
      isOpen: new SimpleChange(false, true, true)
    });

    expect(categoryAdminServiceMock.getAll).toHaveBeenCalled();
    expect(component.categories).toEqual(newCats);
  });
});
