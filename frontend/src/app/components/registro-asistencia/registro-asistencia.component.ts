import { Component, OnInit } from '@angular/core';
import { RegistroAsistenciaService, NuevaAsistencia, Reporte } from '../../services/registro-asistencia.service';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';

@Component({
  selector: 'app-registro-asistencia',
  templateUrl: './registro-asistencia.component.html',
  styleUrls: ['./registro-asistencia.component.css']
})
export class RegistroAsistenciaComponent implements OnInit {
  empleados: any[] = [];
  nuevoEmpleadoId: number | null = null;
  nuevoEstado: 'Presente' | 'Tarde' | 'Ausente' = 'Presente';
  nuevoMotivo: string = '';
  mostrarMotivo: boolean = false;

  // Lista local para asistencias registradas en esta sesión
  asistenciasSesion: Reporte[] = [];

  constructor(
    private registroAsistenciaService: RegistroAsistenciaService,
    private registroEmpleadosService: RegistroEmpleadosService
  ) {}

  ngOnInit(): void {
    this.cargarEmpleados();
  }

  cargarEmpleados(): void {
    this.registroEmpleadosService.getAllEmpleados().subscribe(
      response => {
        if (response && response.data) {
          this.empleados = response.data;
        }
      },
      error => console.error('Error al cargar empleados', error)
    );
  }

  onEstadoChange(): void {
    this.mostrarMotivo = this.nuevoEstado === 'Tarde' || this.nuevoEstado === 'Ausente';
    if (!this.mostrarMotivo) {
      this.nuevoMotivo = '';
    }
  }

  registrarAsistencia(): void {
    if (this.nuevoEmpleadoId === null) {
      alert('Por favor, selecciona un empleado');
      return;
    }
    if (this.mostrarMotivo && !this.nuevoMotivo.trim()) {
      alert('Por favor, ingrese el motivo para el estado seleccionado.');
      return;
    }

    const ahora = new Date();

    const nuevoRegistro: NuevaAsistencia = {
      empleado: {id: this.nuevoEmpleadoId},
      fecha: ahora.toISOString().slice(0, 10),
      horaEntrada: ahora.toTimeString().slice(0, 8),
      estado: this.nuevoEstado,
      motivo: this.mostrarMotivo ? this.nuevoMotivo.trim() : null,
      tipoRegistro: 'ENTRADA_1'
    };

    this.registroAsistenciaService.registrarAsistenciaManual(nuevoRegistro).subscribe(
      () => {
        alert('Asistencia registrada con éxito');

        // Agregar asistencia a la lista local para mostrar en la tabla
        const empleadoNombre = this.empleados.find(e => e.id === this.nuevoEmpleadoId)?.nombre || 'Desconocido';
        this.asistenciasSesion.push({
          nombre: empleadoNombre,
          fecha: nuevoRegistro.fecha,
          hora: nuevoRegistro.horaEntrada,
          estado: nuevoRegistro.estado,
          motivo: nuevoRegistro.motivo || ''
        });

        // Resetear formulario
        this.nuevoEmpleadoId = null;
        this.nuevoEstado = 'Presente';
        this.nuevoMotivo = '';
        this.mostrarMotivo = false;
      },
      error => {
        alert('Error al registrar asistencia, intenta nuevamente');
        console.error(error);
      }
    );
  }

  // Método para limpiar la tabla local (sin afectar la base de datos)
  limpiarTabla(): void {
    this.asistenciasSesion = [];
  }
}
