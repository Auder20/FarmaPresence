import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

interface Reporte {
  nombre: string;
  fecha: string;
  hora: string;
  estado: 'Presente' | 'Tarde' | 'Ausente';
  motivo?: string;
}

@Injectable({
  providedIn: 'root'
})
export class RegistroAsistenciaService {

  private apiUrl = 'http://localhost:8080/asistencia';  // Cambia a la URL correcta del backend

  constructor(private http: HttpClient) {}

  getRegistros(): Observable<Reporte[]> {
    return this.http.get<Reporte[]>(this.apiUrl);
  }

  registrarAsistencia(registro: Reporte): Observable<any> {
    return this.http.post(this.apiUrl, registro);
  }
}
