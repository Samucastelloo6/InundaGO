import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Output } from '@angular/core';
import { RouteDTO } from '../../../../../core/models/route.models';
import { MapApiService } from '../../../../../core/routes/routes-backend.service';


@Component({
  selector: 'app-history-panel',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './route-history-panel.html',
})
export class RouteHistoryPanelComponent {
  @Output() selectRoute = new EventEmitter<RouteDTO>();

  routes: RouteDTO[] = [];
  loading = true;
  error?: string;

  constructor(private mapApi: MapApiService) {
    this.loadRoutes();
  }

  loadRoutes(): void {
    this.loading = true;
    this.error = undefined;
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

  onSelect(r: RouteDTO) {
    this.selectRoute.emit(r);
  }

  formatDuration(min: number): string {
    if (min < 60) return `${min} min`;
    const h = Math.floor(min / 60);
    const m = min % 60;
    return m > 0 ? `${h} h ${m} min` : `${h} h`;
  }
}
