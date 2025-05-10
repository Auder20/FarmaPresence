import { Component, OnInit } from '@angular/core'; 
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';

interface Empleado {
  nombre: string;
  identificacion: string;
  fechaContratacion: string;
  activo: boolean;
  activoStr?: string; // Nueva propiedad para almacenar la selección de "Sí" o "No"
  rol: string;
  huellaDactilar: string;
}

@Component({
  selector: 'app-registro-empleados',
  templateUrl: './registros.component.html',
  styleUrls: ['./registros.component.css']
})
export class RegistrosComponent implements OnInit {
  empleado: Empleado = { nombre: '', identificacion: '', fechaContratacion: '', activo: false, rol: '', huellaDactilar: '' };
  huellas: string[] = [];

  constructor(private registroService: RegistroEmpleadosService) {}

  ngOnInit(): void {
    this.getHuellas();
  }

  getHuellas(): void {
    this.registroService.getAllHuellas().subscribe((response: any) => {
      this.huellas = response.data; // Ajusta el acceso a la lista de huellas según tu respuesta backend
    });
  }

  onSubmit(): void {
    if (this.empleado.huellaDactilar) {
      this.empleado.activo = this.empleado.activoStr === 'Sí' ? true : false;
  
      console.log(this.empleado);
      console.log(this.empleado.huellaDactilar);
  
      this.registroService.updateEmpleado(this.empleado.huellaDactilar, this.empleado)
        .subscribe(
          (response) => {
            console.log('Empleado registrado:', response);
            alert(response.message || 'Empleado actualizado correctamente');
              // Vaciar el formulario después de éxito
          this.resetForm();
          },
          (error) => {
            console.error('Error al actualizar el empleado:', error);
  
            // Acceder al mensaje de error que está dentro de la respuesta HTTP
            if (error.error && error.error.message) {
              alert(error.error.message);  // Mostrar el mensaje que viene del backend
            } else {
              alert('Error desconocido al actualizar el empleado');
            }
          }
        );
    } else {
      alert('Debe seleccionar una huella dactilar válida.');
    }
  }
  
  // Método para vaciar el formulario
resetForm(): void {
  // Restablecer las propiedades del objeto empleado a su estado inicial
  this.empleado = {
    huellaDactilar: '',
    nombre: '',
    identificacion: '',
    fechaContratacion: '',
    activoStr: 'Sí',
    activo: true,
    rol: ''
  };
}

  onRefresh() {
    this.getHuellas(); // Actualiza la lista al hacer clic en el botón
  }
}
