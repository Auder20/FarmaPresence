import {
  AfterViewInit,
  Component,
  ElementRef,
  OnInit,
  ViewChild,
} from '@angular/core';
import Chart from 'chart.js/auto';
import { forkJoin } from 'rxjs';
import { ReporteService, Reporte } from '../../services/reporte.service';
import { RegistroEmpleadosService } from '../../services/registro-empleados.service';

interface Empleado {
  id: number;
  nombre: string;
}

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css'],
})
export class ReportesComponent implements OnInit, AfterViewInit {
  filtroNombre: string = '';
  filtroDia: string = '';
  filtroMes: string = '';
  filtroAnio: string = '';
  filtroEstado: string = '';

  fechaInicio: string = '';
  fechaFin: string = '';

  reportes: Reporte[] = [];
  reportesFiltrados: Reporte[] = [];
  empleados: Empleado[] = [];

  dias: string[] = Array.from({ length: 31 }, (_, i) => (i + 1).toString());
  meses = [
    { name: 'Enero', value: '1' },
    { name: 'Febrero', value: '2' },
    { name: 'Marzo', value: '3' },
    { name: 'Abril', value: '4' },
    { name: 'Mayo', value: '5' },
    { name: 'Junio', value: '6' },
    { name: 'Julio', value: '7' },
    { name: 'Agosto', value: '8' },
    { name: 'Septiembre', value: '9' },
    { name: 'Octubre', value: '10' },
    { name: 'Noviembre', value: '11' },
    { name: 'Diciembre', value: '12' },
  ];
  anios: string[] = ['2023', '2024', '2025'];

  @ViewChild('estadoCanvas') estadoCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('asistenciaCanvas') asistenciaCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('comparacionCanvas') comparacionCanvas!: ElementRef<HTMLCanvasElement>;

  private estadoChart?: Chart;
  private asistenciaChart?: Chart;
  private comparacionChart?: Chart;

  constructor(
    private reporteService: ReporteService,
    private empleadosService: RegistroEmpleadosService
  ) {}

  ngOnInit(): void {
    this.cargarDatosCompletos();
  }

  ngAfterViewInit() {
    // Las gráficas se generan luego de cargar datos
  }

  cargarDatosCompletos(): void {
    forkJoin({
      empleados: this.empleadosService.getAllEmpleados(),
      reportes: this.reporteService.getReportesDesdeBackend(),
    }).subscribe(
      ({ empleados, reportes }) => {
        this.empleados = empleados;

        this.reportes = reportes.map((r: Reporte) => {
          return {
            ...r,
            nombre: r.empleado ? r.empleado.nombre : 'Desconocido',
            hora: r.horaEntrada, // para facilitar acceso en la tabla
          };
        });

        this.reportesFiltrados = [...this.reportes];
        this.generarGraficas();
      },
      (error) => {
        console.error('Error cargando empleados o reportes', error);
      }
    );
  }

  onFiltroChange() {
    this.reportesFiltrados = this.reportes.filter((r) => {
      return (
        (!this.filtroNombre ||
          (r.nombre?.toLowerCase() ?? '').includes(this.filtroNombre.toLowerCase())) &&
        (!this.filtroDia || r.fecha.split('-')[2] === this.filtroDia) &&
        (!this.filtroMes || r.fecha.split('-')[1] === this.filtroMes.padStart(2, '0')) &&
        (!this.filtroAnio || r.fecha.split('-')[0] === this.filtroAnio) &&
        (!this.filtroEstado || r.estado === this.filtroEstado)
      );
    });
    this.generarGraficas();
  }

  exportarExcel() {
    // Implementa la exportación si deseas
  }

  descargarReportePDF() {
    if (!this.fechaInicio || !this.fechaFin) {
      alert('Por favor, selecciona fecha de inicio y fin.');
      return;
    }
    if (this.fechaInicio > this.fechaFin) {
      alert('La fecha de inicio no puede ser mayor que la fecha fin.');
      return;
    }

    this.reporteService
      .descargarReporteCumplimientoGeneral(this.fechaInicio, this.fechaFin)
      .subscribe(
        (blob) => {
          const url = window.URL.createObjectURL(blob);
          const a = document.createElement('a');
          a.href = url;
          a.download = `reporte_cumplimiento_${this.fechaInicio}_a_${this.fechaFin}.pdf`;
          a.click();
          window.URL.revokeObjectURL(url);
        },
        (error: any) => {
          console.error('Error descargando el reporte PDF', error);
          alert('Error al descargar el reporte PDF.');
        }
      );
  }

  private generarGraficas() {
    this.generarGraficaEstado();
    this.generarGraficaAsistenciasPorMes();
    this.generarGraficaComparacionPorEmpleado();
  }

  private generarGraficaEstado() {
    if (this.estadoChart) {
      this.estadoChart.destroy();
    }

    const estadoCounts = this.reportesFiltrados.reduce(
      (acc, curr) => {
        if (curr.estado === 'Presente') acc.Presente++;
        else if (curr.estado === 'Tarde') acc.Tarde++;
        else if (curr.estado === 'Ausente') acc.Ausente++;
        return acc;
      },
      { Presente: 0, Tarde: 0, Ausente: 0 }
    );

    const ctx = this.estadoCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    this.estadoChart = new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Presente', 'Tarde', 'Ausente'],
        datasets: [
          {
            data: [
              estadoCounts.Presente,
              estadoCounts.Tarde,
              estadoCounts.Ausente,
            ],
            backgroundColor: ['#4caf50', '#ff9800', '#f44336'],
          },
        ],
      },
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' },
          title: { display: true, text: 'Estado de Asistencia' },
        },
      },
    });
  }

  private generarGraficaAsistenciasPorMes() {
    if (this.asistenciaChart) {
      this.asistenciaChart.destroy();
    }

    const asistenciasPorMes: { [key: string]: number } = {};

   this.reportesFiltrados.forEach((r) => {
  if (r.estado !== 'Ausente') {
    const mes = r.fecha.split('-')[1]; // ejemplo: "05"
    asistenciasPorMes[mes] = (asistenciasPorMes[mes] || 0) + 1;
  }
});


    const labels = this.meses.map((m) => m.name);
    const data = this.meses.map((m) => {
      const mesConCero = m.value.padStart(2, '0'); // "01", "02", ...
      return asistenciasPorMes[mesConCero] || 0;
    });

    const ctx = this.asistenciaCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    this.asistenciaChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Asistencias',
            data: data,
            backgroundColor: '#2196f3',
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
          },
        },
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Asistencias por Mes' },
        },
      },
    });
  }

  private generarGraficaComparacionPorEmpleado() {
    if (this.comparacionChart) {
      this.comparacionChart.destroy();
    }

    const asistenciasPorEmpleado: { [key: string]: number } = {};

 this.reportesFiltrados.forEach((r) => {
  if (r.nombre && r.estado !== 'Ausente') {
    asistenciasPorEmpleado[r.nombre] = (asistenciasPorEmpleado[r.nombre] || 0) + 1;
  }
});


    const labels = Object.keys(asistenciasPorEmpleado);
    const data = labels.map((label) => asistenciasPorEmpleado[label]);

    const ctx = this.comparacionCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    this.comparacionChart = new Chart(ctx, {
      type: 'bar',
      data: {
        labels: labels,
        datasets: [
          {
            label: 'Asistencias',
            data: data,
            backgroundColor: '#673ab7',
          },
        ],
      },
      options: {
        responsive: true,
        scales: {
          y: {
            beginAtZero: true,
            ticks: { stepSize: 1 },
          },
        },
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Comparación por Empleado' },
        },
      },
    });
  }
}
