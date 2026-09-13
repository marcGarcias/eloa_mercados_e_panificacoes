import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModalComponent } from './modal.component';

describe('ModalComponent', () => {
  let component: ModalComponent;
  let fixture: ComponentFixture<ModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModalComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve emitir evento de close ao chamar closeModal()', () => {
    const closeSpy = vi.spyOn(component.close, 'emit');
    component.closeModal();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('deve fechar ao pressionar Escape quando isOpen for true', () => {
    component.isOpen = true;
    const closeSpy = vi.spyOn(component, 'closeModal');

    component.onEscape();
    expect(closeSpy).toHaveBeenCalled();
  });

  it('não deve fechar ao pressionar Escape quando isOpen for false', () => {
    component.isOpen = false;
    const closeSpy = vi.spyOn(component, 'closeModal');

    component.onEscape();
    expect(closeSpy).not.toHaveBeenCalled();
  });
});
