import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { RegistroEmpleadosService } from '../../../services/registro-empleados.service';
import { LoginService } from '../../../services/login.service';

@Component({
  selector: 'app-informacion-inicio',
  templateUrl: './informacion-inicio.component.html',
  styleUrls: ['./informacion-inicio.component.css']
})
export class InformacionInicioComponent implements OnInit, OnDestroy {
  usuarioAutenticado: boolean = false;
  private authSubscription?: Subscription;

  modalEstado: boolean = false;
  modalEmpleadosVisible: boolean = false;

  empleados: any[] = [];
  horarios: any[] = [];

  searchTerm: string = '';

  idEmpleado: string = '';
  nombreEmpleado: string = '';
  rolEmpleado: string = '';
  telefonoEmpleado: string = '';
  identificacionEmpleado: string = '';
  activoEmpleado: boolean = false;
  horarioEmpleado: string = '';  // Aquí guardaremos el idHorario
  huellaEmpleado: string = '';

  userName: string = 'Usuario';

  constructor(
    private registroEmpleadosService: RegistroEmpleadosService,
    public loginService: LoginService
  ) {}

  ngOnInit(): void {
    this.authSubscription = this.loginService.autenticado$.subscribe(isAuth => {
      this.usuarioAutenticado = isAuth;
    });

    this.loginService.studentInfo$.subscribe(info => {
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

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
  }

  consultarEmpleados() {
    this.registroEmpleadosService.getAllEmpleados().subscribe(
      (response) => {
        if (response && response.data) {
          this.empleados = response.data;
          this.modalEmpleadosVisible = true;
        }
      },
      (error) => {
        console.error('Error al obtener empleados:', error);
      }
    );
  }

  get empleadosFiltrados() {
    if (!this.searchTerm) {
      return this.empleados;
    }
    const term = this.searchTerm.toLowerCase();
    return this.empleados.filter(e =>
      e.identificacion.toLowerCase().includes(term) ||
      e.nombre.toLowerCase().includes(term) ||
      e.rol.toLowerCase().includes(term)
    );
  }

  cargarEmpleadoParaActualizar(empleado: any) {
    this.idEmpleado = empleado.id || '';
    this.nombreEmpleado = empleado.nombre || '';
    this.rolEmpleado = empleado.rol || '';
    this.telefonoEmpleado = empleado.telefono || '';
    this.identificacionEmpleado = empleado.identificacion || '';
    this.activoEmpleado = empleado.activo || false;
    this.horarioEmpleado = empleado.idHorario || '';  // Aquí el cambio importante
    this.huellaEmpleado = empleado.huellaDactilar || '';
    this.modalEstado = true;
  }

  cerrarModal() {
    this.modalEstado = false;
  }

  cerrarModalEmpleados() {
    this.modalEmpleadosVisible = false;
    this.searchTerm = '';
  }

  guardarCambios() {
    const empleadoData = {
      nombre: this.nombreEmpleado,
      rol: this.rolEmpleado,
      telefono: this.telefonoEmpleado,
      identificacion: this.identificacionEmpleado,
      activo: this.activoEmpleado,
      idHorario: this.horarioEmpleado  // Aquí también el cambio importante
    };

    this.registroEmpleadosService.updateEmpleadoByIdentificacion(
      this.identificacionEmpleado,
      empleadoData
    ).subscribe(
      (response) => {
        console.log('Empleado actualizado:', response);
        this.cerrarModal();
        this.consultarEmpleados();
      },
      (error) => {
        console.error('Error al actualizar empleado:', error);
      }
    );
  }

  getDescripcionHorario(idHorario: string): string {
    for (let i = 0; i < this.horarios.length; i++) {
      if (this.horarios[i].id === idHorario) {
        return this.horarios[i].descripcion;
      }
    }
    return 'N/A';
  }
}
