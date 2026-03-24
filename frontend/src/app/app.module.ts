import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HTTP_INTERCEPTORS } from '@angular/common/http';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ReportesComponent } from './components/reportes/reportes.component';
import { RegistrosComponent } from './components/registros-Empleados/registros.component';
import { HeaderComponent } from './components/header/header.component';
import { InformacionInicioComponent } from './components/PRINCIPAL/informacion-inicio/informacion-inicio.component';
import { LoginComponent } from './components/PRINCIPAL/login/login.component';
import { ResetPasswordComponent } from './components/reset-password/reset-password.component';
import { TurnoProgramadoComponent } from './components/turno-programado/turno-programado.component';

// JWT Interceptor
import { JwtInterceptor } from './interceptors/jwt.interceptor';

import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

//proporciona directivas y tuberías (pipes) comunes utilizadas
//en aplicaciones Angular, como ngClass, ngIf, ngFor, entre otras
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { RegistroAsistenciaComponent } from './components/registro-asistencia/registro-asistencia.component'; // Importa ReactiveFormsModule y FormsModule
import { AgregarUsuarioComponent } from './components/usuario/agregar-usuario.component';
import { EditarPerfilComponent } from './components/usuario/editar-perfil.component';

@NgModule({
  declarations: [
    AppComponent,
    ReportesComponent,
    RegistrosComponent,
    HeaderComponent,
    InformacionInicioComponent,
    LoginComponent,
    RegistroAsistenciaComponent,
    AgregarUsuarioComponent,
    EditarPerfilComponent,
    ResetPasswordComponent,
    TurnoProgramadoComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    CommonModule,
    ReactiveFormsModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: JwtInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
