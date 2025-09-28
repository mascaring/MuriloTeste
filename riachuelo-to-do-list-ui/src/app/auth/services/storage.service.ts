import { Injectable } from '@angular/core';
import { JwtService, JwtPayload } from './jwt.service';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly TOKEN_KEY = 'auth_token';

  constructor(private jwtService: JwtService) { }

  saveToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    return !this.jwtService.isTokenExpired(token);
  }

  getUserEmail(): string | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    return this.jwtService.getUserEmail(token);
  }

  getUserId(): number {
    const token = this.getToken();
    if (!token) {
      return 1;
    }

    return this.jwtService.getUserId(token);
  }

  getDecodedToken(): JwtPayload | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    return this.jwtService.decodeToken(token);
  }
}
