import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RegistroEmpleadosService {

  private apiUrl = 'https://prueba-ylpz.onrender.com/empleado'; // Ajusta la URL según sea necesario
  private apiUrlRegsitrarEmpeleado = 'https://prueba-ylpz.onrender.com/empleado/registrar'; // Cambia la URL al endpoint correcto de tu backend
  private apiUrlHorarios = 'https://prueba-ylpz.onrender.com/horarios'; // URL para horarios
  private apiUrlAsistencia = 'https://prueba-ylpz.onrender.com/asistencia'; // URL para asistencia
  private apiUrlRegistrarhuella = 'https://prueba-ylpz.onrender.com/empleado/registrarHuella';
  private urlRegistrarHuella = 'https://prueba-ylpz.onrender.com/asistencia/huella/entrada';


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
  updateHuella(huella: string, empleado: any): Observable<any> {
  const url = `${this.apiUrlRegistrarhuella}/${huella}`;
  const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
  return this.http.post(url, empleado, { headers });
}
registrarEntradaHuella(huella: string): Observable<any> {
  return this.http.post(`${this.urlRegistrarHuella}/${huella}`, null);
}

}
