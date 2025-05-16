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

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.actualizarRegistros();
  }

  actualizarRegistros(): void {
    const datos = this.reporteService.getReportes();

    // Ordenar por fecha y hora descendente
    this.registros = [...datos].sort((a, b) => {
      const fechaHoraA = new Date(`${a.fecha}T${a.hora}`);
      const fechaHoraB = new Date(`${b.fecha}T${b.hora}`);
      return fechaHoraB.getTime() - fechaHoraA.getTime();
    });
  }

  registrarAsistencia(): void {
    if (!this.nuevoNombre.trim()) return;

    const ahora = new Date();
    const nuevoRegistro: Reporte = {
      nombre: this.nuevoNombre.trim(),
      fecha: ahora.toISOString().slice(0, 10),
      hora: ahora.toTimeString().slice(0, 5),
      estado: this.nuevoEstado
    };

    // Guardar en el servicio
    const actuales = this.reporteService.getReportes();
    this.reporteService.setReportes([...actuales, nuevoRegistro]);

    // Limpiar formulario y actualizar lista
    this.nuevoNombre = '';
    this.nuevoEstado = 'Presente';
    this.actualizarRegistros();
  }
}
