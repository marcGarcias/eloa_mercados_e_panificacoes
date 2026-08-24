export type UserRole = 'SUPER_ADMIN' | 'ADMIN' | 'EDITOR';
export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface User {
  id: string;
  userCode: string;
  name: string;
  role: UserRole;
  status: UserStatus;
  lastLoginAt?: string;
  password?: string;
}

// Helpers para tradução na UI
export const RoleTranslations: Record<UserRole, string> = {
  'SUPER_ADMIN': 'Proprietário',
  'ADMIN': 'Administrador',
  'EDITOR': 'Editor'
};

export const StatusTranslations: Record<UserStatus, string> = {
  'ACTIVE': 'Ativo',
  'INACTIVE': 'Inativo'
};
