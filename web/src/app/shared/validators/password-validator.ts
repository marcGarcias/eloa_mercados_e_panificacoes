import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export interface PasswordRulesState {
  minLength: boolean;
  hasUpperCase: boolean;
  hasLowerCase: boolean;
  hasSpecialChar: boolean;
  isValid: boolean;
}

export function checkPasswordStrength(password: string | null | undefined): PasswordRulesState {
  const pwd = password || '';
  const minLength = pwd.length >= 8;
  const hasUpperCase = /[A-Z]/.test(pwd);
  const hasLowerCase = /[a-z]/.test(pwd);
  const hasSpecialChar = /[^a-zA-Z0-9]/.test(pwd);
  const isValid = minLength && hasUpperCase && hasLowerCase && hasSpecialChar;

  return {
    minLength,
    hasUpperCase,
    hasLowerCase,
    hasSpecialChar,
    isValid
  };
}

export function passwordStrengthValidator(): ValidatorFn {
  return (control: AbstractControl): ValidationErrors | null => {
    const value = control.value;
    if (!value) {
      return null;
    }
    const state = checkPasswordStrength(value);
    return state.isValid ? null : { passwordStrength: state };
  };
}
