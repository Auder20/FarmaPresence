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
  password: string = ''; // Campo para la nueva contraseña si el usuario quiere cambiarla
  mostrarContrasena: boolean = false;

  // Para el formulario de cambio de contraseña
  mostrarFormularioCambioContrasena: boolean = false;
  contrasenaActual: string = '';
  nuevaContrasena: string = '';
  confirmarContrasena: string = '';

  // Guardamos internamente la contraseña original (nunca se muestra)
  private passwordOriginal: string = '';

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
        this.nombreCompleto = usuario.nombreCompleto || '';
        this.username = usuario.username || '';
        this.rol = usuario.rol || '';
        this.telefono = usuario.telefono || '';
        this.correoElectronico = usuario.correoElectronico || '';
        this.passwordOriginal = usuario.password || ''; // Guardamos la original
        this.password = ''; // No mostramos la contraseña real
      },
      error: (error) => {
        console.error('Error al cargar usuario:', error);
        alert('Error al cargar datos del usuario.');
      }
    });
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

    // Por defecto usamos la contraseña original
    let passwordToSend = this.passwordOriginal;

    // Si el usuario escribió una nueva contraseña, la usamos en lugar de la original
    if (this.password && this.password.trim() !== '') {
      passwordToSend = this.password.trim();
    }

    const usuarioActualizado: any = {
      id: this.idusuario,
      nombreCompleto: this.nombreCompleto,
      username: this.username,
      rol: this.rol,
      telefono: this.telefono,
      correoElectronico: this.correoElectronico,
      password: passwordToSend
    };

    console.log('Usuario actualizado que se enviará:', usuarioActualizado);

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

  console.log('Contraseña actual enviada:', this.contrasenaActual);
  console.log('Nueva contraseña enviada:', this.nuevaContrasena);

  this.usuarioService.updatePassword(this.idusuario!, {
    contrasenaActual: this.contrasenaActual,
    nuevaContrasena: this.nuevaContrasena
  }).subscribe({
    next: () => {
      alert('Contraseña cambiada correctamente');
      this.passwordOriginal = this.nuevaContrasena;
      this.toggleMostrarFormulario();
    },
    error: (error) => {
      console.error('Error al cambiar contraseña:', error);
      alert('Error al cambiar la contraseña. Verifica tu contraseña actual.');
    }
  });
}

}
