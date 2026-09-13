import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeroComponent } from './hero.component';
import { ContentBanner } from '../../models/content.model';

describe('HeroComponent', () => {
  let component: HeroComponent;
  let fixture: ComponentFixture<HeroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeroComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HeroComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve receber e renderizar o banner', () => {
    const banner: ContentBanner = {
      selo: 'Qualidade Garantida',
      titulo: 'Tradição e Sabor',
      subtitulo: 'Produtos frescos diariamente',
      descricao: 'Descrição do banner',
      indicadores: [{ nome: 'Anos de história', valor: '30+' }]
    };

    fixture.componentRef.setInput('banner', banner);
    fixture.detectChanges();

    expect(component.banner?.titulo).toBe('Tradição e Sabor');
  });
});
