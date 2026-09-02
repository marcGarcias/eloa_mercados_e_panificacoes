import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, CreateUserPayload, UpdateUserPayload } from '../models/user.model';
import { SpringPage } from '../models/page.model';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = (environment?.apiUrl ?? '') + '/api/admin/users';

  getAll(page = 0, size = 10): Observable<SpringPage<User>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    // O backend será implementado depois, mas já preparamos para suportar SpringPage
    return this.http.get<SpringPage<User>>(this.apiUrl, { params });
  }

  create(user: CreateUserPayload): Observable<void> {
    return this.http.post<void>(this.apiUrl, user);
  }

  updateData(id: string, user: UpdateUserPayload): Observable<void> {
    // PATCH endpoint for data update
    return this.http.patch<void>(`${this.apiUrl}/${id}`, {
      name: user.name,
      role: user.role,
      status: user.status
    });
  }

  changePassword(id: string, newPassword: string): Observable<void> {
    // PUT endpoint for password update
    return this.http.put<void>(`${this.apiUrl}/${id}/password`, { newPassword });
  }

  delete(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
