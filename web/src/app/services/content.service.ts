import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { SiteContent } from '../models/content.model';

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private http = inject(HttpClient);
  private apiUrl = (environment?.apiUrl ?? '') + '/api/admin/content';

  getContent(): Observable<SiteContent> {
    return this.http.get<SiteContent>(this.apiUrl);
  }

  saveContent(newContent: SiteContent): Observable<boolean> {
    return this.http.put<boolean>(this.apiUrl, newContent);
  }
}
