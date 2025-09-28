import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface ToastMessage {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  private toastSubject = new BehaviorSubject<ToastMessage | null>(null);
  private isVisibleSubject = new BehaviorSubject<boolean>(false);

  getToast(): Observable<ToastMessage | null> {
    return this.toastSubject.asObservable();
  }

  getVisibility(): Observable<boolean> {
    return this.isVisibleSubject.asObservable();
  }

  showSuccess(message: string): void {
    this.showToast({ message, type: 'success' });
  }

  showError(message: string): void {
    this.showToast({ message, type: 'error' });
  }

  showInfo(message: string): void {
    this.showToast({ message, type: 'info' });
  }

  private showToast(toast: ToastMessage): void {
    this.toastSubject.next(toast);
    this.isVisibleSubject.next(true);

    setTimeout(() => {
      this.hideToast();
    }, 4000);
  }

  hideToast(): void {
    this.isVisibleSubject.next(false);
    setTimeout(() => {
      this.toastSubject.next(null);
    }, 300);
  }
}
