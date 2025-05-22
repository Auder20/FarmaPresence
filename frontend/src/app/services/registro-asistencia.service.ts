import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Interface para envío de datos al backend
export interface NuevaAsistencia {
  empleado: {id: number};
  fecha: string;
  horaEntrada: string;
  estado: 'Presente' | 'Tarde' | 'Ausente';
  motivo?: string | null;
  tipoRegistro: string;
}

// Interface para registros de asistencia recibidos
export interface Reporte {
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
  // URL para el registro manual en controlador asistenciaManual
private urlRegistrarManual = 'http://localhost:8080/asistencia/manual/registrarIngreso';

  // URL para obtener registros y para la huella en controlador asistenciaHuella
  private urlRegistrosHuella = 'http://localhost:8080/asistenciaHuella/todas';
  private urlRegistrarHuella = 'http://localhost:8080/asistenciaHuella/entrada';

  constructor(private http: HttpClient) {}

  // Obtener registros (GET) - desde controlador huella
  getRegistros(): Observable<Reporte[]> {
    return this.http.get<Reporte[]>(this.urlRegistrosHuella);
  }

  // Registrar nueva asistencia (POST) - desde controlador manual
  registrarAsistenciaManual(registro: NuevaAsistencia): Observable<any> {
    return this.http.post(this.urlRegistrarManual, registro);
  }

  // Registrar entrada por huella (POST), enviando la huella como parámetro en la URL
  registrarEntradaHuella(huella: string): Observable<any> {
    return this.http.post(`${this.urlRegistrarHuella}/${huella}`, null);
  }
}
