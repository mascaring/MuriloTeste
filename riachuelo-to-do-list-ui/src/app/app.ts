import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastComponent } from './shared/components/toast/toast.component';
import { LoadingComponent } from './shared/components/loading/loading.component';
import { ToastService } from './shared/services/toast.service';
import { LoadingService } from './shared/services/loading.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, ToastComponent, LoadingComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  title = 'riachuelo-to-do-list';
  
  toastMessage$: Observable<any>;
  isToastVisible$: Observable<boolean>;
  isLoading$: Observable<boolean>;
  loadingMessage$: Observable<string>;

  constructor(
    private toastService: ToastService,
    private loadingService: LoadingService
  ) {
    this.toastMessage$ = this.toastService.getToast();
    this.isToastVisible$ = this.toastService.getVisibility();
    this.isLoading$ = this.loadingService.isLoading$;
    this.loadingMessage$ = this.loadingService.message$;
  }
}
