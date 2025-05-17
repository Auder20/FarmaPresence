import { Component, OnInit, OnDestroy } from '@angular/core';
import { LoginService } from '../../services/login.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {
  username: string | null = null;
  userId: number | null = null;  // <-- nuevo
  isDropdownOpen: boolean = false;
  selectedNavItem: string = 'inicio';

  mobileMenuOpen: boolean = false;  // <-- para menú hamburguesa móvil

  private authSubscription?: Subscription;
  private usernameSubscription?: Subscription;
  private usuarioidSubscription?: Subscription;  // Suscripción nueva

  constructor(private loginService: LoginService) {}

  ngOnInit(): void {
    // Leer userId inicial desde localStorage (por si ya está guardado)
    const storedUserId = localStorage.getItem('usuarioid');
    this.userId = storedUserId !== null && !isNaN(+storedUserId) ? +storedUserId : null;

    this.username = this.normalizeUsername(this.loginService.getUsername());

    this.authSubscription = this.loginService.autenticado$.subscribe(loggedIn => {
      if (!loggedIn) {
        this.username = null;
        this.userId = null; // Limpiar userId al logout
      }
    });

    this.usernameSubscription = this.loginService.username$.subscribe(newName => {
      this.username = this.normalizeUsername(newName);
    });

    // Suscribirse al observable para recibir actualizaciones del usuarioid
    this.usuarioidSubscription = this.loginService.usuarioid$.subscribe(newId => {
      this.userId = newId !== null && !isNaN(+newId) ? +newId : null;
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
    this.usernameSubscription?.unsubscribe();
    this.usuarioidSubscription?.unsubscribe();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  selectNavItem(item: string) {
    this.selectedNavItem = item;
    this.isDropdownOpen = false;
    this.mobileMenuOpen = false; // Cerrar menú móvil al seleccionar opción
  }

  logout() {
    this.loginService.logout();
    this.isDropdownOpen = false;
    this.userId = null;
    this.mobileMenuOpen = false; // Asegurar que el menú se cierra al hacer logout
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  // Evitar mostrar "undefined" literal
  private normalizeUsername(name: string | null | undefined): string | null {
    if (!name || name === 'undefined' || name.trim().length === 0) {
      return null;
    }
    return name;
  }

  // Construir ruta al perfil de usuario para usar en el HTML
  get editarPerfilLink(): string {
    return this.userId ? `/editar-perfil/${this.userId}` : '/editar-perfil';
  }
}
