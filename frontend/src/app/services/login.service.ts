import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private API_SERVER = "http://localhost:8080/usuario";
  private API_SERVER_AUTH = "http://localhost:8080/auth";

  // Claves para localStorage
  private usuarioidKey = 'usuarioid';
  private usernameKey = 'username';
  private estudianteidKey = 'estudianteid';
  private rememberMeKey = 'rememberMe';
  private isLoggedInKey = 'isLoggedIn';

  private isLoggedIn: boolean = localStorage.getItem(this.isLoggedInKey) === 'true';

  private autenticadoSubject = new BehaviorSubject<boolean>(this.isLoggedIn);
  autenticado$ = this.autenticadoSubject.asObservable();

  private usernameSubject = new BehaviorSubject<string | null>(localStorage.getItem(this.usernameKey));
  username$ = this.usernameSubject.asObservable();

  private usuarioidSubject = new BehaviorSubject<number | null>(this.getStoredUsuarioid());
  usuarioid$ = this.usuarioidSubject.asObservable();

  private mostrarFormularioSubject = new BehaviorSubject<boolean>(true);
  mostrarFormulario$ = this.mostrarFormularioSubject.asObservable();

  private botonesInicioKey = 'botonesInicio';
  private botonesInicioSubject = new BehaviorSubject<boolean>(localStorage.getItem(this.botonesInicioKey) === 'true');
  botonesInicio$ = this.botonesInicioSubject.asObservable();

  private botonesHeaderKey = 'botonesHeader';
  private botonesHeaderSubject = new BehaviorSubject<boolean>(localStorage.getItem(this.botonesHeaderKey) === 'true');
  botonesHeader$ = this.botonesHeaderSubject.asObservable();

  private formVisibleKey = 'formVisible';
  private formVisibility = new BehaviorSubject<boolean>(localStorage.getItem(this.formVisibleKey) === 'true');
  currentFormVisibility = this.formVisibility.asObservable();

  private studentInfoSubject = new BehaviorSubject<any>(null);
  studentInfo$ = this.studentInfoSubject.asObservable();

  constructor(private httpClient: HttpClient, private router: Router, private authService: AuthService) {
    const mostrarFormulario = localStorage.getItem('mostrarFormulario');
    if (mostrarFormulario) {
      this.mostrarFormularioSubject.next(mostrarFormulario === 'true');
    }

    const botonesInicio = localStorage.getItem(this.botonesInicioKey);
    if (botonesInicio) {
      this.botonesInicioSubject.next(botonesInicio === 'true');
    }

    const isLoggedIn = localStorage.getItem(this.isLoggedInKey) === 'true';
    this.isLoggedIn = isLoggedIn;
    this.autenticadoSubject.next(isLoggedIn);

    if (isLoggedIn) {
      const userId = this.getStoredUsuarioid();
      if (userId) {
        this.getStudentInfo(userId.toString()).subscribe();
        this.changeFormVisibility(true);
      }
    }
  }

  private getStoredUsuarioid(): number | null {
    const stored = localStorage.getItem(this.usuarioidKey);
    return stored !== null && !isNaN(+stored) ? +stored : null;
  }

  login(username: string, password: string, rememberMe: boolean): Observable<boolean> {
    const loginPayload = { username, password };

    return this.httpClient.post<any>(`${this.API_SERVER}/login`, loginPayload).pipe(
      map(response => {
        if (response && response.code === "200" && response.message === "Login exitoso") {
          localStorage.setItem(this.isLoggedInKey, 'true');
          localStorage.setItem('autenticado', 'true');
          this.isLoggedIn = true;
          this.autenticadoSubject.next(true);

          this.setMostrarFormulario(false);
          this.unlockButtons();
          this.changeFormVisibility(true);

          const userId = response.data.id;
          const userName = response.data.nombreCompleto ?? '';

          localStorage.setItem(this.usernameKey, userName);
          this.usernameSubject.next(userName);

          localStorage.setItem(this.usuarioidKey, userId.toString());
          this.usuarioidSubject.next(userId);

          localStorage.setItem(this.rememberMeKey, rememberMe.toString());

          this.getStudentInfo(userId).subscribe(estudiante => {
            if (estudiante?.data?.id) {
              localStorage.setItem(this.estudianteidKey, estudiante.data.id.toString());
            }
          });

          this.authService.notifyLogin();

          return true;
        } else {
          return false;
        }
      }),
      catchError(error => {
        console.error('Login error', error);
        return of(false);
      })
    );
  }

  logout(): void {
    this.isLoggedIn = false;
    localStorage.removeItem(this.isLoggedInKey);
    localStorage.removeItem('autenticado');
    localStorage.removeItem(this.usuarioidKey);
    localStorage.removeItem(this.usernameKey);
    this.usernameSubject.next(null);
    this.usuarioidSubject.next(null);
    localStorage.removeItem(this.estudianteidKey);
    localStorage.removeItem(this.rememberMeKey);

    this.autenticadoSubject.next(false);

    this.setMostrarFormulario(true);
    this.lockButtons();
    this.changeFormVisibility(false);
    this.router.navigate(['/login']);
  }

  getUsername(): string | null {
    const username = this.usernameSubject.value || localStorage.getItem(this.usernameKey);
    return username && username !== 'undefined' ? username : null;
  }

  isAuthenticated(): boolean {
    return this.isLoggedIn;
  }

  toggleMostrarFormulario(): void {
    const currentValue = this.mostrarFormularioSubject.value;
    const newValue = !currentValue;
    this.mostrarFormularioSubject.next(newValue);
    localStorage.setItem('mostrarFormulario', newValue.toString());
  }

  setMostrarFormulario(value: boolean): void {
    this.mostrarFormularioSubject.next(value);
    localStorage.setItem('mostrarFormulario', value.toString());
  }

  unlockButtons(): void {
    this.botonesInicioSubject.next(false);
    localStorage.setItem(this.botonesInicioKey, 'false');
  }

  lockButtons(): void {
    this.botonesInicioSubject.next(true);
    localStorage.setItem(this.botonesInicioKey, 'true');
  }

  unlockButtonsH(): void {
    this.botonesHeaderSubject.next(false);
    localStorage.setItem(this.botonesHeaderKey, 'false');
  }

  lockButtonsH(): void {
    this.botonesHeaderSubject.next(true);
    localStorage.setItem(this.botonesHeaderKey, 'true');
  }

  changeFormVisibility(isVisible: boolean): void {
    this.formVisibility.next(isVisible);
    localStorage.setItem(this.formVisibleKey, isVisible.toString());
  }

  getStudentInfo(userId: string): Observable<any> {
    return this.httpClient.get<any>(`${this.API_SERVER}/${userId}`).pipe(
      tap(info => this.studentInfoSubject.next(info)),
      catchError(error => {
        console.error('Error fetching student info', error);
        return of(null);
      })
    );
  }

  updateStudentInfo(studentId: number, studentData: any): Observable<any> {
    return this.httpClient.put<any>(`http://localhost:8080/estudiante/${studentId}`, studentData).pipe(
      catchError(error => {
        console.error('Error updating student info', error);
        return throwError(error);
      })
    );
  }

  // >>> NUEVA LÓGICA AÑADIDA >>>

forgotPassword(email: string): Observable<any> {
  const data = { email: email };  // 👈 el backend espera 'email', no 'correosElectronicos'
  return this.httpClient.post(`${this.API_SERVER_AUTH}/forgot-password`, data) // 👈 asegúrate del endpoint correcto
}


  resetPassword(token: string, newPassword: string): Observable<any> {
  const data = {token: token, newPassword: newPassword };
  return this.httpClient.post(`${this.API_SERVER_AUTH}/reset-password`, data);
}

}
