/**
 * Utilitários de formatação para exibição consistente de dados (CNPJ, Telefone/WhatsApp e Links).
 */

/**
 * Formata CNPJ de 14 caracteres (numérico ou alfanumérico) no padrão XX.XXX.XXX/XXXX-XX.
 * Se o valor não possuir 14 caracteres alfanuméricos, retorna o valor original trimmed ou string vazia.
 */
export function formatCnpj(value?: string | null): string {
  if (!value) return '';
  const trimmed = value.trim();
  const clean = trimmed.replace(/[^a-zA-Z0-9]/g, '').toUpperCase();
  if (clean.length === 14) {
    return `${clean.slice(0, 2)}.${clean.slice(2, 5)}.${clean.slice(5, 8)}/${clean.slice(8, 12)}-${clean.slice(12, 14)}`;
  }
  return trimmed;
}

/**
 * Formata telefone/WhatsApp brasileiro para exibição amigável:
 * - 11 dígitos (DDD + 9 dígitos): (XX) XXXXX-XXXX
 * - 10 dígitos (DDD + 8 dígitos): (XX) XXXX-XXXX
 * - 13 dígitos com DDI 55: (XX) XXXXX-XXXX
 * - 12 dígitos com DDI 55: (XX) XXXX-XXXX
 */
export function formatPhone(value?: string | null): string {
  if (!value) return '';
  const trimmed = value.trim();
  let digits = trimmed.replace(/\D/g, '');

  if (digits.startsWith('55') && (digits.length === 12 || digits.length === 13)) {
    digits = digits.slice(2);
  }

  if (digits.length === 11) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
  }
  if (digits.length === 10) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6, 10)}`;
  }
  if (digits.length === 9) {
    return `${digits.slice(0, 5)}-${digits.slice(5, 9)}`;
  }
  if (digits.length === 8) {
    return `${digits.slice(0, 4)}-${digits.slice(4, 8)}`;
  }

  return trimmed;
}

/**
 * Monta URL direta do WhatsApp (https://wa.me/...) garantindo o prefixo DDI 55 para números brasileiros de 10 ou 11 dígitos.
 */
export function formatWhatsappLink(value?: string | null): string {
  if (!value) return 'https://wa.me/';
  const digits = value.replace(/\D/g, '');
  if (!digits) return 'https://wa.me/';

  if (digits.length === 10 || digits.length === 11) {
    return `https://wa.me/55${digits}`;
  }

  return `https://wa.me/${digits}`;
}
