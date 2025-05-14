import { Component, OnInit } from '@angular/core';
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

  constructor(private router: Router, private loginService: LoginService) {}

  ngOnInit(): void {

    //para mantener la ruta actual después de recargar la página
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.selectedNavItem = event.urlAfterRedirects.split('/')[1]; // Obtener la ruta actual después de las redirecciones
      }
    });

    this.loginService.studentInfo$.subscribe((info: any) => {
      if (info && info.data && info.data.nombre) {
        this.username = info.data.nombre;
      } else {
        this.username = "";
      }
    });
  }

  selectNavItem(navItem: string): void {
    this.selectedNavItem = navItem;
  }

  editProfile(): void {
    // Implementar la lógica para editar perfil
    alert('Editar perfil - funcionalidad pendiente');
  }

  addUser(): void {
    // Implementar la lógica para agregar usuario
    alert('Agregar usuario - funcionalidad pendiente');
  }

  logout(): void {
    // Implementar la lógica para cerrar sesión
    alert('Cerrar sesión - funcionalidad pendiente');
  }
}
