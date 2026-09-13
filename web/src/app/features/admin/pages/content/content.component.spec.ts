import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ContentComponent } from './content.component';
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

  it('deve validar CNPJ no saveContent e emitir toast se for inválido', () => {
    fixture.detectChanges();
    component.contentForm.get('dados.cnpj')?.setValue('CNPJ-INVALIDO');

    component.saveContent();

    expect(mockToastService.error).toHaveBeenCalledWith('Formato de CNPJ inválido. Ex: 00.000.000/0000-00');
    expect(mockContentService.saveContent).not.toHaveBeenCalled();
  });

  it('deve salvar o conteúdo com sucesso', () => {
    fixture.detectChanges();

    component.contentForm.get('banner.titulo')?.setValue('Novo Título Eloá');
    component.saveContent();

    expect(mockContentService.saveContent).toHaveBeenCalledWith(expect.objectContaining({
      banner: expect.objectContaining({
        titulo: 'Novo Título Eloá'
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
