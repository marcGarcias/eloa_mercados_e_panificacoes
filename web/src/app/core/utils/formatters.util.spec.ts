import { describe, it, expect } from 'vitest';
import { formatCnpj, formatPhone, formatWhatsappLink } from './formatters.util';

describe('formatters.util', () => {
  describe('formatCnpj', () => {
    it('deve formatar CNPJ com 14 dígitos numéricos sem pontuação', () => {
      expect(formatCnpj('57068741000138')).toBe('57.068.741/0001-38');
    });

    it('deve formatar CNPJ já formatado mantendo a formatação correta', () => {
      expect(formatCnpj('57.068.741/0001-38')).toBe('57.068.741/0001-38');
    });

    it('deve formatar CNPJ com espaços e caracteres mistos', () => {
      expect(formatCnpj('  57 068 741 0001 38  ')).toBe('57.068.741/0001-38');
    });

    it('deve formatar CNPJ alfanumérico', () => {
      expect(formatCnpj('12ABC345000199')).toBe('12.ABC.345/0001-99');
    });

    it('deve retornar string vazia para valores nulos ou vazios', () => {
      expect(formatCnpj(null)).toBe('');
      expect(formatCnpj(undefined)).toBe('');
      expect(formatCnpj('')).toBe('');
      expect(formatCnpj('   ')).toBe('');
    });

    it('deve retornar valor trimmed se não tiver 14 caracteres', () => {
      expect(formatCnpj('12345')).toBe('12345');
    });
  });

  describe('formatPhone', () => {
    it('deve formatar celular com DDD (11 dígitos)', () => {
      expect(formatPhone('11982869113')).toBe('(11) 98286-9113');
    });

    it('deve formatar telefone fixo com DDD (10 dígitos)', () => {
      expect(formatPhone('1133334444')).toBe('(11) 3333-4444');
    });

    it('deve formatar celular com DDI 55 (13 dígitos)', () => {
      expect(formatPhone('5511982869113')).toBe('(11) 98286-9113');
    });

    it('deve formatar telefone fixo com DDI 55 (12 dígitos)', () => {
      expect(formatPhone('551133334444')).toBe('(11) 3333-4444');
    });

    it('deve formatar celular já com máscara', () => {
      expect(formatPhone('(11) 98286-9113')).toBe('(11) 98286-9113');
    });

    it('deve formatar número com 9 dígitos', () => {
      expect(formatPhone('982869113')).toBe('98286-9113');
    });

    it('deve formatar número com 8 dígitos', () => {
      expect(formatPhone('33334444')).toBe('3333-4444');
    });

    it('deve retornar string vazia para valores nulos ou vazios', () => {
      expect(formatPhone(null)).toBe('');
      expect(formatPhone(undefined)).toBe('');
      expect(formatPhone('')).toBe('');
    });
  });

  describe('formatWhatsappLink', () => {
    it('deve adicionar 55 para número de 11 dígitos', () => {
      expect(formatWhatsappLink('11982869113')).toBe('https://wa.me/5511982869113');
    });

    it('deve adicionar 55 para número de 10 dígitos', () => {
      expect(formatWhatsappLink('1133334444')).toBe('https://wa.me/551133334444');
    });

    it('deve manter número que já possui DDI 55', () => {
      expect(formatWhatsappLink('5511982869113')).toBe('https://wa.me/5511982869113');
    });

    it('deve limpar caracteres não numéricos', () => {
      expect(formatWhatsappLink('+55 (11) 98286-9113')).toBe('https://wa.me/5511982869113');
    });

    it('deve retornar url base para valores nulos ou vazios', () => {
      expect(formatWhatsappLink(null)).toBe('https://wa.me/');
      expect(formatWhatsappLink(undefined)).toBe('https://wa.me/');
      expect(formatWhatsappLink('')).toBe('https://wa.me/');
    });
  });
});
