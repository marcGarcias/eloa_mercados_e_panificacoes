import { FormControl } from '@angular/forms';
import { checkPasswordStrength, passwordStrengthValidator } from './password-validator';

describe('Password Validator Unit Tests', () => {
  describe('checkPasswordStrength()', () => {
    it('deve marcar todos os requisitos como falsos para senha vazia ou nula', () => {
      const resNull = checkPasswordStrength(null);
      expect(resNull.minLength).toBe(false);
      expect(resNull.hasUpperCase).toBe(false);
      expect(resNull.hasLowerCase).toBe(false);
      expect(resNull.hasSpecialChar).toBe(false);
      expect(resNull.isValid).toBe(false);

      const resEmpty = checkPasswordStrength('');
      expect(resEmpty.isValid).toBe(false);
    });

    it('deve validar tamanho mínimo de 8 caracteres', () => {
      const shortRes = checkPasswordStrength('Ab1!xyz');
      expect(shortRes.minLength).toBe(false);
      expect(shortRes.isValid).toBe(false);

      const validLengthRes = checkPasswordStrength('Ab1!xyzw');
      expect(validLengthRes.minLength).toBe(true);
    });

    it('deve validar presença de letra maiúscula', () => {
      const noUpper = checkPasswordStrength('senha123!@');
      expect(noUpper.hasUpperCase).toBe(false);
      expect(noUpper.isValid).toBe(false);

      const withUpper = checkPasswordStrength('Senha123!@');
      expect(withUpper.hasUpperCase).toBe(true);
    });

    it('deve validar presença de letra minúscula', () => {
      const noLower = checkPasswordStrength('SENHA123!@');
      expect(noLower.hasLowerCase).toBe(false);
      expect(noLower.isValid).toBe(false);

      const withLower = checkPasswordStrength('Senha123!@');
      expect(withLower.hasLowerCase).toBe(true);
    });

    it('deve validar presença de caractere especial', () => {
      const noSpecial = checkPasswordStrength('SenhaForte123');
      expect(noSpecial.hasSpecialChar).toBe(false);
      expect(noSpecial.isValid).toBe(false);

      const withSpecial = checkPasswordStrength('SenhaForte123!');
      expect(withSpecial.hasSpecialChar).toBe(true);
    });

    it('deve validar como válido quando todos os critérios forem atendidos', () => {
      const result = checkPasswordStrength('SenhaSegura123!');
      expect(result.minLength).toBe(true);
      expect(result.hasUpperCase).toBe(true);
      expect(result.hasLowerCase).toBe(true);
      expect(result.hasSpecialChar).toBe(true);
      expect(result.isValid).toBe(true);
    });
  });

  describe('passwordStrengthValidator()', () => {
    const validator = passwordStrengthValidator();

    it('deve retornar null para valor vazio (deixando para Validators.required)', () => {
      const control = new FormControl('');
      expect(validator(control)).toBeNull();
    });

    it('deve retornar erro passwordStrength para senha fraca', () => {
      const control = new FormControl('fraca');
      const errors = validator(control);
      expect(errors).not.toBeNull();
      expect(errors?.['passwordStrength']).toBeDefined();
      expect(errors?.['passwordStrength'].isValid).toBe(false);
    });

    it('deve retornar null para senha forte e válida', () => {
      const control = new FormControl('SenhaForte@2026');
      expect(validator(control)).toBeNull();
    });
  });
});
