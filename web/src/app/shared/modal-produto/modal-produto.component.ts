import {
  Component,
  Input,
  Output,
  EventEmitter,
  OnChanges,
  SimpleChanges,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
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

/**
 * Modal dual-mode de produto.
 *
 * Modo CRIACAO: @Input product = null
 *   - Titulo: "Novo produto"
 *   - Campos: name, categoryId, weight, photo (WebP obrigatoria)
 *   - Submit: monta CreateProductPayload
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
export class ModalProdutoComponent implements OnChanges {

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

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.resetModal();
    }
    if (changes['product'] && this.isOpen) {
      this.resetModal();
    }
  }

  // ----------------------------------------------------------------
  // Controle de foto
  // ----------------------------------------------------------------

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

  // ----------------------------------------------------------------
  // Acoes do modal
  // ----------------------------------------------------------------

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
      return;
    }
    if (!this.isEditMode && !this.selectedPhoto) {
      this.photoError = 'A foto do produto e obrigatoria.';
      return;
    }
    if (this.photoError) return;

    const formValue = this.form.getRawValue();

    if (this.isEditMode && this.product) {
      const payload: UpdateProductPayload = {};
      if (formValue.name)       payload.name       = formValue.name;
      if (formValue.weight)     payload.weight     = Number(formValue.weight);
      if (formValue.categoryId) payload.categoryId = Number(formValue.categoryId);
      if (formValue.status)     payload.status     = formValue.status;
      
      if (this.selectedPhoto)   payload.photo      = this.selectedPhoto;

      // Log do FormData para verificacao (remover na integracao real)
      const fd = this.productService.buildUpdateFormData(payload);
      console.log('[MODAL] PATCH FormData entries:');
      fd.forEach((value, key) => console.log(' ', key, '=', value));

      // TODO: Integrar com ProductService.update(this.product.id, payload)
      this.isSubmitting = true;
      this.productService.update(this.product.id, payload).subscribe({
        next: (updated) => { this.isSubmitting = false; this.saved.emit(updated); },
        error: () => { this.isSubmitting = false; }
      });

    } else {
      const payload: CreateProductPayload = {
        name:       formValue.name,
        weight:     Number(formValue.weight),
        categoryId: Number(formValue.categoryId),
        photo:      this.selectedPhoto!,
      };

      // Log do FormData para verificacao (remover na integracao real)
      const fd = this.productService.buildCreateFormData(payload);
      console.log('[MODAL] POST FormData entries:');
      fd.forEach((value, key) => console.log(' ', key, '=', value));

      // TODO: Integrar com ProductService.create(payload)
      this.isSubmitting = true;
      this.productService.create(payload).subscribe({
        next: (created) => { this.isSubmitting = false; this.saved.emit(created); },
        error: () => { this.isSubmitting = false; }
      });
    }
  }

  // ----------------------------------------------------------------
  // Helpers de template
  // ----------------------------------------------------------------

  isFieldInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl && ctrl.invalid && ctrl.touched);
  }

  getFieldError(field: string): string {
    const ctrl = this.form.get(field);
    if (!ctrl || !ctrl.errors) return '';
    if (ctrl.errors['required'])  return 'Campo obrigatorio.';
    if (ctrl.errors['min'])       return `Valor minimo: ${ctrl.errors['min'].min}.`;
    if (ctrl.errors['minlength']) return `Minimo de ${ctrl.errors['minlength'].requiredLength} caracteres.`;
    return 'Valor invalido.';
  }

  // ----------------------------------------------------------------
  // Internos
  // ----------------------------------------------------------------

  private buildForm(): FormGroup {
    return this.fb.group({
      name:       ['', [Validators.required, Validators.minLength(2)]],
      categoryId: ['', Validators.required],
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
        name: '', categoryId: '', weight: '', status: ProductStatus.ACTIVE
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
