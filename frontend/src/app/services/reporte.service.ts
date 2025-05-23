import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';

export interface Empleado {
  id: number;
  nombre: string;
  // Puedes agregar más campos si necesitas
}

export interface Reporte {
  id: number;
  empleado: Empleado;
  fecha: string;
  horaEntrada: string;
  estado: string;
  motivo?: string | null;
  nombre?: string;  // auxiliar para mostrar nombre
  hora?: string;    // auxiliar para usar en la tabla (mapeo de horaEntrada)
}


@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private baseUrl = 'http://localhost:8080'; // Cambia si tu API está en otro host/puerto

  constructor(private http: HttpClient) {}

  getReportesDesdeBackend(): Observable<Reporte[]> {
    return this.http.get<{ code: string; message: string; data: Reporte[] }>(`${this.baseUrl}/asistencia/todas`).pipe(
      map(response => response.data)
    );
  }

  //descargarReporteCumplimientoGeneral(fechaInicio: string, fechaFin: string): Observable<Blob> {
    //return this.http.get(`${this.baseUrl}/asistencia/reporteCumplimientoGeneral`, {
      //params: {
        //fechaInicio,
        //fechaFin
      //},
      //responseType: 'blob'
    //});
  //}
}
