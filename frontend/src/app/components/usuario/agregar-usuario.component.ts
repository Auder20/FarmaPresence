import { Component } from '@angular/core';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agregar-usuario',
  templateUrl: './agregar-usuario.component.html',
  styleUrls: ['./agregar-usuario.component.css']
})
export class AgregarUsuarioComponent {
  usuario = {
    nombre: '',
    identificacion: '',
    correo: '',
    Rol: '',
    nombreDeUsuario: '',
    contrasena: ''
  };

  mostrarContrasena: boolean = false;

  constructor(private registroEmpleadosService: RegistroEmpleadosService, private router: Router) {}

  toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  onSubmit(): void {
    if (this.validarFormulario()) {
      this.registroEmpleadosService.updateEmpleado('', this.usuario).subscribe({
        next: (response) => {
          alert('Usuario registrado correctamente');
          this.resetForm();
          this.router.navigate(['/informacionInicio']);
        },
        error: (error) => {
          console.error('Error al registrar usuario:', error);
          alert('Error al registrar usuario. Intente nuevamente.');
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

  resetForm(): void {
    this.usuario = {
      nombre: '',
      identificacion: '',
      correo: '',
      Rol: '',
      nombreDeUsuario: '',
      contrasena: ''
    };
    this.mostrarContrasena = false;
  }
}
