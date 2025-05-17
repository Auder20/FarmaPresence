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
  isDropdownOpen: boolean = false;
  selectedNavItem: string = 'inicio';

  private authSubscription?: Subscription;
  private usernameSubscription?: Subscription;

  constructor(private loginService: LoginService) {}

  ngOnInit(): void {
    // Set initial username (cuidado con 'undefined')
    this.username = this.normalizeUsername(this.loginService.getUsername());

    // Suscripción a cambios de autenticación para ocultar menú si se desloguea
    this.authSubscription = this.loginService.autenticado$.subscribe(loggedIn => {
      if (!loggedIn) {
        this.username = null;
      }
    });

    // Suscripción a cambios del nombre de usuario (reactivo)
    this.usernameSubscription = this.loginService.username$.subscribe(newName => {
      this.username = this.normalizeUsername(newName);
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
    this.usernameSubscription?.unsubscribe();
  }

  toggleDropdown() {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  selectNavItem(item: string) {
    this.selectedNavItem = item;
    this.isDropdownOpen = false;
  }

  logout() {
    this.loginService.logout();
    this.isDropdownOpen = false;
  }

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  // Utilidad para evitar mostrar "undefined" literal
  private normalizeUsername(name: string | null | undefined): string | null {
    if (!name || name === 'undefined' || name.trim().length === 0) {
      return null;
    }
    return name;
  }
}
