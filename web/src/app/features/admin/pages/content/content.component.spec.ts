import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { ContentComponent, optionalCnpjValidator, optionalPhoneValidator } from './content.component';
import { ContentService } from '../../../../services/content.service';
import { ToastService } from '../../../../services/toast.service';
import { of, throwError } from 'rxjs';
import { DEFAULT_SITE_CONTENT } from '../../../../core/constants/content-fallbacks';
import { SiteContent } from '../../../../models/content.model';

describe('ContentComponent (Admin)', () => {
  let component: ContentComponent;
  let fixture: ComponentFixture<ContentComponent>;
  let mockContentService: any;
  let mockToastService: any;

  beforeEach(async () => {
    mockContentService = {
      getContent: vi.fn().mockReturnValue(of(DEFAULT_SITE_CONTENT)),
      saveContent: vi.fn().mockReturnValue(of(DEFAULT_SITE_CONTENT))
    };

    mockToastService = {
      success: vi.fn(),
      error: vi.fn()
    };

    await TestBed.configureTestingModule({
      imports: [ContentComponent, ReactiveFormsModule],
      providers: [
        { provide: ContentService, useValue: mockContentService },
        { provide: ToastService, useValue: mockToastService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ContentComponent);
    component = fixture.componentInstance;
  });

  describe('Validadores Customizados', () => {
    it('optionalCnpjValidator deve aceitar valor vazio/nulo', () => {
      const validator = optionalCnpjValidator();
      expect(validator(new FormControl(''))).toBeNull();
      expect(validator(new FormControl(null))).toBeNull();
      expect(validator(new FormControl('   '))).toBeNull();
    });

    it('optionalCnpjValidator deve aceitar CNPJ formatado e numérico/alfanumérico de 14 dígitos', () => {
      const validator = optionalCnpjValidator();
      expect(validator(new FormControl('12.345.678/0001-95'))).toBeNull();
      expect(validator(new FormControl('12345678000195'))).toBeNull();
      expect(validator(new FormControl('12ABC345000195'))).toBeNull();
    });

    it('optionalCnpjValidator deve rejeitar CNPJ com quantidade errada ou caracteres inválidos', () => {
      const validator = optionalCnpjValidator();
      expect(validator(new FormControl('12345'))).toEqual({ invalidCnpj: true });
      expect(validator(new FormControl('123456780001959999'))).toEqual({ invalidCnpj: true });
    });

    it('optionalPhoneValidator deve aceitar valor vazio/nulo', () => {
      const validator = optionalPhoneValidator();
      expect(validator(new FormControl(''))).toBeNull();
      expect(validator(new FormControl(null))).toBeNull();
      expect(validator(new FormControl('   '))).toBeNull();
    });

    it('optionalPhoneValidator deve aceitar telefones de 10 a 13 dígitos com ou sem formatação', () => {
      const validator = optionalPhoneValidator();
      expect(validator(new FormControl('(11) 98888-7777'))).toBeNull();
      expect(validator(new FormControl('11988887777'))).toBeNull();
      expect(validator(new FormControl('1133334444'))).toBeNull();
      expect(validator(new FormControl('+55 11 98888-7777'))).toBeNull();
    });

    it('optionalPhoneValidator deve rejeitar telefone com menos de 10 dígitos ou sem dígitos', () => {
      const validator = optionalPhoneValidator();
      expect(validator(new FormControl('12345'))).toEqual({ invalidPhone: true });
      expect(validator(new FormControl('TelefoneEloa'))).toEqual({ invalidPhone: true });
    });
  });

  describe('Formatação nos Campos (Blur)', () => {
    it('deve auto-formatar CNPJ ao executar formatCnpjField()', () => {
      fixture.detectChanges();
      const ctrl = component.contentForm.get('dados.cnpj');
      ctrl?.setValue('12345678000195');

      component.formatCnpjField();

      expect(ctrl?.value).toBe('12.345.678/0001-95');
    });

    it('deve auto-formatar WhatsApp ao executar formatWhatsappField()', () => {
      fixture.detectChanges();
      const ctrl = component.contentForm.get('dados.whatsapp');
      ctrl?.setValue('11977778888');

      component.formatWhatsappField();

      expect(ctrl?.value).toBe('(11) 97777-8888');
    });
  });

  it('deve inicializar o formulário e carregar o conteúdo existente', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy();
    expect(mockContentService.getContent).toHaveBeenCalled();
    expect(component.contentForm).toBeDefined();
    expect(component.bannerIndicadores.length).toBe(3);
    expect(component.diferenciaisCards.length).toBe(3);
    expect(component.sobreLista.length).toBeGreaterThan(0);
    expect(component.estatisticasLista.length).toBeGreaterThan(0);
    expect(component.faqItens.length).toBeGreaterThan(0);
  });

  it('deve alternar seções com toggleSection()', () => {
    expect(component.openSection).toBeNull();
    component.toggleSection('banner');
    expect(component.openSection).toBe('banner');
    component.toggleSection('banner');
    expect(component.openSection).toBeNull();
  });

  it('deve adicionar e remover itens na lista Sobre', () => {
    fixture.detectChanges();
    const initialCount = component.sobreLista.length;

    component.addSobreItem();
    expect(component.sobreLista.length).toBe(initialCount + 1);

    component.removeSobreItem(0);
    expect(component.sobreLista.length).toBe(initialCount);
  });

  it('deve adicionar e remover itens na lista de Estatísticas', () => {
    fixture.detectChanges();
    const initialCount = component.estatisticasLista.length;

    component.addEstatistica();
    expect(component.estatisticasLista.length).toBe(initialCount + 1);

    component.removeEstatistica(0);
    expect(component.estatisticasLista.length).toBe(initialCount);
  });

  it('deve bloquear saveContent se o formulário for inválido e abrir a seção com erro', () => {
    fixture.detectChanges();
    component.contentForm.get('banner.titulo')?.setValue('A'.repeat(200)); // Max 150

    component.saveContent();

    expect(component.contentForm.invalid).toBe(true);
    expect(component.openSection).toBe('banner');
    expect(mockToastService.error).toHaveBeenCalledWith('Revise os campos destacados antes de salvar.');
    expect(mockContentService.saveContent).not.toHaveBeenCalled();
  });

  it('deve validar WhatsApp no saveContent e emitir toast se for inválido', () => {
    fixture.detectChanges();
    component.contentForm.get('dados.whatsapp')?.setValue('123');

    component.saveContent();

    expect(mockToastService.error).toHaveBeenCalledWith('Revise os campos destacados antes de salvar.');
    expect(mockContentService.saveContent).not.toHaveBeenCalled();
  });

  it('deve salvar o conteúdo com sucesso e formatar dados antes do envio', () => {
    fixture.detectChanges();

    component.contentForm.get('banner.titulo')?.setValue('Novo Título Eloá');
    component.contentForm.get('dados.cnpj')?.setValue('12345678000195');
    component.contentForm.get('dados.whatsapp')?.setValue('11977778888');

    component.saveContent();

    expect(mockContentService.saveContent).toHaveBeenCalledWith(expect.objectContaining({
      banner: expect.objectContaining({
        titulo: 'Novo Título Eloá'
      }),
      dados: expect.objectContaining({
        cnpj: '12.345.678/0001-95',
        whatsapp: '(11) 97777-8888'
      })
    }));
    expect(mockToastService.success).toHaveBeenCalledWith('Conteúdo salvo com sucesso!');
  });

  it('deve tratar erro ao salvar o conteúdo', () => {
    mockContentService.saveContent.mockReturnValue(throwError(() => new Error('Erro de conexão')));
    fixture.detectChanges();

    component.saveContent();

    expect(mockToastService.error).toHaveBeenCalledWith('Erro ao salvar o conteúdo. Tente novamente.');
  });

  it('deve preencher fallbacks em caso de falha no carregamento inicial', () => {
    const warnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
    mockContentService.getContent.mockReturnValue(throwError(() => new Error('Falha')));

    component.ngOnInit();

    expect(component.bannerIndicadores.length).toBe(3);
    warnSpy.mockRestore();
  });

  it('deve desinscrever subscriptions no ngOnDestroy', () => {
    fixture.detectChanges();
    const unsubSpy = vi.spyOn((component as unknown as { subs: { unsubscribe: () => void } }).subs, 'unsubscribe');
    component.ngOnDestroy();
    expect(unsubSpy).toHaveBeenCalled();
  });
});

