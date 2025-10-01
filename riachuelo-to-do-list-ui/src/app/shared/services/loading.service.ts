import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  private messageSubject = new BehaviorSubject<string>('Carregando...');

  get isLoading$(): Observable<boolean> {
    return this.loadingSubject.asObservable();
  }

  get message$(): Observable<string> {
    return this.messageSubject.asObservable();
  }

  show(message: string = 'Carregando...'): void {
    this.messageSubject.next(message);
    this.loadingSubject.next(true);
  }

  hide(): void {
    this.loadingSubject.next(false);
  }

  setMessage(message: string): void {
    this.messageSubject.next(message);
  }
}
