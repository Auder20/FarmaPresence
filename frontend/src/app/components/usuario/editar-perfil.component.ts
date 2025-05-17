import { Component, OnInit } from '@angular/core';
import { UsuarioService } from '../../services/usuario.service';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-editar-perfil',
  templateUrl: './editar-perfil.component.html',
  styleUrls: ['./editar-perfil.component.css']
})
export class EditarPerfilComponent implements OnInit {

  idusuario: number | null = null;

  nombreCompleto: string = '';
  username: string = '';
  rol: string = '';
  telefono: string = '';
  correoElectronico: string = '';
  password: string = ''; // No mostrar contraseña por seguridad
  mostrarContrasena: boolean = false;

  // Para el formulario de cambio de contraseña (opcional)
  mostrarFormularioCambioContrasena: boolean = false;
  contrasenaActual: string = '';
  nuevaContrasena: string = '';
  confirmarContrasena: string = '';

  constructor(
    private usuarioService: UsuarioService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    const routeId = this.route.snapshot.paramMap.get('id');
    if (routeId && !isNaN(Number(routeId))) {
      this.idusuario = Number(routeId);
      this.cargarDatosUsuario(this.idusuario);
    } else {
      alert('ID de usuario inválido o no proporcionado');
      this.router.navigate(['/informacionInicio']);
    }
  }

  cargarDatosUsuario(id: number): void {
    this.usuarioService.getUsuarioById(id).subscribe({
      next: (response) => {
        const usuario = response.data;
        this.nombreCompleto = usuario.nombreCompleto ?? '';
        this.username = usuario.username ?? '';
        this.rol = usuario.rol ?? '';
        this.telefono = usuario.telefono ?? '';
        this.correoElectronico = usuario.correoElectronico ?? '';
        this.password = ''; // No mostrar la contraseña
      },
      error: (error) => {
        console.error('Error al cargar usuario:', error);
        alert('Error al cargar datos del usuario.');
        this.router.navigate(['/informacionInicio']);
      }
    });
  }

  toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  toggleMostrarFormulario(): void {
    this.mostrarFormularioCambioContrasena = !this.mostrarFormularioCambioContrasena;
    if (!this.mostrarFormularioCambioContrasena) {
      this.contrasenaActual = '';
      this.nuevaContrasena = '';
      this.confirmarContrasena = '';
    }
  }

  guardarCambios(): void {
    if (!this.idusuario) {
      alert('ID de usuario inválido');
      return;
    }

    const usuarioActualizado = {
      id: this.idusuario,
      nombreCompleto: this.nombreCompleto.trim(),
      username: this.username.trim(),
      rol: this.rol.trim(),
      telefono: this.telefono.trim(),
      correoElectronico: this.correoElectronico.trim(),
      password: this.password // vacío si no se cambia aquí
    };

    this.usuarioService.updateUsuario(this.idusuario, usuarioActualizado).subscribe({
      next: () => {
        alert('Perfil actualizado correctamente');
        this.router.navigate(['/informacionInicio']);
      },
      error: (error) => {
        console.error('Error al actualizar perfil:', error);
        alert('Error al actualizar perfil');
      }
    });
  }

  cambiarContrasena(): void {
    if (!this.contrasenaActual || !this.nuevaContrasena || !this.confirmarContrasena) {
      alert('Por favor, completa todos los campos del cambio de contraseña.');
      return;
    }

    if (this.nuevaContrasena !== this.confirmarContrasena) {
      alert('La nueva contraseña y la confirmación no coinciden.');
      return;
    }

    this.usuarioService.updatePassword(this.idusuario!, {
      contrasenaActual: this.contrasenaActual,
      nuevaContrasena: this.nuevaContrasena
    }).subscribe({
      next: () => {
        alert('Contraseña cambiada correctamente');
        this.toggleMostrarFormulario();
      },
      error: (error) => {
        console.error('Error al cambiar contraseña:', error);
        alert('Error al cambiar la contraseña. Verifica tu contraseña actual.');
      }
    });
  }
}
