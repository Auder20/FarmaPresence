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

import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';

import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

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
    const fechaInicioDate = this.fechaInicio ? new Date(this.fechaInicio) : null;
    const fechaFinDate = this.fechaFin ? new Date(this.fechaFin) : null;

    this.reportesFiltrados = this.reportes.filter((r) => {
      const fechaReporte = new Date(r.fecha);

      const cumpleFecha =
        (!fechaInicioDate || fechaReporte >= fechaInicioDate) &&
        (!fechaFinDate || fechaReporte <= fechaFinDate);

      return (
        (!this.filtroNombre ||
          (r.nombre?.toLowerCase() ?? '').includes(this.filtroNombre.toLowerCase())) &&
        (!this.filtroDia || r.fecha.split('-')[2] === this.filtroDia) &&
        (!this.filtroMes || r.fecha.split('-')[1] === this.filtroMes.padStart(2, '0')) &&
        (!this.filtroAnio || r.fecha.split('-')[0] === this.filtroAnio) &&
        (!this.filtroEstado || r.estado === this.filtroEstado) &&
        cumpleFecha
      );
    });

    this.generarGraficas();
  }

exportarExcel() {
  if (this.reportesFiltrados.length === 0) {
    alert('No hay datos para exportar.');
    return;
  }

  // Preparar filtros como array de arrays (filas)
  const filtros = [];
  if (this.filtroNombre) filtros.push([`Nombre: ${this.filtroNombre}`]);
  if (this.filtroDia) filtros.push([`Día: ${this.filtroDia}`]);
  if (this.filtroMes)
    filtros.push([
      `Mes: ${this.meses.find((m) => m.value === this.filtroMes)?.name || this.filtroMes}`,
    ]);
  if (this.filtroAnio) filtros.push([`Año: ${this.filtroAnio}`]);
  if (this.filtroEstado) filtros.push([`Estado: ${this.filtroEstado}`]);
  if (this.fechaInicio) filtros.push([`Desde: ${this.fechaInicio}`]);
  if (this.fechaFin) filtros.push([`Hasta: ${this.fechaFin}`]);

  // Crear encabezado con título centrado en primera fila
  const headerRows = [
    ['Reporte de Asistencia'],
    ...filtros,
    [], // fila vacía para separación
  ];

  // Mapea datos para exportar
  const datosExportar = this.reportesFiltrados.map((r) => ({
    Empleado: r.nombre,
    Fecha: r.fecha,
    Estado: r.estado,
    Hora: r.horaEntrada || r.hora || '',
    Motivo: r.motivo || '',
  }));

  // Crear hoja con encabezados
  const ws = XLSX.utils.aoa_to_sheet(headerRows);

  // Agregar datos con encabezados desde fila 5 (índice 4)
  XLSX.utils.sheet_add_json(ws, datosExportar, { origin: 4 });

  // Ajustar ancho columnas
  ws['!cols'] = [
    { wch: 20 }, // Empleado
    { wch: 15 }, // Fecha
    { wch: 12 }, // Estado
    { wch: 10 }, // Hora
    { wch: 30 }, // Motivo
  ];

  // Centrar título en primera fila
  // XLSX.js no soporta estilos en navegador, para eso usar ExcelJS

  // Crear libro y agregar hoja
  const workbook = XLSX.utils.book_new();
  XLSX.utils.book_append_sheet(workbook, ws, 'Reportes');

  // Guardar archivo
  const excelBuffer: any = XLSX.write(workbook, {
    bookType: 'xlsx',
    type: 'array',
  });

  const data = new Blob([excelBuffer], {
    type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
  });

  saveAs(data, this.getNombreArchivo('xlsx'));
}


descargarReportePDF() {
  if (this.reportesFiltrados.length === 0) {
    alert('No hay datos para exportar.');
    return;
  }

  const doc = new jsPDF('p', 'pt', 'a4');
  const pageWidth = doc.internal.pageSize.getWidth();
  const pageHeight = doc.internal.pageSize.getHeight();
  const margin = 40;
  const usableWidth = pageWidth - margin * 2;
  const usableHeight = pageHeight - margin * 2;

  // Título
  doc.setFontSize(20);
  doc.setFont('helvetica', 'bold');
  doc.text('Reporte de Asistencia', margin, margin);

  // Filtros como lista (línea por línea)
  doc.setFontSize(11);
  doc.setFont('helvetica', 'normal');
  const filtros = [];
  if (this.filtroNombre) filtros.push(`Nombre: ${this.filtroNombre}`);
  if (this.filtroDia) filtros.push(`Día: ${this.filtroDia}`);
  if (this.filtroMes)
    filtros.push(
      `Mes: ${this.meses.find((m) => m.value === this.filtroMes)?.name || this.filtroMes}`
    );
  if (this.filtroAnio) filtros.push(`Año: ${this.filtroAnio}`);
  if (this.filtroEstado) filtros.push(`Estado: ${this.filtroEstado}`);
  if (this.fechaInicio) filtros.push(`Desde: ${this.fechaInicio}`);
  if (this.fechaFin) filtros.push(`Hasta: ${this.fechaFin}`);

  let currentY = margin + 25;
  doc.text('Filtros aplicados:', margin, currentY);
  currentY += 15;
  filtros.forEach((f) => {
    doc.text(`- ${f}`, margin + 10, currentY);
    currentY += 15;
  });
  if (filtros.length === 0) {
    doc.text('- Ninguno', margin + 10, currentY);
    currentY += 15;
  }

  // Gráficas tamaño y posiciones
  const graphWidth = (usableWidth - 20) / 2; // dos gráficos lado a lado con 20 pts separación
  const graphHeight = 140;

  // Insertar gráfica Estado (izquierda)
  const estadoImg = this.estadoCanvas.nativeElement.toDataURL('image/png');
  doc.addImage(estadoImg, 'PNG', margin, currentY, graphWidth, graphHeight);

  // Insertar gráfica Asistencias por Mes (derecha)
  const asistenciaImg = this.asistenciaCanvas.nativeElement.toDataURL('image/png');
  doc.addImage(asistenciaImg, 'PNG', margin + graphWidth + 20, currentY, graphWidth, graphHeight);

  currentY += graphHeight + 30;

  // Nueva página para gráfica Comparación y tabla
  doc.addPage();

  // Insertar gráfica Comparación por Empleado ancho completo
  const comparacionImg = this.comparacionCanvas.nativeElement.toDataURL('image/png');
  const comparacionHeight = 160;
  doc.addImage(comparacionImg, 'PNG', margin, margin, usableWidth, comparacionHeight);

  // Tabla datos debajo
  const columnas = ['Empleado', 'Fecha', 'Estado', 'Hora'];
  const filas = this.reportesFiltrados.map((r) => [
    r.nombre || '',
    r.fecha || '',
    r.estado || '',
    r.horaEntrada || r.hora || '',
  ]);

  autoTable(doc, {
    head: [columnas],
    body: filas,
    startY: margin + comparacionHeight + 20,
    margin: { left: margin, right: margin },
    styles: { fontSize: 8, cellPadding: 3 },
    headStyles: { fillColor: [41, 128, 185], textColor: 255, fontStyle: 'bold' },
    alternateRowStyles: { fillColor: [245, 245, 245] },
    didDrawPage: (data) => {
      const pageNumber = doc.getNumberOfPages();
      doc.setFontSize(9);
      doc.setTextColor(150);
      doc.text(
        `Página ${pageNumber}`,
        pageWidth / 2,
        pageHeight - 10,
        { align: 'center' }
      );
    },
  });

  doc.save(this.getNombreArchivo('pdf'));
}



  // Función auxiliar para generar nombre de archivo basado en filtros
  private getNombreArchivo(ext: 'pdf' | 'xlsx'): string {
    let nombre = 'reportes';

    if (this.filtroNombre) nombre += `_nombre_${this.filtroNombre.replace(/\s+/g, '_')}`;
    if (this.filtroDia) nombre += `_dia_${this.filtroDia}`;
    if (this.filtroMes) nombre += `_mes_${this.filtroMes}`;
    if (this.filtroAnio) nombre += `_anio_${this.filtroAnio}`;
    if (this.filtroEstado) nombre += `_estado_${this.filtroEstado}`;
    if (this.fechaInicio) nombre += `_desde_${this.fechaInicio}`;
    if (this.fechaFin) nombre += `_hasta_${this.fechaFin}`;

    return `${nombre}.${ext}`;
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
        asistenciasPorEmpleado[r.nombre] =
          (asistenciasPorEmpleado[r.nombre] || 0) + 1;
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
