import { Component } from '@angular/core';
import { RegistroEmpleadosService } from '../../../services/registro-empleados.service';

@Component({
  selector: 'app-informacion-inicio',
  templateUrl: './informacion-inicio.component.html',
  styleUrls: ['./informacion-inicio.component.css']
})
export class InformacionInicioComponent {
  modalEstado: boolean = false; // Modal de calificaciones
  empleados: any[] = [];

  // Properties for update form binding
  idEmpleado: string = '';
  nombreEmpleado: string = '';
  rolEmpleado: string = '';
  telefonoEmpleado: string = '';
  identificacionEmpleado: string = '';
  huellaEmpleado: string = '';

  constructor(private registroEmpleadosService: RegistroEmpleadosService) {}

  consultarEmpleados() {
    this.registroEmpleadosService.getAllEmpleados().subscribe(
      (response) => {
        if (response && response.data) {
          this.empleados = response.data;
        }
      },
      (error) => {
        console.error('Error al obtener empleados:', error);
      }
    );
  }

  abrirModalActualizar() {
    this.modalEstado = true;
  }

  cerrarModal() {
    this.modalEstado = false;
  }

  cargarEmpleadoParaActualizar(empleado: any) {
    this.idEmpleado = empleado.id || '';
    this.nombreEmpleado = empleado.nombre || '';
    this.rolEmpleado = empleado.rol || '';
    this.telefonoEmpleado = empleado.telefono || '';
    this.identificacionEmpleado = empleado.identificacion || '';
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
      // Add other fields as needed
    };

    // Call update method in service using huellaEmpleado as identifier
    this.registroEmpleadosService.updateEmpleado(this.huellaEmpleado, empleadoData).subscribe(
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
