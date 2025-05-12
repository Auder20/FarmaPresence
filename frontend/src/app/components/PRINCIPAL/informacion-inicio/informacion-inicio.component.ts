import { Component, OnInit } from '@angular/core';
import { RegistroEmpleadosService } from '../../../services/registro-empleados.service';
import { LoginService } from '../../../services/login.service';

@Component({
  selector: 'app-informacion-inicio',
  templateUrl: './informacion-inicio.component.html',
  styleUrls: ['./informacion-inicio.component.css']
})
export class InformacionInicioComponent implements OnInit {
  modalEstado: boolean = false; // Modal de calificaciones
  empleados: any[] = [];

  searchIdentificacion: string = '';
  modalEmpleadosVisible: boolean = false;

  horarios: any[] = [];

  // Properties for update form binding
  idEmpleado: string = '';
  nombreEmpleado: string = '';
  rolEmpleado: string = '';
  telefonoEmpleado: string = '';
  identificacionEmpleado: string = '';
  activoEmpleado: boolean = false;
  horarioEmpleado: string = '';
  huellaEmpleado: string = '';

  userName: string = 'Usuario';

  constructor(private registroEmpleadosService: RegistroEmpleadosService, private loginService: LoginService) {}

  ngOnInit(): void {
    this.loginService.studentInfo$.subscribe(info => {
      console.log('InformacionInicioComponent - user info received:', info);
      if (info && info.data && info.data.nombre) {
        this.userName = info.data.nombre;
      }
    });

    this.registroEmpleadosService.getAllHorarios().subscribe(
      (response) => {
        if (response && response.data) {
          this.horarios = response.data;
        }
      },
      (error) => {
        console.error('Error al obtener horarios:', error);
      }
    );
  }

  consultarEmpleados() {
    this.registroEmpleadosService.getAllEmpleados().subscribe(
      (response) => {
        if (response && response.data) {
          this.empleados = response.data;
          this.modalEmpleadosVisible = true; // Show modal with employees
        }
      },
      (error) => {
        console.error('Error al obtener empleados:', error);
      }
    );
  }

  get empleadosFiltrados() {
    if (!this.searchIdentificacion) {
      return this.empleados;
    }
    return this.empleados.filter(e =>
      e.identificacion.toLowerCase().includes(this.searchIdentificacion.toLowerCase())
    );
  }

  abrirModalActualizar() {
    this.modalEstado = true;
  }

  cerrarModal() {
    this.modalEstado = false;
  }

  cerrarModalEmpleados() {
    this.modalEmpleadosVisible = false;
    this.searchIdentificacion = '';
  }

  cargarEmpleadoParaActualizar(empleado: any) {
    this.idEmpleado = empleado.id || '';
    this.nombreEmpleado = empleado.nombre || '';
    this.rolEmpleado = empleado.rol || '';
    this.telefonoEmpleado = empleado.telefono || '';
    this.identificacionEmpleado = empleado.identificacion || '';
    this.activoEmpleado = empleado.activo || false;
    this.horarioEmpleado = empleado.horario || '';
    this.huellaEmpleado = empleado.huellaDactilar || '';
    this.abrirModalActualizar();
  }

  guardarCambios() {
    // Prepare employee data object
    const empleadoData = {
      nombre: this.nombreEmpleado,
      rol: this.rolEmpleado,
      telefono: this.telefonoEmpleado,
      identificacion: this.identificacionEmpleado,
      activo: this.activoEmpleado,
      horario: this.horarioEmpleado
    };

    // Call update method in service using identificacion as identifier
    this.registroEmpleadosService.updateEmpleadoByIdentificacion(this.identificacionEmpleado, empleadoData).subscribe(
      (response) => {
        console.log('Empleado actualizado:', response);
        this.cerrarModal();
        this.consultarEmpleados(); // Refresh list
      },
      (error) => {
        console.error('Error al actualizar empleado:', error);
      }
    );
  }
}
