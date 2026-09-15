import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export interface PasswordRulesState {
  minLength: boolean;
  hasUpperCase: boolean;
  hasLowerCase: boolean;
  hasSpecialChar: boolean;
  isValid: boolean;
  length: number;
  remainingChars: number;
  missingUpperCase: boolean;
  missingLowerCase: boolean;
  missingSpecialChar: boolean;
}

export function checkPasswordStrength(password: string | null | undefined): PasswordRulesState {
  const pwd = password || '';
  const length = pwd.length;
  const minLength = length >= 8;
  const hasUpperCase = /[A-Z]/.test(pwd);
  const hasLowerCase = /[a-z]/.test(pwd);
  const hasSpecialChar = /[^a-zA-Z0-9]/.test(pwd);
  const isValid = minLength && hasUpperCase && hasLowerCase && hasSpecialChar;
  const remainingChars = Math.max(0, 8 - length);

  return {
    minLength,
    hasUpperCase,
    hasLowerCase,
    hasSpecialChar,
    isValid,
    length,
    remainingChars,
    missingUpperCase: !hasUpperCase,
    missingLowerCase: !hasLowerCase,
    missingSpecialChar: !hasSpecialChar
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
