import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private loggedInKey = 'isLoggedIn';
  private tokenKey = 'jwtToken';

  constructor() {}

  login(): void {
    localStorage.setItem(this.loggedInKey, 'true');
  }

  notifyLogin(): void {
    this.login();
  }

  logout(): void {
    localStorage.removeItem(this.loggedInKey);
    localStorage.removeItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const token = localStorage.getItem(this.tokenKey);
    if (!token) return false;
    
    try {
      // Decodifica el payload del JWT (parte central en base64)
      const payload = JSON.parse(atob(token.split('.')[1]));
      const now = Math.floor(Date.now() / 1000);
      return payload.exp > now; // válido solo si no expiró
    } catch {
      return false;
    }
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  setToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }
}
