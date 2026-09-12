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

    const iconImg = document.createElement('img');
    iconImg.src = `/assets/icons/toast-${type}.svg`;
    iconImg.className = 'toast-icon';
    iconImg.alt = type;

    const contentDiv = document.createElement('div');
    contentDiv.className = 'toast-content';

    const titleDiv = document.createElement('div');
    titleDiv.className = 'toast-title';
    titleDiv.textContent = title;

    const msgDiv = document.createElement('div');
    msgDiv.className = 'toast-msg';
    msgDiv.textContent = message;

    contentDiv.appendChild(titleDiv);
    contentDiv.appendChild(msgDiv);

    const closeBtn = document.createElement('button');
    closeBtn.className = 'toast-close';
    closeBtn.setAttribute('aria-label', 'Fechar');
    closeBtn.innerHTML = '&times;';

    const progressDiv = document.createElement('div');
    progressDiv.className = 'toast-progress';

    toast.appendChild(iconImg);
    toast.appendChild(contentDiv);
    toast.appendChild(closeBtn);
    toast.appendChild(progressDiv);

    container.appendChild(toast);

    // Pequeno delay para acionar a transição de entrada do CSS
    setTimeout(() => {
      toast.classList.add('show');
    }, 20);
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

