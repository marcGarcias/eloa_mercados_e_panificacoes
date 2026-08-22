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
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategoryAdminService } from '../../services/category-admin.service';
import { CategoryAdminResponse } from '../../models/product.model';

/**
 * Modal de criacao de categoria.
 *
 * Recebe [isOpen] para controlar visibilidade.
 * Emite:
 *   - (saved): CategoryAdminResponse apos criar com sucesso
 *   - (closed): ao fechar sem salvar
 */
@Component({
  selector: 'app-modal-categoria',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modal-categoria.component.html',
  styleUrls: ['./modal-categoria.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModalCategoriaComponent implements OnChanges {

  /** Controla visibilidade do modal */
  @Input() isOpen: boolean = false;

  /** Emitido apos criar com sucesso */
  @Output() saved = new EventEmitter<CategoryAdminResponse>();

  /** Emitido ao fechar o modal */
  @Output() closed = new EventEmitter<void>();

  /** Estado de submissao */
  isSubmitting: boolean = false;

  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly categoryAdminService: CategoryAdminService,
    private readonly cdr: ChangeDetectorRef,
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.reset();
    }
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
      return;
    }

    const name: string = this.form.value.name.trim();

    // TODO: Integrar com CategoryAdminService.create(name) quando o backend estiver pronto.
    // Simulacao local: cria um objeto de retorno mock com ID temporario.
    const mockCreated: CategoryAdminResponse = { id: Date.now(), name };

    console.log('[ModalCategoriaComponent] Categoria criada:', mockCreated);
    this.saved.emit(mockCreated);
  }

  isFieldInvalid(field: string): boolean {
    const ctrl = this.form.get(field);
    return !!(ctrl && ctrl.invalid && ctrl.touched);
  }

  getFieldError(field: string): string {
    const ctrl = this.form.get(field);
    if (!ctrl || !ctrl.errors) return '';
    if (ctrl.errors['required'])  return 'Campo obrigatorio.';
    if (ctrl.errors['minlength']) return `Minimo de ${ctrl.errors['minlength'].requiredLength} caracteres.`;
    if (ctrl.errors['maxlength']) return `Maximo de ${ctrl.errors['maxlength'].requiredLength} caracteres.`;
    return 'Valor invalido.';
  }

  private reset(): void {
    this.isSubmitting = false;
    this.form.reset({ name: '' });
    this.form.markAsUntouched();
    this.cdr.markForCheck();
  }
}
