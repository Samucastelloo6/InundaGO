import { CommonModule } from "@angular/common";
import { Component, CUSTOM_ELEMENTS_SCHEMA, Input, Output, EventEmitter, signal } from "@angular/core";
import { AlertDTO, RouteDTO } from "../../../../core/models/route.models";

@Component({
  selector: 'app-route-alerts',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './route-alerts.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class RouteAlertsComponent {

  alertaClima = signal<AlertDTO | null>(null);
  showingAlternative = signal<boolean>(false);
  routeMessage = signal<string | null>(null);
  routeHasRisk = signal<boolean>(false);

  // la alternativa NO la movemos de momento (se queda en la page)
  rutaAlternativa = signal<RouteDTO | null>(null);

  @Output() showAlternativeRoute = new EventEmitter<void>();
  @Output() showMainRoute = new EventEmitter<void>();

  setAlerts(alerta: AlertDTO | null): void {
    this.alertaClima.set(alerta);
  }

  setShowingAlternative(value: boolean): void {
    this.showingAlternative.set(value);
  }

  setAlternativeRoute(route: RouteDTO | null): void {
    this.rutaAlternativa.set(route);
  }

  reset(): void {
    this.alertaClima.set(null);
    this.rutaAlternativa.set(null);
    this.showingAlternative.set(false);
    this.routeMessage.set(null);
    this.routeHasRisk.set(false);
  }

  //cambio el color de la alerta
  alertTwClass(): string {
    const c = (this.alertaClima()?.color ?? '').toLowerCase();

    if (c.includes('rojo')) return 'bg-red-50 border-red-200 text-red-900';
    if (c.includes('naranja')) return 'bg-orange-50 border-orange-200 text-orange-900';
    if (c.includes('amarillo')) return 'bg-yellow-50 border-yellow-200 text-yellow-900';
    if (c.includes('verde')) return 'bg-green-50 border-green-200 text-green-900';

    return 'bg-slate-50 border-slate-200 text-slate-900';
  }

  badgeTwClass(): string {
    const c = (this.alertaClima()?.color ?? '').toLowerCase();

    if (c.includes('rojo')) return 'bg-red-100 border-red-300 text-red-700';
    if (c.includes('naranja')) return 'bg-orange-100 border-orange-300 text-orange-700';
    if (c.includes('amarillo')) return 'bg-yellow-100 border-yellow-300 text-yellow-800';
    if (c.includes('verde')) return 'bg-green-100 border-green-300 text-green-700';

    return 'bg-slate-100 border-slate-300 text-slate-700';
  }

  alertIcon(): string {
    const c = (this.alertaClima()?.color ?? '').toLowerCase();

    if (c.includes('rojo')) return 'meteocons:code-red-fill';
    if (c.includes('naranja')) return 'meteocons:code-orange-fill';
    if (c.includes('amarillo')) return 'meteocons:code-yellow-fill';
    if (c.includes('verde')) return 'meteocons:code-green-fill';

    return 'meteocons:code-green-fill';
  }

  setRouteRisk(message: string | null, hasRisk: boolean): void {
    this.routeMessage.set(message);
    this.routeHasRisk.set(hasRisk);
  }

}
