import { Injectable } from '@angular/core';

export interface JwtPayload {
  sub: string;
  iat: number;
  exp: number;
  userId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class JwtService {

  constructor() { }
  decodeToken(token: string): JwtPayload | null {
    try {
      const parts = token.split('.');
      if (parts.length !== 3) {
        return null;
      }

      const payload = parts[1];
      const decodedPayload = atob(payload.replace(/-/g, '+').replace(/_/g, '/'));

      return JSON.parse(decodedPayload);
    } catch (error) {
      console.error('Erro ao decodificar token JWT:', error);
      return null;
    }
  }

  getUserEmail(token: string): string | null {
    const payload = this.decodeToken(token);
    return payload ? payload.sub : null;
  }

  getUserId(token: string): number {
    const payload = this.decodeToken(token);
    if (payload && payload.userId) {
      return payload.userId;
    }

    const email = this.getUserEmail(token);
    if (email) {
      return this.hashString(email) % 1000 + 1;
    }

    return 1;
  }

  /**
   * Verifica se o token está expirado
   */
  isTokenExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload) {
      return true;
    }

    const currentTime = Math.floor(Date.now() / 1000);
    return payload.exp < currentTime;
  }

  /**
   * Função auxiliar para gerar hash de string
   */
  private hashString(str: string): number {
    let hash = 0;
    for (let i = 0; i < str.length; i++) {
      const char = str.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return Math.abs(hash);
  }
}
