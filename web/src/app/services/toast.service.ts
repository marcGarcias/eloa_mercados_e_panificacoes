import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  success(message: string, title: string = 'Sucesso') {
    this.show('success', message, title);
  }

  error(message: string, title: string = 'Erro') {
    this.show('error', message, title);
  }

  warning(message: string, title: string = 'Atenção') {
    this.show('warning', message, title);
  }

  info(message: string, title: string = 'Informação') {
    this.show('info', message, title);
  }

  private show(type: 'success' | 'error' | 'warning' | 'info', message: string, title: string) {
    let container = document.getElementById('toast-container');
    if (!container) {
      container = document.createElement('div');
      container.id = 'toast-container';
      document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;

    toast.innerHTML = `
      <img src="/assets/icons/toast-${type}.svg" class="toast-icon" alt="${type}" />
      <div class="toast-content">
        <div class="toast-title">${title}</div>
        <div class="toast-msg">${message}</div>
      </div>
      <button class="toast-close" aria-label="Fechar">&times;</button>
      <div class="toast-progress"></div>
    `;

    container.appendChild(toast);

    // Pequeno delay para acionar a transição de entrada do CSS
    setTimeout(() => {
      toast.classList.add('show');
    }, 20);

    const closeBtn = toast.querySelector('.toast-close');
    const removeToast = () => {
      toast.classList.remove('show');
      // Aguarda o término da transição de saída para remover do DOM
      setTimeout(() => {
        toast.remove();
        // Remove o container se estiver vazio para manter o DOM limpo
        if (container && container.childNodes.length === 0) {
          container.remove();
        }
      }, 300);
    };

    closeBtn?.addEventListener('click', removeToast);

    // Auto-destruição após 3.5 segundos
    setTimeout(removeToast, 3500);
  }
}
