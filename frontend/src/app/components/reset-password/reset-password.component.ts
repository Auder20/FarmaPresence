import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoginService } from '../../services/login.service';

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.css']
})
export class ResetPasswordComponent implements OnInit {

  token: string = '';
  newPassword: string = '';
  confirmPassword: string = '';
  message: string = '';
  loading: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private loginService: LoginService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParams['token'] || '';
    if (!this.token) {
      this.message = 'Token inválido o no proporcionado.';
    }
  }

  resetPassword(): void {
    if (!this.newPassword || !this.confirmPassword) {
      this.message = 'Por favor complete todos los campos.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.message = 'Las contraseñas no coinciden.';
      return;
    }
    this.loading = true;
    this.loginService.resetPassword(this.token, this.newPassword).subscribe({
      next: () => {
        this.message = 'Contraseña actualizada correctamente. Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 3000);
      },
      error: (err) => {
        this.message = 'Error al actualizar la contraseña: ' + (err.error || 'Intente de nuevo.');
        this.loading = false;
      }
    });
  }
}
