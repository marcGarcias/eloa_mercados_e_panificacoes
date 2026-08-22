import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { User } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class UserService {
  private users: User[] = [
    { name: 'Marcelo Garcia', code: '1001', password: 'Padoca#2026', role: 'admin', status: 'ativo', access: 'Hoje, 09:14' },
    { name: 'Eloá Ferreira', code: '1002', password: 'Eloa!Admin1', role: 'admin', status: 'ativo', access: 'Ontem, 18:40' },
    { name: 'Renata Souza', code: '1003', password: 'Renata@321', role: 'editor', status: 'inativo', access: '02/07, 11:05' },
  ];

  getAll(): Observable<User[]> {
    return of([...this.users]);
  }
}
