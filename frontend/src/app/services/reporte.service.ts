import { Injectable } from '@angular/core';

export interface Reporte {
  nombre: string;
  fecha: string;
  hora: string;
  estado: string;
  motivo?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReporteService {
  private reportes: Reporte[] = [];

  setReportes(data: Reporte[]): void {
    this.reportes = data;
  }

  getReportes(): Reporte[] {
    return this.reportes;
  }
}
