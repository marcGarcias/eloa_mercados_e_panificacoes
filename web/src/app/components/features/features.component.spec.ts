import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FeaturesComponent } from './features.component';
import { ContentDiferenciais } from '../../models/content.model';

describe('FeaturesComponent', () => {
  let component: FeaturesComponent;
  let fixture: ComponentFixture<FeaturesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FeaturesComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FeaturesComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve receber e exibir diferenciais', () => {
    const diferenciais: ContentDiferenciais = {
      selo: 'Nossos diferenciais',
      titulo: 'Por que escolher a Eloá?',
      descricao: 'Qualidade superior',
      cards: [{ titulo: 'Fornos modernos', texto: 'Tecnologia de ponta' }]
    };

    fixture.componentRef.setInput('diferenciais', diferenciais);
    fixture.detectChanges();

    expect(component.diferenciais?.titulo).toBe('Por que escolher a Eloá?');
    expect(component.diferenciais?.cards.length).toBe(1);
  });
});
