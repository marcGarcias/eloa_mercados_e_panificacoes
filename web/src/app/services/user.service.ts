import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private users: User[] = [
    { name: 'Marcelo Garcia', email: 'marcelo@eloa.com.br', cpf: '123.456.789-00', password: 'Padoca#2026', role: 'admin', status: 'ativo', access: 'Hoje, 09:14' },
    { name: 'Eloá Ferreira', email: 'eloa@eloa.com.br', cpf: '234.567.890-11', password: 'Eloa!Admin1', role: 'admin', status: 'ativo', access: 'Ontem, 18:40' },
    { name: 'Renata Souza', email: 'renata@eloa.com.br', cpf: '345.678.901-22', password: 'Renata@321', role: 'editor', status: 'inativo', access: '02/07, 11:05' },
  ];

  getAll(): Observable<User[]> {
    return of([...this.users]);
  }
}
