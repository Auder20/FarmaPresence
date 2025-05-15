import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HeaderComponent } from './components/header/header.component';
import { LoginComponent } from './components/PRINCIPAL/login/login.component';
import { RegistrosComponent } from './components/registros-Empleados/registros.component';
import { ReportesComponent } from './components/reportes/reportes.component';
import { GraficasComponent } from './components/graficas/graficas.component';
import { InformacionInicioComponent } from './components/PRINCIPAL/informacion-inicio/informacion-inicio.component';
import { RegistroAsistenciaComponent } from './components/registro-asistencia/registro-asistencia.component';
import { AuthGuard } from './guards/auth.guard';
import { EditarPerfilComponent } from './components/usuario/editar-perfil.component';
import { AgregarUsuarioComponent } from './components/usuario/agregar-usuario.component';

const routes: Routes = [
  { path: 'header', component: HeaderComponent, canActivate: [AuthGuard]},
  { path: 'informacionInicio', component: InformacionInicioComponent, canActivate: [AuthGuard]},
  { path: 'reportes', component: ReportesComponent, canActivate: [AuthGuard]},
  { path: 'registros', component : RegistrosComponent, canActivate: [AuthGuard]},
  { path: 'graficas', component : GraficasComponent, canActivate: [AuthGuard]},
  { path:'registroAsistencia',component: RegistroAsistenciaComponent, canActivate: [AuthGuard]},
  { path: 'editar-perfil', component: EditarPerfilComponent, canActivate: [AuthGuard]},
  { path: 'agregar-usuario', component: AgregarUsuarioComponent, canActivate: [AuthGuard]},
  { path: 'login', component : LoginComponent,
  children:
  [
    { path: '', redirectTo: 'informacionInicio', pathMatch: 'full' }, // Fixed typo here
    { path: 'informacionInicio', component: InformacionInicioComponent },

  ]},

  { path: '', redirectTo: '/login', pathMatch: 'full' } // Redirige al componente de inicio de sesión por defecto
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
