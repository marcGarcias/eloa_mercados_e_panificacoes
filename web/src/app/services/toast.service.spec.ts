import { TestBed } from '@angular/core/testing';
import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { ToastService } from './toast.service';

describe('ToastService', () => {
  let service: ToastService;

  beforeEach(() => {
    vi.useFakeTimers();

    document.getElementById('toast-container')?.remove();

    TestBed.configureTestingModule({
      providers: [ToastService]
    });

    service = TestBed.inject(ToastService);
  });

  afterEach(() => {
    document.getElementById('toast-container')?.remove();
    vi.useRealTimers();
  });

  it('deve criar o container e renderizar o elemento de toast com sucesso', () => {

    service.success('Produto salvo com sucesso!', 'Tudo certo');

    const container = document.getElementById('toast-container');
    expect(container).not.toBeNull();

    const toast = container?.querySelector('.toast-success');
    expect(toast).not.toBeNull();

    const title = toast?.querySelector('.toast-title')?.textContent;
    const msg = toast?.querySelector('.toast-msg')?.textContent;

    expect(title).toBe('Tudo certo');
    expect(msg).toBe('Produto salvo com sucesso!');
  });

  it('deve aplicar a classe show após o delay de entrada', () => {

    service.info('Atualizando catálogo...');
    const toast = document.querySelector('.toast-info');

    expect(toast?.classList.contains('show')).toBe(false);

    vi.advanceTimersByTime(25);

    expect(toast?.classList.contains('show')).toBe(true);
  });

  it('deve auto-remover o toast e o container após 3.5s + delay de saída', () => {

    service.warning('Atenção ao estoque');
    const container = document.getElementById('toast-container');
    expect(container).not.toBeNull();

    vi.advanceTimersByTime(3900);

    expect(document.querySelector('.toast-warning')).toBeNull();
    expect(document.getElementById('toast-container')).toBeNull();
  });

  it('deve remover o toast ao clicar no botão fechar', () => {

    service.error('Falha de conexão', 'Erro 500');
    const closeBtn = document.querySelector('.toast-close') as HTMLButtonElement;
    expect(closeBtn).not.toBeNull();

    closeBtn.click();

    vi.advanceTimersByTime(350);

    expect(document.querySelector('.toast-error')).toBeNull();
  });
});

