import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { StorageService } from '../services/storage.service';
import { ToastService } from '../../shared/services/toast.service';
import { LoadingService } from '../../shared/services/loading.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private storageService: StorageService,
    private router: Router,
    private toastService: ToastService,
    private loadingService: LoadingService
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.registerForm.valid) {
      const { name, email, password } = this.registerForm.value;
      
      this.loadingService.show('Criando conta...');
      this.authService.register(name, email, password).subscribe({
        next: (response: any) => {
          this.storageService.saveToken(response.token);
          this.toastService.showSuccess('Registro realizado com sucesso!');
          this.loadingService.hide();
          this.router.navigate(['/tasks']);
        },
        error: (err: any) => {
          console.error('Erro no registro:', err);
          this.toastService.showError('Erro ao realizar registro');
          this.loadingService.hide();
        }
      });
    }
  }
}
