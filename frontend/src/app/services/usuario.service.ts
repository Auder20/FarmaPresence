import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private apiUrl = environment.apiUrl + '/usuario';

  private httpOptions = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' })
  };

  constructor(private http: HttpClient) {}

  getAllUsuarios(): Observable<any> {
    return this.http.get<any>(this.apiUrl);
  }

  addUsuario(usuario: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, usuario, this.httpOptions);
  }

  updateUsuario(id: number | string, usuario: any): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<any>(url, usuario, this.httpOptions);
  }

  getUsuarioById(id: number | string): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<any>(url);
  }

  deleteUsuario(id: number | string): Observable<any> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<any>(url);
  }

  // Método nuevo para actualizar la contraseña con validación de la actual
  updatePassword(id: number | string, passwords: { contrasenaActual: string; nuevaContrasena: string }): Observable<any> {
    const url = `${this.apiUrl}/change-password/${id}`;
    return this.http.put<any>(url, passwords, this.httpOptions);
  }
}
