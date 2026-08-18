import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { User, Role } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Mock current user for frontend logic until API is fully connected
  private currentUserSubject = new BehaviorSubject<User | null>({
    id: '1',
    code: 'U001',
    name: 'Administrador (Mock)',
    role: 'Owner', // Change this to test Admin/Editor limits
    status: 'Ativo',
    lastAccess: 'Hoje'
  });

  currentUser$ = this.currentUserSubject.asObservable();

  get currentUser(): User | null {
    return this.currentUserSubject.value;
  }
  
  hasRole(roles: Role[]): boolean {
    const user = this.currentUser;
    if (!user) return false;
    return roles.includes(user.role);
  }
}
