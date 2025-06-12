import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Empleado {
  id: number;
  nombre?: string;
}

export interface TurnoProgramado {
  id: number;
  empleado: Empleado;
  fecha: string;
  horaInicio: string;  // camelCase
  horaFin: string;     // camelCase
}

export interface Horario {
  id: number;
  descripcion: string;
  horaInicio1: string;
  horaFin1: string;
  horaInicio2: string;
  horaFin2: string;
}

@Injectable({
  providedIn: 'root'
})
export class TurnoProgramadoService {
  private baseUrl = 'https://prueba-ylpz.onrender.com/turnoProgramado';

  constructor(private http: HttpClient) {}

  getTurnos(): Observable<TurnoProgramado[]> {
    return this.http.get<{ code: string; message: string; data: TurnoProgramado[] }>(
      `${this.baseUrl}/turnos`
    ).pipe(map(response => response.data));
  }

  agregarTurno(turno: Partial<TurnoProgramado>): Observable<TurnoProgramado> {
    // Asegúrate de enviar las propiedades con camelCase
    return this.http.post<{ code: string; message: string; data: TurnoProgramado }>(
      `${this.baseUrl}/asignar`,
      turno
    ).pipe(map(response => response.data));
  }

  getHorarios(): Observable<Horario[]> {
    return this.http.get<{ code: string; message: string; data: Horario[] }>(
      `${this.baseUrl}/horarios`
    ).pipe(map(response => response.data));
  }

  // Método eliminar no implementado en backend, por ahora lo dejamos fuera
}
