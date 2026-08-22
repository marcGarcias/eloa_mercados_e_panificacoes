export interface User {
  name: string;
  email: string;
  cpf: string;
  role: 'admin' | 'editor';
  status: 'ativo' | 'inativo';
  access: string;
  password?: string;
}
