import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HeaderComponent } from './header.component';
import { SiteData } from '../../models/content.model';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve retornar link do whatsapp vazio se dados ou whatsapp forem nulos', () => {
    component.dados = null;
    expect(component.whatsappLink).toBe('https://wa.me/');

    component.dados = { whatsapp: '' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/');
  });

  it('deve limpar caracteres não numéricos do whatsapp e retornar url correta', () => {
    component.dados = { whatsapp: '(11) 98888-7777' } as unknown as SiteData;
    expect(component.whatsappLink).toBe('https://wa.me/5511988887777');
  });
});
