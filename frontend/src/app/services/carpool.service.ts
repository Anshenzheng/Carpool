import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CarpoolPost {
  id: number;
  user: any;
  departure: string;
  destination: string;
  departureTime: string;
  seats: number;
  availableSeats: number;
  contactName: string;
  contactPhone: string;
  description: string;
  status: number;
  auditStatus: number;
  auditRemark: string;
  createdAt: string;
  updatedAt: string;
}

export interface CarpoolApplication {
  id: number;
  post: CarpoolPost;
  user: any;
  applicantName: string;
  applicantPhone: string;
  passengers: number;
  status: number;
  rejectReason: string;
  confirmedAt: string;
  cancelledAt: string;
  createdAt: string;
}

export interface CreatePostRequest {
  departure: string;
  destination: string;
  departureTime: string;
  seats: number;
  contactName: string;
  contactPhone: string;
  description?: string;
}

export interface ApplyRequest {
  postId: number;
  applicantName: string;
  applicantPhone: string;
  passengers: number;
}

@Injectable({
  providedIn: 'root'
})
export class CarpoolService {
  private baseUrl = 'http://localhost:8080/api/carpool';

  constructor(private http: HttpClient) { }

  getActivePosts(): Observable<CarpoolPost[]> {
    return this.http.get<CarpoolPost[]>(`${this.baseUrl}/public/list`);
  }

  searchPosts(departure?: string, destination?: string): Observable<CarpoolPost[]> {
    let params = new HttpParams();
    if (departure) params = params.set('departure', departure);
    if (destination) params = params.set('destination', destination);
    return this.http.get<CarpoolPost[]>(`${this.baseUrl}/public/search`, { params });
  }

  getPostById(id: number): Observable<CarpoolPost> {
    return this.http.get<CarpoolPost>(`${this.baseUrl}/public/${id}`);
  }

  createPost(request: CreatePostRequest): Observable<CarpoolPost> {
    return this.http.post<CarpoolPost>(`${this.baseUrl}/posts`, request);
  }

  getMyPosts(): Observable<CarpoolPost[]> {
    return this.http.get<CarpoolPost[]>(`${this.baseUrl}/posts/my`);
  }

  cancelMyPost(id: number): Observable<CarpoolPost> {
    return this.http.put<CarpoolPost>(`${this.baseUrl}/posts/${id}/cancel`, {});
  }

  applyForCarpool(request: ApplyRequest): Observable<CarpoolApplication> {
    return this.http.post<CarpoolApplication>(`${this.baseUrl}/applications`, request);
  }

  getMyApplications(): Observable<CarpoolApplication[]> {
    return this.http.get<CarpoolApplication[]>(`${this.baseUrl}/applications/my`);
  }

  getReceivedApplications(): Observable<CarpoolApplication[]> {
    return this.http.get<CarpoolApplication[]>(`${this.baseUrl}/applications/received`);
  }

  confirmApplication(id: number): Observable<CarpoolApplication> {
    return this.http.put<CarpoolApplication>(`${this.baseUrl}/applications/${id}/confirm`, {});
  }

  rejectApplication(id: number, rejectReason?: string): Observable<CarpoolApplication> {
    return this.http.put<CarpoolApplication>(`${this.baseUrl}/applications/${id}/reject`, { rejectReason });
  }

  cancelMyApplication(id: number): Observable<CarpoolApplication> {
    return this.http.put<CarpoolApplication>(`${this.baseUrl}/applications/${id}/cancel`, {});
  }
}
