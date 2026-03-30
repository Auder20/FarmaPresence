import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

// Interface para envío de datos al backend
export interface NuevaAsistencia {
  empleado: {id: number};
  fecha: string;
  horaEntrada: string;
  estado?: 'Presente' | 'Tarde' | 'Ausente';
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
private baseUrl = environment.apiUrl;
private urlRegistrarManual = this.baseUrl + '/asistencia/manual/registrarIngreso';
private urlEvaluarEntrada = this.baseUrl + '/asistencia/manual/evaluar-hora-entrada';

  // URL para obtener registros y para la huella en controlador asistenciaHuella
  private urlRegistrosHuella = this.baseUrl + '/asistenciaHuella/todas';
  private urlRegistrarHuella = this.baseUrl + '/asistenciaHuella/entrada';


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
  // Metodo para evaluar la hora de entrada
  evaluarHoraEntrada(empleadoId: number): Observable<{ estado: string, diferencia: string }> {
  return this.http.get<{ estado: string, diferencia: string }>(
    `${this.urlEvaluarEntrada}/${empleadoId}`
  );
}

}
