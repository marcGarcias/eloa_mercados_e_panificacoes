export interface User {
  name: string;
  code: string;
  role: 'admin' | 'editor';
  status: 'ativo' | 'inativo';
  access: string;
  password?: string;
}
