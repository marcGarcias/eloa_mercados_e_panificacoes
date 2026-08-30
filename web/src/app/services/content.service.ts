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

  getContentPublic(): Observable<SiteContent> {
    const publicUrl = (environment?.apiUrl ?? '') + '/api/public/content';
    return this.http.get<SiteContent>(publicUrl);
  }

  saveContent(newContent: SiteContent): Observable<boolean> {
    return this.http.patch<boolean>(this.apiUrl, newContent);
  }
}
