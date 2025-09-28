import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastComponent } from './shared/components/toast/toast.component';
import { ToastService } from './shared/services/toast.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ToastComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = 'riachuelo-to-do-list';
  
  toastMessage$: Observable<any>;
  isToastVisible$: Observable<boolean>;

  constructor(private toastService: ToastService) {
    this.toastMessage$ = this.toastService.getToast();
    this.isToastVisible$ = this.toastService.getVisibility();
  }
}
