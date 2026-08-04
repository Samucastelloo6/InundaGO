import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MapApiService } from '../../core/routes/routes-backend.service';
import { Suggestion } from '../fullscreen-map-page.component/components/route-planner/route-planner';
import { RouteDTO } from '../../core/models/route.models';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'route-history-page.ts',
  imports: [CommonModule],
  templateUrl: './route-history-page.html',
})
export class RouteHistoryPageComponent {

  routes: RouteDTO[] = [];
  loading = true;
  error?: string;

  constructor(private mapApi: MapApiService) {
    this.mapApi.getMyRoutes().subscribe({
      next: (data) => {
        this.routes = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'No se pudieron cargar las rutas';
        this.loading = false;
      },
    });
  }

  formatDuration(min: number): string {
    if (min < 60) return `${min} min`;
    const h = Math.floor(min / 60);
    const m = min % 60;
    return m > 0 ? `${h} h ${m} min` : `${h} h`;
  }
}
