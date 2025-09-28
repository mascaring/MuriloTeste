import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/services/auth.service';
import { StorageService } from '../../../auth/services/storage.service';
import { ToastService } from '../../services/toast.service';

@Component({
  selector: 'app-top-menu',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './top-menu.component.html',
  styleUrls: ['./top-menu.component.scss']
})
export class TopMenuComponent {
  userName: string = '';

  constructor(
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router,
    private toastService: ToastService
  ) {
    this.loadUserInfo();
  }

  private loadUserInfo(): void {
    const userEmail = this.storageService.getUserEmail();
    this.userName = userEmail || 'Usuário';
  }

  logout(): void {
    this.authService.logout();
    this.toastService.showSuccess('Logout realizado com sucesso!');
    this.router.navigate(['/auth/login']);
  }
}
