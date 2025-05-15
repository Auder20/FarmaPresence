import { Component } from '@angular/core';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-editar-perfil',
  templateUrl: './editar-perfil.component.html',
  styleUrls: ['./editar-perfil.component.css']
})
export class EditarPerfilComponent {
  usuario = {
    nombre: '',
    identificacion: '',
    correo: '',
    Rol: '',
    nombreDeUsuario: '',
    contrasena: ''
  };

  constructor(private registroEmpleadosService: RegistroEmpleadosService, private router: Router) {}

  onSubmit(): void {
    if (this.validarFormulario()) {
      this.registroEmpleadosService.updateEmpleadoByIdentificacion(this.usuario.identificacion, this.usuario).subscribe({
        next: (response) => {
          alert('Perfil actualizado correctamente');
          this.router.navigate(['/informacionInicio']);
        },
        error: (error) => {
          console.error('Error al actualizar perfil:', error);
          alert('Error al actualizar perfil. Intente nuevamente.');
        }
      });
    } else {
      alert('Por favor completa todos los campos obligatorios.');
    }
  }

  validarFormulario(): boolean {
    return (
      this.usuario.nombre.trim() !== '' &&
      this.usuario.identificacion.trim() !== '' &&
      this.usuario.correo.trim() !== '' &&
      this.usuario.Rol.trim() !== '' &&
      this.usuario.nombreDeUsuario.trim() !== '' &&
      this.usuario.contrasena.trim() !== ''
    );
  }
}
