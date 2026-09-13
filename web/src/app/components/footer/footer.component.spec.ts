import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FooterComponent } from './footer.component';
import { ContentRodape, SiteData } from '../../models/content.model';

describe('FooterComponent', () => {
  let component: FooterComponent;
  let fixture: ComponentFixture<FooterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FooterComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FooterComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve gerar whatsappLink corretamente', () => {
    component.dados = null;
    expect(component.whatsappLink).toBe('https://wa.me/');

    component.dados = { whatsapp: '11977776666' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/11977776666');
  });

  it('deve exibir dados do rodapé quando fornecidos', () => {
    const rodape: ContentRodape = {
      descricao: 'Padaria e Confeitaria',
      textoContato: 'Fale conosco',
      textoDireitos: 'Todos os direitos reservados.'
    };
    fixture.componentRef.setInput('rodape', rodape);
    fixture.detectChanges();

    expect(component.rodape?.textoDireitos).toBe('Todos os direitos reservados.');
  });
});
