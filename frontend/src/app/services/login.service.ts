import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class LoginService {

  private API_SERVER = "http://localhost:8080/usuario";

  private usuarioidKey = 'usuarioid'; // Clave para almacenar el ID de usuario
  private estudianteidKey = 'estudianteid'; // Clave para almacenar el ID del estudiante
  private rememberMeKey = 'rememberMe'; // Clave para almacenar el estado de "recordar sesión"
  private isLoggedInKey = 'isLoggedIn'; // Clave para estado de login

  private isLoggedIn: boolean = localStorage.getItem(this.isLoggedInKey) === 'true';

  // BehaviorSubject para estado de autenticación reactivo
  private autenticadoSubject = new BehaviorSubject<boolean>(this.isLoggedIn);
  autenticado$ = this.autenticadoSubject.asObservable();

  // Otros BehaviorSubjects para visibilidad de formulario y botones
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
    // Al crear el servicio, se inicializan los estados desde localStorage
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
this.autenticadoSubject.next(isLoggedIn); // 🔥 emite valor al reentrar a la app

if (isLoggedIn) {
  const userId = localStorage.getItem(this.usuarioidKey);
  if (userId) {
    this.getStudentInfo(userId).subscribe();
    this.changeFormVisibility(true);
  }
}

  }

  login(username: string, password: string, rememberMe: boolean): Observable<boolean> {
    const loginPayload = { username, password };

    return this.httpClient.post<any>(`${this.API_SERVER}/login`, loginPayload).pipe(
      map(response => {
        if (response && response.code === "200" && response.message === "Login exitoso") {
          localStorage.setItem(this.isLoggedInKey, 'true');
          localStorage.setItem('autenticado', 'true'); // para compatibilidad
          this.isLoggedIn = true;
          this.autenticadoSubject.next(true); // Notificar a suscriptores

          this.setMostrarFormulario(false);
          this.unlockButtons();
          this.changeFormVisibility(true);

          const userId = response.data.id;
          localStorage.setItem(this.usuarioidKey, userId);
          localStorage.setItem(this.rememberMeKey, rememberMe.toString());

          this.getStudentInfo(userId).subscribe(estudiante => {
            if (estudiante && estudiante.data.id) {
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

  registerUser(usuario: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.httpClient.post(`${this.API_SERVER}`, usuario, { headers });
  }

  logout(): void {
    this.isLoggedIn = false;
    localStorage.removeItem(this.isLoggedInKey);
    localStorage.removeItem('autenticado');
    localStorage.removeItem(this.usuarioidKey);
    localStorage.removeItem(this.estudianteidKey);
    localStorage.removeItem(this.rememberMeKey);

    this.autenticadoSubject.next(false); // Notificar a suscriptores

    this.setMostrarFormulario(true);
    this.lockButtons();
    this.changeFormVisibility(false);
    this.router.navigate(['/']);
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

  toggleLoginStatus(): void {
    this.isLoggedIn = !this.isLoggedIn;
    localStorage.setItem(this.isLoggedInKey, this.isLoggedIn ? 'true' : 'false');
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
      tap(info => {
        this.studentInfoSubject.next(info);
      }),
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

  sendRecoveryLink(email: string): Observable<any> {
    const body = {
      correosElectronicos: [email]
    };
    return this.httpClient.post(`${this.API_SERVER}/forgot-password`, body);
  }

  resetPassword(token: string, password: string): Observable<any> {
    return this.httpClient.post(`http://localhost:8080/usuario/reset-password?token=${token}`, { password });
  }

  estaAutenticado(): boolean {
    return localStorage.getItem('autenticado') === 'true';
  }
}
