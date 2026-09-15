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

/**
 * Formata peso do produto para exibição padronizada no Admin e na Home:
 * - Menor que 1kg: exibe em gramas sem decimais (ex: 0.12 -> 120g, 0.05 -> 50g, 0.5 -> 500g)
 * - 1kg ou número inteiro: exibe como inteiro com unidade kg (ex: 1 -> 1kg, 2 -> 2kg)
 * - Decimais >= 1kg: exibe com 3 casas decimais (ex: 3.25 -> 3.250kg, 1.5 -> 1.500kg)
 */
export function formatProductWeight(weight?: number | string | null): string {
  if (weight == null || weight === '') return '';
  const num = typeof weight === 'number' ? weight : parseFloat(String(weight).replace(',', '.'));
  if (isNaN(num) || num <= 0) return '';

  if (num < 1) {
    const grams = Math.round(num * 1000);
    return `${grams}g`;
  }

  if (Number.isInteger(num)) {
    return `${num}kg`;
  }

  return `${num.toFixed(3)}kg`;
}

/**
 * Converte a entrada de peso digitada pelo usuário em quilogramas (kg) numérico.
 * Regras:
 * - Se houver um 0 à frente sem ponto (ex: '0120', '0500', '050', '05'): o resto são gramas ('0120' -> 120g = 0.12kg).
 * - Se houver ponto/vírgula com 0 à frente (ex: '0.120', '0,120', '0.5'): 0.12kg, 0.5kg.
 * - Se for valor >= 1 (ex: '1', '1.0', '3.250', '3,250'): 1kg, 3.25kg.
 * - Suporta também digitação explícita com 'g' ou 'kg' (ex: '120g' -> 0.12, '3.250kg' -> 3.25).
 */
export function parseProductWeightInput(input?: string | number | null): number {
  if (input == null || input === '') return 0;
  if (typeof input === 'number') {
    return isNaN(input) || input < 0 ? 0 : input;
  }

  let raw = String(input).trim().toLowerCase().replace(',', '.');
  if (!raw) return 0;

  if (raw.endsWith('kg')) {
    raw = raw.replace('kg', '').trim();
    const val = parseFloat(raw);
    return isNaN(val) || val < 0 ? 0 : val;
  }

  if (raw.endsWith('g')) {
    raw = raw.replace('g', '').trim();
    const grams = parseFloat(raw);
    return isNaN(grams) || grams < 0 ? 0 : grams / 1000;
  }

  // Se começa com '0' e NÃO possui ponto decimal (ex: '0120', '0500', '050', '05')
  if (raw.startsWith('0') && !raw.includes('.')) {
    const rest = raw.slice(1);
    const grams = parseFloat(rest);
    return isNaN(grams) || grams <= 0 ? 0 : grams / 1000;
  }

  const val = parseFloat(raw);
  return isNaN(val) || val < 0 ? 0 : val;
}
