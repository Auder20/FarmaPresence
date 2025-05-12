import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistroEmpleadosService {

  private apiUrl = 'http://localhost:8080/empleado'; // Ajusta la URL según sea necesario
  private apiUrlRegsitrarEmpeleado = 'http://localhost:8080/empleado/registrar'; // Cambia la URL al endpoint correcto de tu backend
  private apiUrlHorarios = 'http://localhost:8080/horarios'; // URL para horarios
  private apiUrlAsistencia = 'http://localhost:8080/asistencia'; // URL para asistencia

  constructor(private http: HttpClient) {}

  updateEmpleado(huella: string, empleado: any): Observable<any> {
    const url = `${this.apiUrlRegsitrarEmpeleado}/${huella}`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post(url, empleado, { headers });
  }

  updateEmpleadoByIdentificacion(identificacion: string, empleado: any): Observable<any> {
    const url = `${this.apiUrl}/actualizar/${identificacion}`;
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put(url, empleado, { headers });
  }

  getAllHuellas(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/huellas`);
  }

  getAllEmpleados(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/all`);
  }

  getAllHorarios(): Observable<any> {
    return this.http.get<any>(`${this.apiUrlHorarios}/all`);
  }

  getReporteComparativoGrafica(mes: number, anio: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrlAsistencia}/reporteComparativo/grafica?mes=${mes}&anio=${anio}`);
  }
}
