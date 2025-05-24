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
  nuevoEstado: 'Presente' | 'Tarde' | 'Ausente' | null = null;
  ausenteSeleccionado: boolean = false;
  nuevoMotivo: string = '';
  mostrarMotivo: boolean = false;
  esperandoMotivo: boolean = false; // controla si espera motivo para tardanza

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
    this.nuevoEstado = this.ausenteSeleccionado ? 'Ausente' : null;
    this.mostrarMotivo = this.ausenteSeleccionado;

    if (!this.mostrarMotivo) {
      this.nuevoMotivo = '';
      this.esperandoMotivo = false;
    }
  }

  validarAntesDeRegistrar(): void {
    if (this.nuevoEmpleadoId === null) {
      alert('Por favor, selecciona un empleado');
      return;
    }

    if (this.ausenteSeleccionado) {
      this.nuevoEstado = 'Ausente';
      this.mostrarMotivo = true;

      if (!this.nuevoMotivo.trim()) {
        alert('Debe ingresar el motivo para el estado Ausente.');
        return;
      }
      this.registrarAsistencia();
      return;
    }

    if (this.esperandoMotivo) {
      if (!this.nuevoMotivo.trim()) {
        alert('Debe ingresar el motivo para el estado Tarde.');
        return;
      } else {
        this.registrarAsistencia();
        return;
      }
    }

    this.registroAsistenciaService.evaluarHoraEntrada(this.nuevoEmpleadoId).subscribe(
      (response) => {
        this.nuevoEstado = response.estado as 'Presente' | 'Tarde';

        if (this.nuevoEstado === 'Tarde') {
          this.mostrarMotivo = true;
          this.esperandoMotivo = true;
          alert('Empleado llegó tarde: se requiere ingresar motivo antes de registrar.');
        } else {
          this.mostrarMotivo = false;
          this.esperandoMotivo = false;
          this.registrarAsistencia();
        }
      },
      (error) => {
        alert('Error al validar hora de entrada');
        console.error(error);
      }
    );
  }

  registrarAsistencia(): void {
    if (this.nuevoEmpleadoId === null) {
      alert('Por favor, selecciona un empleado');
      return;
    }

    if ((this.nuevoEstado === 'Tarde' || this.nuevoEstado === 'Ausente') && !this.nuevoMotivo.trim()) {
      alert('Debe ingresar el motivo para este estado.');
      return;
    }

    const ahora = new Date();
    const nuevoRegistro: NuevaAsistencia = {
      empleado: { id: this.nuevoEmpleadoId },
      fecha: ahora.toISOString().slice(0, 10),
      horaEntrada: ahora.toTimeString().slice(0, 8),
      motivo: this.mostrarMotivo ? this.nuevoMotivo.trim() : null,
      tipoRegistro: 'ENTRADA_1',
      estado: this.nuevoEstado!
    };

    this.registroAsistenciaService.registrarAsistenciaManual(nuevoRegistro).subscribe(
      (respuesta) => {
        alert('Asistencia registrada con éxito');

        const estadoFinal = respuesta?.data?.estado || nuevoRegistro.estado;
        const empleadoNombre = this.empleados.find(e => e.id === this.nuevoEmpleadoId)?.nombre || 'Desconocido';

        this.asistenciasSesion.push({
          nombre: empleadoNombre,
          fecha: nuevoRegistro.fecha,
          hora: nuevoRegistro.horaEntrada,
          estado: estadoFinal!,
          motivo: nuevoRegistro.motivo || ''
        });

        // Resetear formulario
        this.nuevoEmpleadoId = null;
        this.nuevoEstado = null;
        this.ausenteSeleccionado = false;
        this.nuevoMotivo = '';
        this.mostrarMotivo = false;
        this.esperandoMotivo = false;
      },
      error => {
        alert('Error al registrar asistencia, intenta nuevamente');
        console.error(error);
      }
    );
  }

  limpiarTabla(): void {
    this.asistenciasSesion = [];
  }
}
