import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ModalCategoriaComponent } from './modal-categoria.component';
import { CategoryAdminService } from '../../services/category-admin.service';
import { of, throwError } from 'rxjs';
import { SimpleChange } from '@angular/core';

describe('ModalCategoriaComponent', () => {
  let component: ModalCategoriaComponent;
  let fixture: ComponentFixture<ModalCategoriaComponent>;
  let categoryAdminServiceMock: any;

  beforeEach(async () => {
    categoryAdminServiceMock = {
      create: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ModalCategoriaComponent, ReactiveFormsModule],
      providers: [
        { provide: CategoryAdminService, useValue: categoryAdminServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ModalCategoriaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
    expect(component.form.valid).toBeFalsy();
  });

  it('deve validar obrigatoriedade e limites de tamanho do campo nome', () => {
    const nameControl = component.form.get('name');

    nameControl?.setValue('');
    expect(nameControl?.valid).toBeFalsy();
    expect(component.getFieldError('name')).toBe('Campo obrigatorio.');

    nameControl?.setValue('A');
    expect(nameControl?.valid).toBeFalsy();
    expect(component.getFieldError('name')).toContain('Minimo de 2 caracteres');

    nameControl?.setValue('A'.repeat(51));
    expect(nameControl?.valid).toBeFalsy();
    expect(component.getFieldError('name')).toContain('Maximo de 50 caracteres');

    nameControl?.setValue('Padaria');
    expect(nameControl?.valid).toBeTruthy();
  });

  it('deve fechar via método close ou tecla Escape', () => {
    const closeSpy = vi.spyOn(component.closed, 'emit');

    component.close();
    expect(closeSpy).toHaveBeenCalled();

    component.isOpen = true;
    component.onEscape();
    expect(closeSpy).toHaveBeenCalledTimes(2);
  });

  it('deve fechar ao clicar no overlay', () => {
    const closeSpy = vi.spyOn(component, 'close');
    const event = { target: { classList: { contains: (cls: string) => cls === 'modal-overlay' } } } as any;

    component.onOverlayClick(event);
    expect(closeSpy).toHaveBeenCalled();

    const innerEvent = { target: { classList: { contains: (cls: string) => false } } } as any;
    component.onOverlayClick(innerEvent);
    expect(closeSpy).toHaveBeenCalledTimes(1);
  });

  it('não deve submeter se o formulário for inválido', () => {
    component.onSubmit();
    expect(categoryAdminServiceMock.create).not.toHaveBeenCalled();
    expect(component.isFieldInvalid('name')).toBeTruthy();
  });

  it('deve criar categoria com sucesso e emitir evento saved', () => {
    const savedSpy = vi.spyOn(component.saved, 'emit');
    const mockCategory = { id: 10, name: 'Confeitaria' };
    categoryAdminServiceMock.create.mockReturnValue(of(mockCategory));

    component.form.get('name')?.setValue('Confeitaria');
    component.onSubmit();

    expect(categoryAdminServiceMock.create).toHaveBeenCalledWith('Confeitaria');
    expect(savedSpy).toHaveBeenCalledWith(mockCategory);
    expect(component.isSubmitting).toBeFalsy();
  });

  it('deve tratar erro na criação de categoria mantendo o formulário', () => {
    categoryAdminServiceMock.create.mockReturnValue(throwError(() => new Error('Falha de rede')));

    component.form.get('name')?.setValue('Confeitaria');
    component.onSubmit();

    expect(categoryAdminServiceMock.create).toHaveBeenCalled();
    expect(component.isSubmitting).toBeFalsy();
  });

  it('deve resetar o formulário quando isOpen mudar para true', () => {
    component.form.get('name')?.setValue('Texto Antigo');
    component.isOpen = true;
    component.ngOnChanges({
      isOpen: new SimpleChange(false, true, true)
    });

    expect(component.form.get('name')?.value).toBe('');
  });
});
