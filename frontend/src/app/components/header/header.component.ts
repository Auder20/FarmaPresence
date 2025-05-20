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
  userId: number | null = null;
  isDropdownOpen: boolean = false;
  selectedNavItem: string = 'inicio';

  mobileMenuOpen: boolean = false;

  private authSubscription?: Subscription;
  private usernameSubscription?: Subscription;
  private usuarioidSubscription?: Subscription;

  constructor(private loginService: LoginService) {}

  ngOnInit(): void {
    const storedUserId = localStorage.getItem('usuarioid');
    this.userId = storedUserId !== null && !isNaN(+storedUserId) ? +storedUserId : null;

    this.username = this.normalizeUsername(this.loginService.getUsername());

    this.authSubscription = this.loginService.autenticado$.subscribe(loggedIn => {
      if (!loggedIn) {
        this.username = null;
        this.userId = null;
      }
    });

    this.usernameSubscription = this.loginService.username$.subscribe(newName => {
      this.username = this.normalizeUsername(newName);
    });

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

  closeDropdown() {
    this.isDropdownOpen = false;
  }

  toggleMobileMenu() {
    this.mobileMenuOpen = !this.mobileMenuOpen;
  }

  closeMobileMenu() {
    this.mobileMenuOpen = false;
  }

  selectNavItem(item: string) {
    this.selectedNavItem = item;
    this.closeMobileMenu();
    this.closeDropdown();
  }

  logout() {
    this.loginService.logout();
    this.closeMobileMenu();
    this.closeDropdown();
    this.userId = null;
  }

  private normalizeUsername(name: string | null | undefined): string | null {
    if (!name || name === 'undefined' || name.trim().length === 0) {
      return null;
    }
    return name;
  }

  get editarPerfilLink(): string {
    return this.userId ? `/editar-perfil/${this.userId}` : '/editar-perfil';
  }
}
