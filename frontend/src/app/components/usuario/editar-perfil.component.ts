import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
  selector: 'app-editar-perfil',
  templateUrl: './editar-perfil.component.html',
  styleUrls: ['./editar-perfil.component.css']
})
export class EditarPerfilComponent {
  // Estado del modal
  modalEstado: boolean = false;

  // Datos del perfil del usuario
  @Input() idusuario: number | string = '';
  nombreDeUsuario: string = '';
  Rolusuario: string = 'User'; // valor por defecto
  telefonousuario: string = '';
  correoUSUARIO: string = '';
  UserName: string = '';
  contrasena: string ='';

  // Evento para notificar al componente padre
  @Output() perfilActualizado = new EventEmitter<any>();

  // Método para abrir el modal y cargar datos
  abrirModal(usuario: any) {
    this.idusuario = usuario.id;
    this.nombreDeUsuario = usuario.nombre;
    this.Rolusuario = usuario.rol;
    this.telefonousuario = usuario.telefono;
    this.correoUSUARIO = usuario.correo;
    this.modalEstado = true;
    this.UserName = usuario.UserName;
    this.contrasena= usuario.contrasena;
  }

   mostrarContrasena: boolean = false;
 toggleMostrarContrasena(): void {
    this.mostrarContrasena = !this.mostrarContrasena;
  }

  // Método para cerrar el modal
  cerrarModal() {
    this.modalEstado = false;
  }

  // Guardar cambios y emitir al componente padre
  guardarCambios() {
    const perfilActualizado = {
      id: this.idusuario,
      nombre: this.nombreDeUsuario,
      rol: this.Rolusuario,
      telefono: this.telefonousuario,
      correo: this.correoUSUARIO,
      UserName: this.UserName,
      contrasena: this.contrasena
    };

    // Emitir evento al padre
    this.perfilActualizado.emit(perfilActualizado);

    // Cerrar modal
    this.cerrarModal();
  }
}