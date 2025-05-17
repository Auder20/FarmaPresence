import { Component } from '@angular/core';
import { UsuarioService } from '../../services/usuario.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-agregar-usuario',
  templateUrl: './agregar-usuario.component.html',
  styleUrls: ['./agregar-usuario.component.css']
})
export class AgregarUsuarioComponent {
  usuario = {
    nombreCompleto: '',
    identificacion: '',
    correoElectronico: '',
    rol: '',
    username: '',
    password: ''
  };

  mostrarContrasena: boolean = false;

  constructor(private usuarioService: UsuarioService, private router: Router) {}

  toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  onSubmit(): void {
    if (this.validarFormulario()) {
      // El objeto ya tiene las propiedades correctas para el backend
      this.usuarioService.addUsuario(this.usuario).subscribe({
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
      this.usuario.nombreCompleto.trim() !== '' &&
      this.usuario.identificacion.trim() !== '' &&
      this.usuario.correoElectronico.trim() !== '' &&
      this.usuario.rol.trim() !== '' &&
      this.usuario.username.trim() !== '' &&
      this.usuario.password.trim() !== ''
    );
  }

  resetForm(): void {
    this.usuario = {
      nombreCompleto: '',
      identificacion: '',
      correoElectronico: '',
      rol: '',
      username: '',
      password: ''
    };
    this.mostrarContrasena = false;
  }
}
