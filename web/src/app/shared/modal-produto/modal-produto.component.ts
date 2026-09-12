import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  OnDestroy,
  SimpleChanges,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  HostListener,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl } from '@angular/forms';
import {
  ProductAdminResponse,
  CategoryAdminResponse,
  ProductStatus,
  CreateProductPayload,
  UpdateProductPayload,
} from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { CategoryAdminService } from '../../services/category-admin.service';
import { ToastService } from '../../services/toast.service';
import { finalize, Subscription } from 'rxjs';

/**
 * Modal dual-mode de produto.
 *
 * Modo CRIACAO: @Input product = null
 *   - Titulo: "Novo produto"
 *   - Campos: name, categoryId, weight, photo (WebP obrigatoria)
 *   - Submit: valida integridade e existência das categorias na API antes de criar
 *
 * Modo EDICAO: @Input product = ProductAdminResponse
 *   - Titulo: "Editar produto"
 *   - Campos: todos do modo criacao + status + position
 *   - Formulario pre-preenchido com dados do produto
 *   - Submit: monta UpdateProductPayload (somente campos alterados)
 *
 * Emite:
 *   - (saved): ProductAdminResponse apos salvar com sucesso
 *   - (closed): ao fechar sem salvar
 */
@Component({
  selector: 'app-modal-produto',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modal-produto.component.html',
  styleUrls: ['./modal-produto.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModalProdutoComponent implements OnChanges, OnDestroy {
  private readonly subs = new Subscription();

  /** Produto a editar. null = modo criacao */
  @Input() product: ProductAdminResponse | null = null;

  /** Lista de categorias para o select */
  @Input() categories: CategoryAdminResponse[] = [];

  /** Controla visibilidade do modal */
  @Input() isOpen: boolean = false;

  /** Emitido apos salvar com sucesso */
  @Output() saved = new EventEmitter<ProductAdminResponse>();

  /** Emitido ao fechar o modal */
  @Output() closed = new EventEmitter<void>();

  /** Referencia ao arquivo de foto selecionado */
  selectedPhoto: File | null = null;

  /** URL de preview da imagem (local ou da API) */
  photoPreviewUrl: string | null = null;

  /** Mensagem de erro de validacao da imagem */
  photoError: string | null = null;

  /** Estado de submissao */
  isSubmitting: boolean = false;

  /** Enum de status disponivel no template */
  readonly ProductStatus = ProductStatus;

  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly productService: ProductService,
    private readonly categoryAdminService: CategoryAdminService,
    private readonly toastService: ToastService,
    private readonly cdr: ChangeDetectorRef,
  ) {
    this.form = this.buildForm();
  }

  get isEditMode(): boolean {
    return this.product !== null;
  }

  get modalTitle(): string {
    return this.isEditMode ? 'Editar produto' : 'Novo produto';
  }

  get submitLabel(): string {
    return this.isEditMode ? 'Salvar alteracoes' : 'Criar produto';
  }

  @HostListener('keydown.escape')
  onEscape(): void {
    if (this.isOpen) {
      this.close();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.resetModal();
    }
    if (changes['product'] && this.isOpen) {
      this.resetModal();
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  onPhotoChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.photoError = null;
    this.selectedPhoto = null;
    this.photoPreviewUrl = null;

    if (!file) return;

    // Validacao de formato WebP
    const isWebp =
      file.type === 'image/webp' ||
      file.name.toLowerCase().endsWith('.webp');

    if (!isWebp) {
      this.photoError = 'A imagem deve estar no formato WebP.';
      this.cdr.markForCheck();
      return;
    }

    const MAX_SIZE_MB = 10;
    const MAX_SIZE_BYTES = MAX_SIZE_MB * 1024 * 1024;
    if (file.size > MAX_SIZE_BYTES) {
      this.photoError = `A imagem não pode ultrapassar ${MAX_SIZE_MB}MB. Tamanho selecionado: ${(file.size / (1024 * 1024)).toFixed(2)}MB.`;
      this.cdr.markForCheck();
      return;
    }

    this.selectedPhoto = file;
    const reader = new FileReader();
    reader.onload = (e) => {
      this.photoPreviewUrl = e.target?.result as string;
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  clearPhoto(): void {
    this.selectedPhoto = null;
    this.photoPreviewUrl = this.isEditMode ? (this.product?.photo ?? null) : null;
    this.photoError = null;
    this.cdr.markForCheck();
  }

  close(): void {
    this.closed.emit();
  }

  onOverlayClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.close();
    }
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.cdr.markForCheck();
      return;
    }
    if (!this.isEditMode && !this.selectedPhoto) {
      this.photoError = 'A foto do produto e obrigatoria.';
      this.cdr.markForCheck();
      return;
    }
    if (this.photoError) return;

    const formValue = this.form.getRawValue();
    this.isSubmitting = true;
    this.cdr.markForCheck();

    this.subs.add(
      this.categoryAdminService.getAll().subscribe({
        next: (freshCategories) => {
          this.categories = freshCategories;
          const targetCategoryId = Number(formValue.categoryId);
          const categoryExists = freshCategories.some(cat => cat.id === targetCategoryId);

          if (!categoryExists) {
            this.isSubmitting = false;
            this.toastService.error('A categoria selecionada não foi encontrada ou foi removida. Selecione uma categoria válida.', 'Categoria não encontrada');
            this.form.get('categoryId')?.setErrors({ notFound: true });
            this.cdr.markForCheck();
            return;
          }

          if (this.isEditMode && this.product) {
            const payload: UpdateProductPayload = {};
            if (formValue.name)       payload.name       = formValue.name;
            if (formValue.weight)     payload.weight     = Number(formValue.weight);
            if (formValue.categoryId) payload.categoryId = targetCategoryId;
            if (formValue.status)     payload.status     = formValue.status;
            if (this.selectedPhoto)   payload.photo      = this.selectedPhoto;

            this.subs.add(
              this.productService.update(this.product.id, payload).pipe(
                finalize(() => {
                  this.isSubmitting = false;
                  this.cdr.markForCheck();
                })
              ).subscribe({
                next: (updated) => { 
                  this.saved.emit(updated);
                  this.cdr.markForCheck();
                },
                error: (err) => {
                  console.error('[MODAL] Erro ao atualizar produto:', err);
                  this.toastService.error(err.error?.message || 'Erro ao atualizar produto.', 'Erro');
                  this.cdr.markForCheck();
                }
              })
            );

          } else {
            const payload: CreateProductPayload = {
              name:       formValue.name,
              weight:     Number(formValue.weight),
              categoryId: targetCategoryId,
              photo:      this.selectedPhoto!,
            };

            this.subs.add(
              this.productService.create(payload).pipe(
                finalize(() => {
                  this.isSubmitting = false;
                  this.cdr.markForCheck();
                })
              ).subscribe({
                next: (created) => { 
                  this.saved.emit(created);
                  this.cdr.markForCheck();
                },
                error: (err) => {
                  console.error('[MODAL] Erro ao criar produto:', err);
                  this.toastService.error(err.error?.message || 'Erro ao criar produto.', 'Erro');
                  this.cdr.markForCheck();
                }
              })
            );
          }
        },
        error: (err) => {
          this.isSubmitting = false;
          console.error('[MODAL] Erro ao verificar categorias na API:', err);
          this.toastService.error('Não foi possível verificar as categorias na API. Tente novamente.', 'Falha de Verificação');
          this.cdr.markForCheck();
        }
      })
    );
  }

  isFieldInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl && ctrl.invalid && ctrl.touched);
  }

  getFieldError(field: string): string {
    const ctrl = this.form.get(field);
    if (!ctrl || !ctrl.errors) return '';
    if (ctrl.errors['required'])  return 'Campo obrigatorio.';
    if (ctrl.errors['notFound'])  return 'Categoria inexistente. Selecione uma da lista.';
    if (ctrl.errors['min'])       return `Valor minimo: ${ctrl.errors['min'].min}.`;
    if (ctrl.errors['minlength']) return `Minimo de ${ctrl.errors['minlength'].requiredLength} caracteres.`;
    return 'Valor invalido.';
  }

  private buildForm(): FormGroup {
    return this.fb.group({
      name:       ['', [Validators.required, Validators.minLength(2)]],
      categoryId: [null, [Validators.required, (ctrl: AbstractControl) => (Number(ctrl.value) > 0 ? null : { required: true })]],
      weight:     ['', [Validators.required, Validators.min(0.001)]],
      // Campos exclusivos do modo edicao
      status:   [ProductStatus.ACTIVE],
    });
  }

  private resetModal(): void {
    this.selectedPhoto   = null;
    this.photoPreviewUrl = null;
    this.photoError      = null;
    this.isSubmitting    = false;

    if (this.isEditMode && this.product) {
      this.form.patchValue({
        name:       this.product.name,
        categoryId: this.findCategoryId(this.product.categoryName),
        weight:     this.product.weight,
        status:     this.product.status,
      });
      this.photoPreviewUrl = this.product.photo || null;
    } else {
      this.form.reset({
        name: '', categoryId: null, weight: '', status: ProductStatus.ACTIVE
      });
    }

    this.form.markAsUntouched();
    this.cdr.markForCheck();
  }

  private findCategoryId(categoryName: string): number | string {
    const found = this.categories.find(c => c.name === categoryName);
    return found ? found.id : '';
  }
}
