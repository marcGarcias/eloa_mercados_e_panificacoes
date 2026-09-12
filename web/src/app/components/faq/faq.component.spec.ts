import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, it, expect, beforeEach } from 'vitest';
import { FaqComponent } from './faq.component';
import { ContentFaq } from '../../models/content.model';

describe('FaqComponent', () => {
  let component: FaqComponent;
  let fixture: ComponentFixture<FaqComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FaqComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FaqComponent);
    component = fixture.componentInstance;
  });

  it('deve utilizar os itens do fallback padrão quando nenhum input faq for fornecido', () => {
    fixture.detectChanges();

    expect(component.itens.length).toBe(5);
    expect(component.itens[0].id).toBe('faq-1');
    expect(component.itens[0].pergunta).toBe('Como faço para pedir ou cotar produtos da Eloá Panificações?');

    const compiled = fixture.nativeElement as HTMLElement;
    const questions = compiled.querySelectorAll('.faq-question');
    expect(questions.length).toBe(5);
  });

  it('deve renderizar as respostas dinâmicas quando fornecido o @Input() faq', () => {
    const mockFaq: ContentFaq = {
      itens: [
        {
          id: 'faq-1',
          pergunta: 'Como faço para pedir ou cotar produtos da Eloá Panificações?',
          resposta: 'Resposta dinâmica atualizada pelo painel administrativo!'
        },
        {
          id: 'faq-2',
          pergunta: 'Quais tipos de produtos a Eloá comercializa?',
          resposta: 'Pães especiais artesanais e fornadas quentes.'
        }
      ]
    };

    fixture.componentRef.setInput('faq', mockFaq);
    fixture.detectChanges();

    expect(component.itens.length).toBe(2);
    expect(component.itens[0].resposta).toBe('Resposta dinâmica atualizada pelo painel administrativo!');

    const compiled = fixture.nativeElement as HTMLElement;
    const answers = compiled.querySelectorAll('.faq-answer p');
    expect(answers[0].textContent?.trim()).toBe('Resposta dinâmica atualizada pelo painel administrativo!');
  });
});
