import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, CUSTOM_ELEMENTS_SCHEMA, signal, viewChild } from '@angular/core';
import { environment } from '../../../../../environments/environment';
import { AlertDTO, RouteDTO } from '../../../../core/models/route.models';
import { OriginComponent } from './origin-picker/origin-picker';
import { DestinationComponent } from './destination-picker/destination-picker';

export type LatLng = { lng: number; lat: number };
export type PickTarget = 'origin' | 'destination';

//Para  autocompletar
export type Suggestion = { title: string; subtitle: string; lng: number; lat: number };

@Component({
  selector: 'app-route-planner',
  standalone: true,
  imports: [CommonModule, OriginComponent, DestinationComponent],
  templateUrl: './route-planner.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class RoutePlannerComponent {

  origin = viewChild<OriginComponent>(OriginComponent);

  destination = viewChild<DestinationComponent>(DestinationComponent);


  //Modo seleccion en mapa
  picking = signal<PickTarget | null>(null);

  @Output() requestUseCurrentLocation = new EventEmitter<void>();
  @Output() startPickOnMap = new EventEmitter<PickTarget>();
  @Output() stopPickOnMap = new EventEmitter<void>();
  @Output() clear = new EventEmitter<void>();

  @Input() routeDurationMin: number | null = null;
  @Input() routeDistanceKm: number | null = null;
  @Input() routeMessage: string | null = null;
  @Input() routeHasRisk: boolean = false;


  @Output() requestBuildRoute = new EventEmitter<{
    originText: string;
    destinationText: string;
    originCoords: LatLng | null;
    destinationCoords: LatLng | null;
  }>();

  useCurrentLocation() {
    this.picking.set(null);
    this.requestUseCurrentLocation.emit();

  }
  togglePick(target: PickTarget) {
    const next = this.picking() === target ? null : target;
    this.picking.set(next);
    if (next) this.startPickOnMap.emit(next);
    else this.stopPickOnMap.emit();

  }

  buildRoute() {
    const originText = this.origin()?.getOriginText() ?? '';
    const originCoords = this.origin()?.getOriginCoords() ?? null;
    const destinationText = this.destination()?.getDestinationText() ?? '';
    const destinationCoords = this.destination()?.getDestinationCoords() ?? null;

    this.requestBuildRoute.emit({
      originText,
      destinationText,
      originCoords,
      destinationCoords,
    });
  }


  setOriginFromMap(coords: LatLng, label: string = 'Origen seleccionado') {
    this.origin()?.setOriginFromMap(coords, label);
  }

  setOriginFromCurrentLocation(coords: LatLng, label: string = 'Ubicación actual') {
    this.origin()?.setOriginFromCurrentLocation(coords, label);
  }


  setDestinationFromMap(coords: LatLng, label: string = 'Destino seleccionado') {
    this.destination()?.setDestinationFromMap(coords, label);
  }



  reset(): void {
    this.origin()?.reset();
    this.destination()?.reset();
    this.picking.set(null);
  }
}
