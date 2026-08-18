export type Role = 'Owner' | 'Admin' | 'Editor';

export interface User {
  id: string;
  code: string;
  name: string;
  role: Role;
  status: 'Ativo' | 'Inativo';
  lastAccess: string;
  password?: string;
}
