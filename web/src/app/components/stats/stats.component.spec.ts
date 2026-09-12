import { ComponentFixture, TestBed } from '@angular/core/testing';
import { describe, it, expect, beforeEach } from 'vitest';
import { StatsComponent } from './stats.component';
import { ContentEstatisticas } from '../../models/content.model';

describe('StatsComponent', () => {
  let component: StatsComponent;
  let fixture: ComponentFixture<StatsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatsComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(StatsComponent);
    component = fixture.componentInstance;
  });

  it('deve usar o fallback default com 4 estatísticas quando nenhum input for fornecido', () => {
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const items = compiled.querySelectorAll('.numero-item');
    expect(items.length).toBe(4);

    const animatedElements = compiled.querySelectorAll('.numero-val[data-target]');
    expect(animatedElements.length).toBe(3);

    const labels = Array.from(compiled.querySelectorAll('.numero-label')).map(el => el.textContent?.trim());
    expect(labels).toContain('Anos de Tradição');
    expect(labels).toContain('Produção Fresca');
  });

  it('deve animar caso o valor seja número e anular animação caso seja texto', () => {
    const mockEstatisticas: ContentEstatisticas = {
      lista: [
        { nome: 'Anos de Mercado', valor: '+15' },
        { nome: 'Satisfação', valor: '99%' },
        { nome: 'Produção', valor: 'Diária' },
        { nome: 'Tipo de Massa', valor: 'Artesanal' }
      ]
    };

    fixture.componentRef.setInput('estatisticas', mockEstatisticas);
    fixture.detectChanges();

    expect(component.parsedStats.length).toBe(4);

    expect(component.parsedStats[0].target).toBe(15);
    expect(component.parsedStats[0].prefix).toBe('+');

    expect(component.parsedStats[1].target).toBe(99);
    expect(component.parsedStats[1].suffix).toBe('%');

    expect(component.parsedStats[2].target).toBeNull();
    expect(component.parsedStats[2].text).toBe('Diária');

    expect(component.parsedStats[3].target).toBeNull();
    expect(component.parsedStats[3].text).toBe('Artesanal');

    const compiled = fixture.nativeElement as HTMLElement;
    const values = compiled.querySelectorAll('.numero-val');

    expect(values[0].getAttribute('data-target')).toBe('15');
    expect(values[1].getAttribute('data-target')).toBe('99');

    expect(values[2].getAttribute('data-target')).toBeNull();
    expect(values[2].textContent?.trim()).toBe('Diária');
    expect(values[3].getAttribute('data-target')).toBeNull();
    expect(values[3].textContent?.trim()).toBe('Artesanal');
  });
});

