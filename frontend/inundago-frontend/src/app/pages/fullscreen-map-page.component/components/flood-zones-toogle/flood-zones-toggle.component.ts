import { Component, EventEmitter, Output, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MapApiService } from '../../../../core/routes/routes-backend.service';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-flood-zones-toggle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './flood-zones-toggle.component.html',
})
export class FloodZonesToggleComponent implements OnInit {

  @Input({ required: true }) map!: mapboxgl.Map;


  //zonas inundables
  private floodSourceId = 'flood-zones';
  private floodFillLayerId = 'flood-zones-fill';
  private floodLineLayerId = 'flood-zones-line';
  showFloodZones = false;
  private floodLoaded = false;

  constructor(private mapApi: MapApiService) { }

  ngOnInit(): void {
    if (!this.map) return;

    if (this.map.loaded()) {
      this.ensureFloodZonesLayerExists();
    } else {
      this.map.once('load', () => this.ensureFloodZonesLayerExists());
    }
  }


  @Output() change = new EventEmitter<Event>();

  onChange(event: Event) {
    this.change.emit(event);
  }


  async toggleFloodZones(event: Event): Promise<void> {
    this.showFloodZones = (event.target as HTMLInputElement).checked;

    const visibility = this.showFloodZones ? 'visible' : 'none';

    // Por si el usuario toca el toggle antes de que el mapa haya cargado del todo:
    if (!this.map.getSource(this.floodSourceId)) {
      if (this.map.loaded()) this.ensureFloodZonesLayerExists();
      else this.map.once('load', () => this.ensureFloodZonesLayerExists());
    }

    this.map.setLayoutProperty(this.floodFillLayerId, 'visibility', visibility);
    this.map.setLayoutProperty(this.floodLineLayerId, 'visibility', visibility);

    if (this.showFloodZones) {
      await this.loadFloodZonesFromBackend();
    }
  }


  private ensureFloodZonesLayerExists(): void {

    if (this.map.getSource(this.floodSourceId)) return;

    // Fuente vacía
    this.map.addSource(this.floodSourceId, {
      type: 'geojson',
      data: {
        type: 'FeatureCollection',
        features: []
      }
    });

    // Relleno
    this.map.addLayer({
      id: this.floodFillLayerId,
      type: 'fill',
      source: this.floodSourceId,
      layout: { visibility: 'none' },
      paint: {
        'fill-color': '#ef4444',
        'fill-opacity': 0.35
      }
    });

    // Borde
    this.map.addLayer({
      id: this.floodLineLayerId,
      type: 'line',
      source: this.floodSourceId,
      layout: { visibility: 'none' },
      paint: {
        'line-color': '#991b1b',
        'line-width': 2
      }
    });
  }

  private async loadFloodZonesFromBackend(): Promise<void> {
    if (this.floodLoaded) return;

    try {
      const geojson = await firstValueFrom(this.mapApi.getFloodZones());
      const src = this.map.getSource(this.floodSourceId) as mapboxgl.GeoJSONSource;
      src.setData(geojson);
      this.floodLoaded = true;
    } catch (e) {
      console.error(e);
      alert('No se pudieron cargar las zonas inundables');
    }
  }


}
