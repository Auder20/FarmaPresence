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
    nombre: '',
    identificacion: '',
    correo: '',
    Rol: '',
    nombreDeUsuario: '',
    contrasena: '',
    telefono: ''
  };

  mostrarContrasena: boolean = false;

  constructor(private usuarioService: UsuarioService, private router: Router) {}

  toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  onSubmit(): void {
    if (this.validarFormulario()) {
      const nuevoUsuario = {
        nombre: this.usuario.nombre,
        identificacion: this.usuario.identificacion,
        correo: this.usuario.correo,
        Rol: this.usuario.Rol,
        username: this.usuario.nombreDeUsuario,
        contrasena: this.usuario.contrasena,
        telefono: this.usuario.telefono
      };
      this.usuarioService.addUsuario(nuevoUsuario).subscribe({
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
      this.usuario.contrasena.trim() !== '' &&
      this.usuario.telefono.trim() != ''
    );
  }

  resetForm(): void {
    this.usuario = {
      nombre: '',
      identificacion: '',
      correo: '',
      Rol: '',
      nombreDeUsuario: '',
      contrasena: '',
      telefono: ''
    };
    this.mostrarContrasena = false;
  }
}
