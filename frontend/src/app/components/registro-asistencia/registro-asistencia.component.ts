import { Component, OnInit } from '@angular/core';
import { RegistroAsistenciaService } from '../../services/registro-asistencia.service';

interface Reporte {
  nombre: string;
  fecha: string;
  hora: string;
  estado: 'Presente' | 'Tarde' | 'Ausente';
  motivo?: string;
}

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

  constructor(private registroAsistenciaService: RegistroAsistenciaService) {}

  ngOnInit(): void {
    this.actualizarRegistros();
  }

  actualizarRegistros(): void {
    this.registroAsistenciaService.getRegistros().subscribe({
      next: (datos) => {
        this.registros = datos.sort((a, b) => {
          const fechaHoraA = new Date(`${a.fecha}T${a.hora}`);
          const fechaHoraB = new Date(`${b.fecha}T${b.hora}`);
          return fechaHoraB.getTime() - fechaHoraA.getTime();
        });
      },
      error: (error) => {
        console.error('Error obteniendo registros:', error);
      }
    });
  }

  onEstadoChange(): void {
    this.mostrarMotivo = this.nuevoEstado === 'Tarde' || this.nuevoEstado === 'Ausente';
    if (!this.mostrarMotivo) {
      this.nuevoMotivo = '';
    }
  }

  registrarAsistencia(): void {
    if (!this.nuevoNombre.trim()) {
      alert('Por favor, ingrese el nombre.');
      return;
    }
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

    this.registroAsistenciaService.registrarAsistencia(nuevoRegistro).subscribe({
      next: () => {
        alert('Asistencia registrada correctamente');
        this.nuevoNombre = '';
        this.nuevoEstado = 'Presente';
        this.nuevoMotivo = '';
        this.mostrarMotivo = false;
        this.actualizarRegistros();
      },
      error: (error) => {
        console.error('Error al registrar asistencia:', error);
        alert('Error al registrar asistencia');
      }
    });
  }
}
