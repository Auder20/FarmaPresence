import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/PRINCIPAL/login/login.component';
import { RegistrosComponent } from './components/registros-Empleados/registros.component';
import { ReportesComponent } from './components/reportes/reportes.component';
import { GraficasComponent } from './components/graficas/graficas.component';
import { InformacionInicioComponent } from './components/PRINCIPAL/informacion-inicio/informacion-inicio.component';
import { RegistroAsistenciaComponent } from './components/registro-asistencia/registro-asistencia.component';
import { EditarPerfilComponent } from './components/usuario/editar-perfil.component';
import { AgregarUsuarioComponent } from './components/usuario/agregar-usuario.component';
import { AuthGuard } from './guards/auth.guard';
import { RedirectGuard } from './guards/redirect.guard';

const routes: Routes = [
  // Rutas protegidas por AuthGuard
  { path: 'header', component: HeaderComponent, canActivate: [AuthGuard] },
  { path: 'informacionInicio', component: InformacionInicioComponent, canActivate: [AuthGuard] },
  { path: 'reportes', component: ReportesComponent, canActivate: [AuthGuard] },
  { path: 'registros', component: RegistrosComponent, canActivate: [AuthGuard] },
  { path: 'graficas', component: GraficasComponent, canActivate: [AuthGuard] },
  { path: 'editar-perfil', component: EditarPerfilComponent, canActivate: [AuthGuard] },
  { path: 'agregar-usuario', component: AgregarUsuarioComponent, canActivate: [AuthGuard] },
  { path: 'registro-asistencia', component: RegistroAsistenciaComponent, canActivate: [AuthGuard] },

  // Ruta pública para login
  { path: 'login', component: LoginComponent },

  // Redirección condicional por defecto
  { path: '', canActivate: [RedirectGuard], component: LoginComponent },

  // Ruta comodín para cualquier ruta no definida
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
