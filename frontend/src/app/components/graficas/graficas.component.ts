import { Component, OnInit } from '@angular/core';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';

@Component({
  selector: 'app-graficas',
  templateUrl: './graficas.component.html',
  styleUrls: ['./graficas.component.css']
})
export class GraficasComponent implements OnInit {
  mes: number = new Date().getMonth() + 1; // Current month
  anio: number = new Date().getFullYear(); // Current year
  datosGrafica: any = null;
  error: string = '';

  constructor(private registroService: RegistroEmpleadosService) {}

  ngOnInit(): void {
    this.cargarDatosGrafica();
  }

  cargarDatosGrafica(): void {
    this.registroService.getReporteComparativoGrafica(this.mes, this.anio).subscribe(
      (data) => {
        this.datosGrafica = data;
        this.error = '';
      },
      (error) => {
        this.error = 'Error al cargar los datos de la gráfica';
        console.error(error);
      }
    );
  }

  onMesAnioChange(): void {
    this.cargarDatosGrafica();
  }
}
