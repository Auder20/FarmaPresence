import { Component, OnInit } from '@angular/core';
import { TurnoProgramadoService, TurnoProgramado, Empleado } from '../../services/turno-programado.service';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';

@Component({
  selector: 'app-turno-programado',
  templateUrl: './turno-programado.component.html',
  styleUrls: ['./turno-programado.component.css'],
})
export class TurnoProgramadoComponent implements OnInit {
  turnos: TurnoProgramado[] = [];
  empleados: Empleado[] = [];

  nuevoTurno: {
    empleado: Empleado | undefined;
    fecha: string;
    horaInicio: string;  // Cambiado para coincidir con backend
    horaFin: string;     // Cambiado para coincidir con backend
  } = {
    empleado: undefined,
    fecha: '',
    horaInicio: '',
    horaFin: ''
  };

  constructor(
    private turnoService: TurnoProgramadoService,
    private empleadosService: RegistroEmpleadosService
  ) {}

  ngOnInit(): void {
    this.cargarEmpleadosYTurnos();
  }

  cargarEmpleadosYTurnos(): void {
    this.empleadosService.getAllEmpleados().subscribe(
      (Response) => {
        this.empleados = Response.data || [];
      },
      (error) => {
        console.error('Error al cargar empleados', error);
      }
    );

    this.turnoService.getTurnos().subscribe(
      turnos => { this.turnos = turnos; },
      error => { alert('Error al cargar turnos'); }
    );
  }

  agregarTurno(): void {
    if (
      !this.nuevoTurno.empleado ||
      !this.nuevoTurno.fecha ||
      !this.nuevoTurno.horaInicio ||
      !this.nuevoTurno.horaFin
    ) {
      alert('Completa todos los campos.');
      return;
    }

    // Enviamos solo el ID del empleado en el objeto para backend
    const payload = {
      empleado: this.nuevoTurno.empleado,
      fecha: this.nuevoTurno.fecha,
      horaInicio: this.nuevoTurno.horaInicio,
      horaFin: this.nuevoTurno.horaFin
    };

    this.turnoService.agregarTurno(payload).subscribe(
      (turnoCreado) => {
        alert('Turno creado con éxito');
        this.cargarEmpleadosYTurnos(); // refresca lista para mostrar nombre empleado
        this.nuevoTurno = { empleado: undefined, fecha: '', horaInicio: '', horaFin: '' }; // limpia formulario
      },
      (error) => {
        alert('Error al agregar el turno');
        console.error(error);
      }
    );
  }

}
