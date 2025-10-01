// src/app/auth/login/login.component.ts

import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { StorageService } from '../services/storage.service';
import { LoadingService } from '../../shared/services/loading.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterLink
  ],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router,
    private storageService: StorageService,
    private loadingService: LoadingService
  ) {}

  ngOnInit(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }
    const { email, password } = this.loginForm.value;
    this.loadingService.show('Fazendo login...');
    this.authService.login(email, password).subscribe({
      next: (response: any) => {
        console.log('Login bem-sucedido!', response);
        this.storageService.saveToken(response.token);
        this.loadingService.hide();
        this.router.navigate(['/tasks']);
      },
      error: (err: any) => {
        console.error('Erro no login', err);
        this.loadingService.hide();
      }
    });
  }
}
