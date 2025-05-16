import { Component, OnInit } from '@angular/core';
import { Chart, registerables } from 'chart.js';
import { ReporteService, Reporte } from '../../services/reporte.service';

Chart.register(...registerables);

type Estado = 'Presente' | 'Tarde' | 'Ausente';

@Component({
  selector: 'app-graficas',
  templateUrl: './graficas.component.html',
  styleUrls: ['./graficas.component.css']
})
export class GraficasComponent implements OnInit {
  constructor(private reporteService: ReporteService) {}

  ngOnInit(): void {
    const reportes = this.reporteService.getReportes();
    this.renderAsistenciaGeneral(reportes);
    this.renderComparacionAsistencia(reportes);
  }

  renderAsistenciaGeneral(reportes: Reporte[]): void {
    const resumen: Record<Estado, number> = {
      Presente: 0,
      Tarde: 0,
      Ausente: 0
    };

    reportes.forEach(r => {
      const estado = r.estado as Estado;
      if (resumen[estado] !== undefined) resumen[estado]++;
    });

    const ctx = document.getElementById('attendanceChart') as HTMLCanvasElement;
    new Chart(ctx, {
      type: 'pie',
      data: {
        labels: ['Presente', 'Tarde', 'Ausente'],
        datasets: [{
          label: 'Asistencia General',
          data: [resumen['Presente'], resumen['Tarde'], resumen['Ausente']],
          backgroundColor: ['#28a745', '#ffc107', '#dc3545']
        }]
      }
    });
  }

  renderComparacionAsistencia(reportes: Reporte[]): void {
    const personas: Record<string, Record<Estado, number>> = {};

    reportes.forEach(r => {
      const estado = r.estado as Estado;

      if (!personas[r.nombre]) {
        personas[r.nombre] = {
          Presente: 0,
          Tarde: 0,
          Ausente: 0
        };
      }

      if (personas[r.nombre][estado] !== undefined) {
        personas[r.nombre][estado]++;
      }
    });

    const nombres = Object.keys(personas);
    const presente = nombres.map(n => personas[n]['Presente']);
    const tarde = nombres.map(n => personas[n]['Tarde']);
    const ausente = nombres.map(n => personas[n]['Ausente']);

    const canvas = document.createElement('canvas');
    canvas.id = 'comparacionChart';
    document.querySelector('.graficas-container')?.appendChild(canvas);

    const ctx = canvas.getContext('2d');
    new Chart(ctx!, {
      type: 'bar',
      data: {
        labels: nombres,
        datasets: [
          { label: 'Presente', data: presente, backgroundColor: '#28a745' },
          { label: 'Tarde', data: tarde, backgroundColor: '#ffc107' },
          { label: 'Ausente', data: ausente, backgroundColor: '#dc3545' }
        ]
      },
      options: {
        responsive: true,
        plugins: {
          title: { display: true, text: 'Comparación de Asistencia por Persona' }
        },
        scales: {
          y: { beginAtZero: true }
        }
      }
    });
  }
}
