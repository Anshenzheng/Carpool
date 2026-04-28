import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface AuditRequest {
  postId: number;
  auditStatus: number;
  auditRemark?: string;
}

export interface OverviewStats {
  totalPosts: number;
  activePosts: number;
  totalApplications: number;
  confirmedApplications: number;
  todayPosts: number;
  todayApplications: number;
  monthPosts: number;
  monthApplications: number;
  completionRate: string;
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private baseUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) { }

  getPendingAuditPosts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/posts/pending`);
  }

  getAllPosts(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/posts/all`);
  }

  auditPost(request: AuditRequest): Observable<any> {
    return this.http.post(`${this.baseUrl}/posts/audit`, request);
  }

  takeDownPost(id: number, reason?: string): Observable<any> {
    return this.http.put(`${this.baseUrl}/posts/${id}/take-down`, { reason });
  }

  getOverviewStatistics(): Observable<OverviewStats> {
    return this.http.get<OverviewStats>(`${this.baseUrl}/statistics/overview`);
  }

  getRouteStatistics(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/statistics/routes`);
  }

  exportPosts(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/statistics/export/posts`, { responseType: 'blob' });
  }

  exportApplications(): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/statistics/export/applications`, { responseType: 'blob' });
  }
}
