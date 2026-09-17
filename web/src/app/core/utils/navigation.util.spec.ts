import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { smoothScrollToSection } from './navigation.util';

describe('navigation.util', () => {
  let mockEvent: Event;
  let spyPreventDefault: ReturnType<typeof vi.fn>;
  let spyScrollTo: ReturnType<typeof vi.fn>;
  let spyPushState: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    spyPreventDefault = vi.fn();
    mockEvent = {
      preventDefault: spyPreventDefault
    } as unknown as Event;

    spyScrollTo = vi.spyOn(window, 'scrollTo').mockImplementation(() => {});
    spyPushState = vi.spyOn(history, 'pushState').mockImplementation(() => {});
  });

  afterEach(() => {
    vi.restoreAllMocks();
    document.body.innerHTML = '';
  });

  it('deve prevenir o comportamento padrao do evento de clique', () => {
    smoothScrollToSection(mockEvent, 'home');
    expect(spyPreventDefault).toHaveBeenCalled();
  });

  it('ao navegar para home, deve rolar para top: 0 com comportamento smooth', () => {
    smoothScrollToSection(mockEvent, 'home');
    expect(spyScrollTo).toHaveBeenCalledWith({
      top: 0,
      behavior: 'smooth'
    });
  });

  it('ao navegar para uma secao existente, deve calcular a posicao compensando a altura do header', () => {
    const header = document.createElement('header');
    Object.defineProperty(header, 'offsetHeight', { value: 98, configurable: true });
    document.body.appendChild(header);

    const section = document.createElement('section');
    section.id = 'catalog';
    section.getBoundingClientRect = vi.fn().mockReturnValue({ top: 500 });
    document.body.appendChild(section);

    Object.defineProperty(window, 'scrollY', { value: 200, configurable: true });

    smoothScrollToSection(mockEvent, 'catalog');

    // elementPosition = 500 + 200 = 700. offsetPosition = 700 - 98 = 602.
    expect(spyScrollTo).toHaveBeenCalledWith({
      top: 602,
      behavior: 'smooth'
    });
    expect(spyPushState).toHaveBeenCalledWith(null, '', '#catalog');
  });

  it('nao deve rolar se o elemento alvo nao existir no documento', () => {
    smoothScrollToSection(mockEvent, 'secao-inexistente');
    expect(spyScrollTo).not.toHaveBeenCalled();
  });
});
