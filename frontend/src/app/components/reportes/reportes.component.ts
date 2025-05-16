import { Component, OnInit } from '@angular/core';
import * as XLSX from 'xlsx';
import { saveAs } from 'file-saver';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

import { ReporteService, Reporte } from '../../services/reporte.service';

@Component({
  selector: 'app-reportes',
  templateUrl: './reportes.component.html',
  styleUrls: ['./reportes.component.css']
})
export class ReportesComponent implements OnInit {
  reportes: Reporte[] = [];
  filtroNombre: string = '';
  filtroFecha: string = '';
  filtroEstado: string = '';

  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    this.reportes = [
      { nombre: 'Juan Pérez', fecha: '2025-05-06', hora: '08:00', estado: 'Presente' },
      { nombre: 'Laura Gómez', fecha: '2025-05-06', hora: '08:05', estado: 'Tarde' },
      { nombre: 'Carlos Ruiz', fecha: '2025-05-06', hora: '08:00', estado: 'Presente' },
      { nombre: 'Marta Díaz', fecha: '2025-05-07', hora: '08:10', estado: 'Tarde' },
      { nombre: 'Andrés Suárez', fecha: '2025-05-08', hora: '-', estado: 'Ausente' },
      { nombre: 'Andrés Suárez', fecha: '2025-05-09', hora: '-', estado: 'Ausente' },
      { nombre: 'Marta Díaz', fecha: '2025-05-08', hora: '8:10', estado: 'Presente' },
      { nombre: 'Andrés Suárez', fecha: '2025-05-10', hora: '-', estado: 'Ausente' },
      { nombre: 'Carlos Ruiz', fecha: '2025-05-09', hora: '9:00', estado: 'Tarde' },
      { nombre: 'Laura Gómez', fecha: '2025-05-10', hora: '7:50', estado: 'Presente' },
      { nombre: 'Juan Pérez', fecha: '2025-05-11', hora: '-', estado: 'Ausente' }
    ];

    // Guardar en el servicio para que GraficasComponent lo use
    this.reporteService.setReportes(this.reportes);
  }

  get reportesFiltrados(): Reporte[] {
    return this.reportes.filter(r => {
      const nombreCoincide = r.nombre.toLowerCase().includes(this.filtroNombre.toLowerCase());
      const fechaCoincide = this.filtroFecha ? r.fecha === this.filtroFecha : true;
      const estadoCoincide = this.filtroEstado ? r.estado === this.filtroEstado : true;
      return nombreCoincide && fechaCoincide && estadoCoincide;
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
    autoTable(doc, {
      head: [['Nombre', 'Fecha', 'Hora', 'Estado']],
      body: this.reportesFiltrados.map(r => [r.nombre, r.fecha, r.hora, r.estado])
    });
    doc.save('reportes.pdf');
  }
}
