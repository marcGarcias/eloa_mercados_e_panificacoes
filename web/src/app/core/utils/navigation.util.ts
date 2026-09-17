/**
 * Realiza a rolagem suave e assertiva até a seção especificada,
 * compensando a altura real do cabeçalho sticky em tempo de execução.
 */
export function smoothScrollToSection(event: Event, targetId: string): void {
  event.preventDefault();

  if (targetId === 'home') {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    if (window.location.hash !== '#home') {
      history.pushState(null, '', '#home');
    }
    return;
  }

  const targetElement = document.getElementById(targetId);
  if (!targetElement) {
    return;
  }

  const header = document.querySelector('header');
  const headerHeight = header ? header.offsetHeight : 98;

  const elementPosition = targetElement.getBoundingClientRect().top + window.scrollY;
  const offsetPosition = Math.max(0, Math.round(elementPosition - headerHeight));

  window.scrollTo({
    top: offsetPosition,
    behavior: 'smooth'
  });

  if (window.location.hash !== '#' + targetId) {
    history.pushState(null, '', '#' + targetId);
  }
}
