import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { LoginService } from '../../../services/login.service';
import { AuthService } from '../../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit, OnDestroy {

  formVisible: boolean = false;
  studentInfo: any;
  username: string = "";
  password: string = "";
  rememberMe: boolean = false;

  recoveryEmail: string = '';
  showRecoveryModal: boolean = false;
  validationMessage: string | null = null;
  sendingRecovery: boolean = false;

  passwordFieldType: string = 'password';

  showError: boolean = false;

  private authSubscription?: Subscription;

  constructor(public loginService: LoginService, private authService: AuthService, private router: Router) { }

  ngOnInit(): void {
    this.authSubscription = this.loginService.autenticado$.subscribe(isAuth => {
      this.formVisible = !isAuth;
      this.showError = false;

      if (isAuth) {
        const userId = localStorage.getItem('usuarioid');
        if (userId) {
          this.loginService.getStudentInfo(userId).subscribe(info => {
            this.studentInfo = info;
          });
        }
      } else {
        this.studentInfo = null;
      }
    });
  }

  ngOnDestroy(): void {
    this.authSubscription?.unsubscribe();
  }

  togglePasswordVisibility() {
    this.passwordFieldType = this.passwordFieldType === 'password' ? 'text' : 'password';
  }

  openRecoveryModal() {
    this.showRecoveryModal = true;
    this.validationMessage = null;
    this.recoveryEmail = '';
  }

  closeRecoveryModal() {
    this.showRecoveryModal = false;
    this.validationMessage = null;
    this.recoveryEmail = '';
    this.sendingRecovery = false;
  }

  login(): void {
    this.loginService.login(this.username, this.password, this.rememberMe).subscribe(
      success => {
        if (success) {
          this.showError = false;
          window.alert('Inicio de sesión exitoso. ¡Bienvenido!');
          this.authService.notifyLogin();
          this.router.navigate(['/informacionInicio']);
        } else {
          this.showError = true;
        }
      },
      error => {
        window.alert('Hubo un problema con el inicio de sesión. Por favor, intente de nuevo más tarde.');
      }
    );
  }

  sendRecoveryLink() {
    if (!this.recoveryEmail) {
      this.validationMessage = 'Por favor ingrese su correo electrónico.';
      return;
    }

    this.sendingRecovery = true;
   this.loginService.forgotPassword(this.recoveryEmail).subscribe(
  response => {
    alert('Correo de recuperación enviado');
  },
  error => {
    console.error(error);
    alert('Hubo un error al intentar enviar el correo');
  }
);

  }

  logout(): void {
    this.loginService.logout();
    this.authService.logout();
    this.username = '';
    this.password = '';
    this.router.navigate(['/login']);
  }
}
