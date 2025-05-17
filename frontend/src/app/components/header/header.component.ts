import { Component } from '@angular/core';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent {
  username: string = 'Auder Gonzalez Martinez'; // ejemplo, asigna dinámicamente
  isDropdownOpen: boolean = false;
  selectedNavItem: string = 'inicio';

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  selectNavItem(item: string) {
    this.selectedNavItem = item;
    this.isDropdownOpen = false; // cerrar menú al seleccionar opción
  }

  editProfile() {
    // lógica para editar perfil
    alert('Editar perfil');
    this.isDropdownOpen = false;
  }

  addUser() {
    // lógica para agregar usuario
    alert('Agregar usuario');
    this.isDropdownOpen = false;
  }

  logout() {
    // lógica para cerrar sesión
    alert('Sesión cerrada');
    this.isDropdownOpen = false;
  }
  closeDropdown() {
  this.isDropdownOpen = false;
}

}
