import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent implements OnInit {

  selectedNavItem: string = "";
  botonesHeader: boolean = true;
  username: string = "";
  isDropdownOpen: boolean = false;

  constructor(private router: Router, private loginService: LoginService) {}

  ngOnInit(): void {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.selectedNavItem = event.urlAfterRedirects.split('/')[1];
      }
    });

   this.loginService.studentInfo$.subscribe((info: any) => {
      if (info && info.data && info.data.nombreCompleto) {
        this.username = info.data.nombreCompleto;
      } else {
        this.username = "";
      }
    });
  }
    toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  selectNavItem(navItem: string): void {
    this.selectedNavItem = navItem;
  }

  editProfile(): void {
    this.router.navigate(['/editar-perfil']);
  }

  addUser(): void {
    this.router.navigate(['/agregar-usuario']);
  }

  logout(): void {
    this.loginService.logout();
  }
}
