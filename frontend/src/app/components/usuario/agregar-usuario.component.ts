import { Component } from '@angular/core';

@Component({
  selector: 'app-agregar-usuario',
  templateUrl: './agregar-usuario.component.html',
  styleUrls: ['./agregar-usuario.component.css']
})
export class AgregarUsuarioComponent {
  // Modelo de usuario basado en los campos del formulario HTML
  usuario = {
    nombre: '',
    identificacion: '',
    correo: '',
    Rol: '',
    nombreDeUsuario: '',
    contrasena: ''
  };

  // Controla si la contraseña se muestra o se oculta
  mostrarContrasena: boolean = false;

  // Alternar visibilidad de la contraseña
  toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  // Envío del formulario
  onSubmit(): void {
    if (this.validarFormulario()) {
      console.log('Usuario registrado:', this.usuario);
      alert('Usuario registrado correctamente');
      this.resetForm();
    } else {
      alert('Por favor completa todos los campos obligatorios.');
    }
  }

  // Validación básica de campos obligatorios
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

  // Restablecer el formulario a valores vacíos
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
