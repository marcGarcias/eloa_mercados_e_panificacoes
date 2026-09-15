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
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategoryAdminService } from '../../services/category-admin.service';
import { CategoryAdminResponse } from '../../models/product.model';
import { finalize, Subscription } from 'rxjs';

@Component({
  selector: 'app-modal-categoria',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './modal-categoria.component.html',
  styleUrls: ['./modal-categoria.component.css'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ModalCategoriaComponent implements OnChanges, OnDestroy {
  private readonly subs = new Subscription();

  @Input() isOpen: boolean = false;

  @Input() category: CategoryAdminResponse | null = null;

  @Output() saved = new EventEmitter<CategoryAdminResponse>();

  @Output() closed = new EventEmitter<void>();

  isSubmitting: boolean = false;

  form: FormGroup;

  constructor(
    private readonly fb: FormBuilder,
    private readonly categoryAdminService: CategoryAdminService,
    private readonly cdr: ChangeDetectorRef,
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(16)]],
    });
  }

  get isEditMode(): boolean {
    return this.category !== null;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['isOpen'] && this.isOpen) {
      this.reset();
    } else if (changes['category'] && this.isOpen) {
      this.reset();
    }
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  @HostListener('keydown.escape')
  onEscape(): void {
    if (this.isOpen) {
      this.close();
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
      this.cdr.markForCheck();
      return;
    }

    const name: string = this.form.value.name.trim();
    this.isSubmitting = true;
    this.cdr.markForCheck();

    const request$ = this.isEditMode && this.category
      ? this.categoryAdminService.update(this.category.id, name)
      : this.categoryAdminService.create(name);

    this.subs.add(
      request$.pipe(
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.markForCheck();
        })
      ).subscribe({
        next: (savedCat) => {
          this.saved.emit(savedCat);
          this.cdr.markForCheck();
        },
        error: (err) => {
          console.error('[ModalCategoriaComponent] Erro ao salvar categoria:', err);
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
    if (ctrl.errors['minlength']) return `Minimo de ${ctrl.errors['minlength'].requiredLength} caracteres.`;
    if (ctrl.errors['maxlength']) return `Maximo de ${ctrl.errors['maxlength'].requiredLength} caracteres.`;
    return 'Valor invalido.';
  }

  private reset(): void {
    this.isSubmitting = false;
    this.form.reset({ name: this.category?.name ?? '' });
    this.form.markAsUntouched();
    this.cdr.markForCheck();
  }
}

