import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface ToastMessage {
  message: string;
  type: 'success' | 'error' | 'info';
}

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.scss']
})
export class ToastComponent {
  @Input() message: ToastMessage | null = null;
  @Input() isVisible: boolean = false;

  get toastClass(): string {
    if (!this.message) return '';
    return `toast toast-${this.message.type}`;
  }

  get iconClass(): string {
    if (!this.message) return '';
    switch (this.message.type) {
      case 'success': return '✓';
      case 'error': return '✕';
      case 'info': return 'ℹ';
      default: return '';
    }
  }
}
