import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FooterComponent } from './footer.component';
import { ContentRodape, SiteData } from '../../models/content.model';
import { vi } from 'vitest';

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

  afterEach(() => {
    if (component) {
      component.ngOnDestroy();
    }
  });

  it('deve criar o componente', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('currentYear inicia com o ano atual', () => {
    fixture.detectChanges();
    const expectedYear = new Date().getFullYear();
    expect(component.currentYear).toBe(expectedYear);
  });

  it('o copyright aparece com o ano correto no DOM', () => {
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    const copySpan = compiled.querySelector('.copy span');
    const expectedYear = new Date().getFullYear();

    expect(copySpan).toBeTruthy();
    expect(copySpan?.textContent?.trim()).toBe(`© ${expectedYear} Eloa Mercados e Panificações. Todos os direitos reservados.`);
  });

  it('o texto de copy não depende de textoDireitos', () => {
    const mockRodape = {
      descricao: 'Padaria tradicional',
      textoContato: 'Fale conosco no WhatsApp',
      textoDireitos: 'Texto Antigo Customizado Que Nao Deve Aparecer'
    } as unknown as ContentRodape;

    fixture.componentRef.setInput('rodape', mockRodape);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const copySpan = compiled.querySelector('.copy span');
    const expectedYear = new Date().getFullYear();

    expect(copySpan?.textContent?.trim()).toBe(`© ${expectedYear} Eloa Mercados e Panificações. Todos os direitos reservados.`);
    expect(copySpan?.textContent).not.toContain('Texto Antigo Customizado');
  });

  it('o timer é limpo no ngOnDestroy', () => {
    fixture.detectChanges();
    const spyClearTimeout = vi.spyOn(window, 'clearTimeout');

    expect(component.updateTimer).toBeDefined();
    component.ngOnDestroy();

    expect(spyClearTimeout).toHaveBeenCalled();
    expect(component.updateTimer).toBeUndefined();
  });

  it('o timer não cria múltiplas atualizações desnecessárias ao reagendar', () => {
    fixture.detectChanges();
    const spyClearTimeout = vi.spyOn(window, 'clearTimeout');

    const initialTimer = component.updateTimer;
    expect(initialTimer).toBeDefined();

    component.scheduleNextMonthlyUpdate();

    expect(spyClearTimeout).toHaveBeenCalledWith(initialTimer);
    expect(component.updateTimer).toBeDefined();
  });

  it('deve gerar whatsappLink corretamente formatado para o wa.me', () => {
    component.dados = null;
    expect(component.whatsappLink).toBe('https://wa.me/');

    component.dados = { whatsapp: '11977776666' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/5511977776666');

    component.dados = { whatsapp: '+55 (11) 98888-9999' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/5511988889999');
  });

  it('deve formatar CNPJ para exibição independente da forma escrita pelo admin', () => {
    component.dados = { cnpj: '57068741000138' } as unknown as SiteData;
    expect(component.formattedCnpj).toBe('57.068.741/0001-38');

    component.dados = { cnpj: '  57.068.741/0001-38  ' } as unknown as SiteData;
    expect(component.formattedCnpj).toBe('57.068.741/0001-38');

    component.dados = null;
    expect(component.formattedCnpj).toBe('57.068.741/0001-38');
  });

  it('deve exibir dados do rodapé quando fornecidos', () => {
    const rodape: ContentRodape = {
      descricao: 'Padaria e Confeitaria',
      textoContato: 'Fale conosco'
    };
    fixture.componentRef.setInput('rodape', rodape);
    fixture.detectChanges();

    expect(component.rodape?.descricao).toBe('Padaria e Confeitaria');
    expect(component.rodape?.textoContato).toBe('Fale conosco');
  });
});
