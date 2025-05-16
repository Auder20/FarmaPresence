import { Component, OnInit } from '@angular/core';
import { ReporteService, Reporte } from '../../services/reporte.service';

@Component({
  selector: 'app-registro-asistencia',
  templateUrl: './registro-asistencia.component.html',
  styleUrls: ['./registro-asistencia.component.css']
})
export class RegistroAsistenciaComponent implements OnInit {
  registros: Reporte[] = [];
  nuevoNombre: string = '';
  nuevoEstado: 'Presente' | 'Tarde' | 'Ausente' = 'Presente';
  nuevoMotivo: string = '';
  mostrarMotivo: boolean = false;

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.actualizarRegistros();
  }

  actualizarRegistros(): void {
    const datos = this.reporteService.getReportes();

    this.registros = [...datos].sort((a, b) => {
      const fechaHoraA = new Date(`${a.fecha}T${a.hora}`);
      const fechaHoraB = new Date(`${b.fecha}T${b.hora}`);
      return fechaHoraB.getTime() - fechaHoraA.getTime();
    });
  }

  onEstadoChange(): void {
    this.mostrarMotivo = this.nuevoEstado === 'Tarde' || this.nuevoEstado === 'Ausente';
    if (!this.mostrarMotivo) {
      this.nuevoMotivo = '';
    }
  }

  registrarAsistencia(): void {
    if (!this.nuevoNombre.trim()) return;
    if (this.mostrarMotivo && !this.nuevoMotivo.trim()) {
      alert('Por favor, ingrese el motivo para el estado seleccionado.');
      return;
    }

    const ahora = new Date();
    const nuevoRegistro: Reporte = {
      nombre: this.nuevoNombre.trim(),
      fecha: ahora.toISOString().slice(0, 10),
      hora: ahora.toTimeString().slice(0, 5),
      estado: this.nuevoEstado,
      motivo: this.nuevoMotivo.trim() || undefined
    };

    const actuales = this.reporteService.getReportes();
    this.reporteService.setReportes([...actuales, nuevoRegistro]);

    this.nuevoNombre = '';
    this.nuevoEstado = 'Presente';
    this.nuevoMotivo = '';
    this.mostrarMotivo = false;

    this.actualizarRegistros();
  }
}
