import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AboutComponent } from './about.component';
import { ContentSobre } from '../../models/content.model';

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AboutComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve filtrar itens vazios em validList e calcular hasValidItems', () => {
    component.sobre = null;
    expect(component.validList).toEqual([]);
    expect(component.hasValidItems).toBeFalsy();

    const sobre: ContentSobre = {
      selo: 'Nossa história',
      titulo: 'Sobre nós',
      descricao: 'Desde 1990',
      lista: [
        { nome: 'História', descricao: 'Tradição na família' },
        { nome: '   ', descricao: '' },
        { nome: '', descricao: 'Só descrição' }
      ]
    };

    component.sobre = sobre;
    expect(component.validList.length).toBe(2);
    expect(component.hasValidItems).toBeTruthy();
  });
});
