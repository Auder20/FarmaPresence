import { Component, OnInit, AfterViewInit, ViewChild, ElementRef } from '@angular/core';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

import { ReporteService, Reporte } from '../../services/reporte.service';

import { Chart, registerables } from 'chart.js';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit, AfterViewInit {
  reportes: Reporte[] = [];

  filtroNombre: string = '';
  filtroDia: string = '';
  filtroMes: string = '';
  filtroAnio: string = '';
  filtroEstado: string = '';
  motivoFiltro: string = '';

  dias: string[] = [];
  meses = [
    { value: '01', name: 'Enero' },
    { value: '02', name: 'Febrero' },
    { value: '03', name: 'Marzo' },
    { value: '04', name: 'Abril' },
    { value: '05', name: 'Mayo' },
    { value: '06', name: 'Junio' },
    { value: '07', name: 'Julio' },
    { value: '08', name: 'Agosto' },
    { value: '09', name: 'Septiembre' },
    { value: '10', name: 'Octubre' },
    { value: '11', name: 'Noviembre' },
    { value: '12', name: 'Diciembre' }
  ];
  anios: string[] = [];

  estadoChart: Chart | undefined;
  asistenciaChart: Chart | undefined;
  comparacionChart: Chart | undefined;

  @ViewChild('estadoCanvas') estadoCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('asistenciaCanvas') asistenciaCanvas!: ElementRef<HTMLCanvasElement>;
  @ViewChild('comparacionCanvas') comparacionCanvas!: ElementRef<HTMLCanvasElement>;

  constructor(private reporteService: ReporteService) {
    Chart.register(...registerables);
  }

  ngOnInit(): void {
    this.reportes = [
      { nombre: 'Juan Pérez', fecha: '2025-01-06', hora: '08:00', estado: 'Presente' },
      { nombre: 'Laura Gómez', fecha: '2025-05-06', hora: '08:05', estado: 'Tarde' },
      { nombre: 'Carlos Ruiz', fecha: '2025-02-06', hora: '08:00', estado: 'Presente' },
      { nombre: 'Marta Díaz', fecha: '2022-07-07', hora: '08:10', estado: 'Tarde' },
      { nombre: 'Andrés Suárez', fecha: '2025-05-08', hora: '-', estado: 'Ausente' },
      { nombre: 'Andrés Suárez', fecha: '2023-05-09', hora: '-', estado: 'Ausente' },
      { nombre: 'Marta Díaz', fecha: '2024-06-08', hora: '8:10', estado: 'Presente' },
      { nombre: 'Andrés Suárez', fecha: '2025-05-10', hora: '-', estado: 'Ausente' },
      { nombre: 'Carlos Ruiz', fecha: '2025-05-09', hora: '9:00', estado: 'Tarde' },
      { nombre: 'Laura Gómez', fecha: '2025-05-10', hora: '7:50', estado: 'Presente' },
      { nombre: 'Juan Pérez', fecha: '2025-05-11', hora: '-', estado: 'Ausente' },
      { nombre: 'Guillermo Humanez', fecha: '2025-05-20', hora: '-', estado: 'Ausente' },
    ];

    this.reporteService.setReportes(this.reportes);

    this.dias = Array.from({ length: 31 }, (_, i) => (i + 1).toString().padStart(2, '0'));
    const currentYear = new Date().getFullYear();
    for (let y = 2020; y <= currentYear; y++) {
      this.anios.push(y.toString());
    }
  }

  ngAfterViewInit(): void {
    this.crearGraficas();
  }

  onFiltroChange() {
    this.actualizarGraficas();
  }

  get reportesFiltrados(): Reporte[] {
    return this.reportes.filter(r => {
      const fecha = new Date(r.fecha);

      const dia = fecha.getDate().toString().padStart(2, '0');
      const mes = (fecha.getMonth() + 1).toString().padStart(2, '0');
      const anio = fecha.getFullYear().toString();

      const diaMatch = this.filtroDia ? dia === this.filtroDia : true;
      const mesMatch = this.filtroMes ? mes === this.filtroMes : true;
      const anioMatch = this.filtroAnio ? anio === this.filtroAnio : true;

      const nombreMatch = this.filtroNombre
        ? r.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase())
        : true;

      const estadoMatch = this.filtroEstado ? r.estado === this.filtroEstado : true;

      return diaMatch && mesMatch && anioMatch && nombreMatch && estadoMatch;
    });
  }

  crearGraficas() {
    this.crearEstadoChart();
    this.crearAsistenciaChart();
    this.crearComparacionChart();
  }

  actualizarGraficas() {
    if (this.estadoChart) {
      this.estadoChart.data = this.generarDatosEstado();
      this.estadoChart.update();
    }
    if (this.asistenciaChart) {
      this.asistenciaChart.data = this.generarDatosAsistencia();
      this.asistenciaChart.update();
    }
    if (this.comparacionChart) {
      this.comparacionChart.data = this.generarDatosComparacion();
      this.comparacionChart.update();
    }
  }

  private generarDatosEstado() {
    const estados: Record<'Presente' | 'Tarde' | 'Ausente', number> = {
      Presente: 0,
      Tarde: 0,
      Ausente: 0,
    };

    this.reportesFiltrados.forEach(r => {
      const estadoKey = r.estado as 'Presente' | 'Tarde' | 'Ausente';
      estados[estadoKey] = (estados[estadoKey] || 0) + 1;
    });

    return {
      labels: ['Presente', 'Tarde', 'Ausente'],
      datasets: [{
        label: 'Estado Asistencia',
        data: [estados.Presente, estados.Tarde, estados.Ausente],
        backgroundColor: ['#4caf50', '#ff9800', '#f44336'],
      }]
    };
  }

  private crearEstadoChart() {
    if (this.estadoChart) {
      this.estadoChart.destroy();
    }
    this.estadoChart = new Chart(this.estadoCanvas.nativeElement, {
      type: 'pie',
      data: this.generarDatosEstado(),
      options: {
        responsive: true,
        plugins: {
          legend: { position: 'bottom' },
          title: { display: true, text: 'Estado de Asistencia' }
        }
      }
    });
  }

  private generarDatosAsistencia() {
    const conteoMes: Record<string, number> = {};
    this.reportesFiltrados.forEach(r => {
      const mes = new Date(r.fecha).toLocaleString('es-CO', { month: 'long', year: 'numeric' });
      conteoMes[mes] = (conteoMes[mes] || 0) + 1;
    });

    const labels = Object.keys(conteoMes);
    const data = labels.map(l => conteoMes[l]);

    return {
      labels,
      datasets: [{
        label: 'Asistencias por Mes',
        data,
        backgroundColor: '#2196f3'
      }]
    };
  }

  private crearAsistenciaChart() {
    if (this.asistenciaChart) {
      this.asistenciaChart.destroy();
    }
    this.asistenciaChart = new Chart(this.asistenciaCanvas.nativeElement, {
      type: 'bar',
      data: this.generarDatosAsistencia(),
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Asistencias por Mes' }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }

  private generarDatosComparacion() {
    const conteoEmpleados: Record<string, number> = {};
    this.reportesFiltrados.forEach(r => {
      conteoEmpleados[r.nombre] = (conteoEmpleados[r.nombre] || 0) + 1;
    });

    const labels = Object.keys(conteoEmpleados);
    const data = labels.map(nombre => conteoEmpleados[nombre]);

    return {
      labels,
      datasets: [{
        label: 'Cantidad de reportes por empleado',
        data,
        backgroundColor: '#673ab7'
      }]
    };
  }

  private crearComparacionChart() {
    if (this.comparacionChart) {
      this.comparacionChart.destroy();
    }
    this.comparacionChart = new Chart(this.comparacionCanvas.nativeElement, {
      type: 'bar',
      data: this.generarDatosComparacion(),
      options: {
        responsive: true,
        plugins: {
          legend: { display: false },
          title: { display: true, text: 'Comparación por Empleado' }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }

  exportarExcel(): void {
    const worksheet = XLSX.utils.json_to_sheet(this.reportesFiltrados);
    const workbook = { Sheets: { 'Reportes': worksheet }, SheetNames: ['Reportes'] };
    const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
    const blob = new Blob([excelBuffer], { type: 'application/octet-stream' });
    saveAs(blob, 'reportes.xlsx');
  }

  exportarPDF(): void {
    const doc = new jsPDF();
    let yPos = 10;

    if (this.estadoCanvas) {
      const estadoImg = this.estadoCanvas.nativeElement.toDataURL('image/png');
      const imgWidth = 180;
      const imgHeight = (this.estadoCanvas.nativeElement.height * imgWidth) / this.estadoCanvas.nativeElement.width;
      doc.addImage(estadoImg, 'PNG', 15, yPos, imgWidth, imgHeight);
      yPos += imgHeight + 10;
    }

    if (this.asistenciaCanvas) {
      const asistenciaImg = this.asistenciaCanvas.nativeElement.toDataURL('image/png');
      const imgWidth = 180;
      const imgHeight = (this.asistenciaCanvas.nativeElement.height * imgWidth) / this.asistenciaCanvas.nativeElement.width;
      doc.addImage(asistenciaImg, 'PNG', 15, yPos, imgWidth, imgHeight);
      yPos += imgHeight + 10;
    }

    if (this.comparacionCanvas) {
      const comparacionImg = this.comparacionCanvas.nativeElement.toDataURL('image/png');
      const imgWidth = 180;
      const imgHeight = (this.comparacionCanvas.nativeElement.height * imgWidth) / this.comparacionCanvas.nativeElement.width;
      doc.addImage(comparacionImg, 'PNG', 15, yPos, imgWidth, imgHeight);
      yPos += imgHeight + 10;
    }

    autoTable(doc, {
      startY: yPos,
      head: [['Nombre', 'Fecha', 'Hora', 'Estado']],
      body: this.reportesFiltrados.map(r => [r.nombre, r.fecha, r.hora, r.estado]),
      theme: 'grid',
      headStyles: { fillColor: [46, 101, 164] },
      styles: { fontSize: 9 }
    });

    doc.save('reportes_completos.pdf');
  }
}
