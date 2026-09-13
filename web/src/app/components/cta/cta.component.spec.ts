import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CtaComponent } from './cta.component';
import { ContentCta, SiteData } from '../../models/content.model';

describe('CtaComponent', () => {
  let component: CtaComponent;
  let fixture: ComponentFixture<CtaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CtaComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(CtaComponent);
    component = fixture.componentInstance;
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('deve gerar whatsappLink corretamente com e sem dados', () => {
    component.dados = null;
    expect(component.whatsappLink).toBe('https://wa.me/');

    component.dados = { whatsapp: '+55 (11) 99999-1234' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/5511999991234');
  });

  it('deve exibir dados do CTA quando informados', () => {
    const cta: ContentCta = {
      selo: 'Fale Conosco',
      titulo: 'Faça seu pedido',
      descricao: 'Entre em contato pelo WhatsApp'
    };
    fixture.componentRef.setInput('cta', cta);
    fixture.detectChanges();

    expect(component.cta?.titulo).toBe('Faça seu pedido');
  });
});
