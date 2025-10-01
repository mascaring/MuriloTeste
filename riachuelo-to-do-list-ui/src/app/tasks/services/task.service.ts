import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from '../../auth/services/storage.service';

export interface Task {
  id?: number;
  title: string;
  description?: string;
  status: 'TO_DO' | 'IN_PROGRESS' | 'DONE';
  dueDate: string;
  userId: number;
  userName?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface TaskFilters {
  status?: string;
  title?: string;
  dueDate?: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private apiUrl = 'http://localhost:8080/tasks';

  constructor(
    private http: HttpClient,
    private storageService: StorageService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.storageService.getToken();
    return new HttpHeaders({
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    });
  }

  getTasks(): Observable<Task[]> {
    return this.http.get<Task[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  searchTasks(filters: TaskFilters): Observable<Task[]> {
    let params = new HttpParams();
    
    if (filters.status) {
      params = params.set('status', filters.status);
    }
    
    if (filters.title) {
      params = params.set('title', filters.title);
    }
    
    if (filters.dueDate) {
      params = params.set('dueDate', filters.dueDate);
    }
    
    if (filters.createdAt) {
      params = params.set('createdAt', filters.createdAt);
    }
    
    return this.http.get<Task[]>(`${this.apiUrl}/search`, { 
      headers: this.getHeaders(),
      params: params
    });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }

  createTask(task: Task): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task, { headers: this.getHeaders() });
  }

  updateTask(id: number, task: Partial<Task>): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task, { headers: this.getHeaders() });
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.getHeaders() });
  }
}