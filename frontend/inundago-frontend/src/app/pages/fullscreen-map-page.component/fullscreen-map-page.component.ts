import { AfterViewInit, ChangeDetectorRef, Component, CUSTOM_ELEMENTS_SCHEMA, ElementRef, inject, viewChild } from '@angular/core';
import mapboxgl from 'mapbox-gl'; // or "const mapboxgl = require('mapbox-gl');"
import { environment } from '../../../environments/environment';
import { RoutePlannerComponent, Suggestion } from './components/route-planner/route-planner';
import { MapApiService } from '../../core/routes/routes-backend.service';
import { firstValueFrom } from 'rxjs';
import { AlertDTO, CoordinateDTO, RouteDTO, RouteRequestDTO } from '../../core/models/route.models';
import { FloodZonesToggleComponent } from './components/flood-zones-toogle/flood-zones-toggle.component';
import { RouteAlertsComponent } from './components/route-alerts/route-alerts';
import { CommonModule } from '@angular/common';
import { RouteHistoryPanelComponent } from './components/route-planner/route-history-panel/route-history-panel';
import { Loading } from '../../shared/components/loading/loading';

mapboxgl.accessToken = environment.mapboxKey;

@Component({
  selector: 'app-fullscreen-map-page',
  standalone: true,
  imports: [CommonModule, RoutePlannerComponent, FloodZonesToggleComponent, RouteAlertsComponent, RouteHistoryPanelComponent, Loading],
  templateUrl: './fullscreen-map-page.component.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})


export class FullscreenMapPageComponent implements AfterViewInit {

  panelOpen = true;

  togglePanel(): void {
    this.panelOpen = !this.panelOpen;
  }

  openPanel(): void {
    this.panelOpen = true;
  }

  historyOpen = false;

  openHistoryPanel(): void {
    this.historyOpen = true;
    this.historyPanel()?.loadRoutes();
  }

  closeHistoryPanel(): void {
    this.historyOpen = false;
  }



  //Map container
  divElement = viewChild<ElementRef>('mapEl');
  planner = viewChild<RoutePlannerComponent>(RoutePlannerComponent);
  alerts = viewChild<RouteAlertsComponent>(RouteAlertsComponent);
  historyPanel = viewChild<RouteHistoryPanelComponent>(RouteHistoryPanelComponent);

  //Mapa
  map!: mapboxgl.Map;

  // Markers
  private userMarker?: mapboxgl.Marker;
  private originMarker?: mapboxgl.Marker;
  private destMarker?: mapboxgl.Marker;

  //Geolocalización
  private watchId?: number;
  private currentLng?: number;
  private currentLat?: number;

  //Guardamos cordenadas actuales
  private originCoords?: { lng: number; lat: number };
  private destinationCoords?: { lng: number; lat: number };

  private cdr = inject(ChangeDetectorRef);

  // Datos de la ruta principal
  private mainRouteData?: {
    coordenadas: CoordinateDTO[];
    duracion: number;
    distancia: number;
    riesgo: boolean;
    mensaje: string;


  };

  // Resultado de la ruta, datos que se muestran en panel
  routeDurationMin: number | null = null;
  routeDistanceKm: number | null = null;
  routeMessage: string | null = null;
  routeHasRisk: boolean = false;
  rutaAlternativa: RouteDTO | null = null;
  alertaClima: AlertDTO | null = null;


  loadingRoute = false;
  sinAlternativaSegura = false;



  //Modo seleccion con click
  private picking: 'origin' | 'destination' | null = null;


  //creo el constrcutor del servicio
  constructor(private mapApi: MapApiService) { }

  //Creo wl mapa
  async ngAfterViewInit() {
    this.cdr.detectChanges();
    if (!this.divElement()?.nativeElement) return;
    const element = this.divElement()!.nativeElement;

    this.map = new mapboxgl.Map({
      container: element, // container ID
      style: 'mapbox://styles/mapbox/streets-v12', // style URL
      center: [-0.3763, 39.4699], // starting position [lng, lat] Valencia
      zoom: 11, // starting zoom
    });


    //Cuando cargo el mapa, arranco geolocalizacion y clicks
    this.map.on('load', () => {
      this.startTrackingLocation();
      this.enableMapClickToPick();
    });
  }


  //Geolocalización
  private startTrackingLocation(): void {

    if (!navigator.geolocation) {
      alert('Tu navegador no soporta geolocalizacion');
      return

    }
    if (this.watchId != null) return;

    this.watchId = navigator.geolocation.watchPosition(
      (pos) => {
        const currentLng = pos.coords.longitude;
        const currentLat = pos.coords.latitude;

        this.currentLng = currentLng;
        this.currentLat = currentLat;

        //Marker azul(ubicacion actual)
        this.setUserMarker(currentLng, currentLat);

        //Por defecto el origen es nuestra ubicación
        if (!this.originMarker) {
          this.setOriginMarker(currentLng, currentLat);
          this.originCoords = { lng: currentLng, lat: currentLat }
          this.planner()?.setOriginFromCurrentLocation({ lng: currentLng, lat: currentLat });
        }

      },
      (err) => {
        console.error(err);
        alert('No se pudo obtener tu ubicación. Revisa permisos del navegador.');
      },
      {
        enableHighAccuracy: true,
        maximumAge: 2000,
        timeout: 10000,
      }
    );
  }


  //Creo o actualizo el marker azul (geolocalizacion)
  private setUserMarker(lng: number, lat: number): void {
    if (!this.userMarker) {
      this.userMarker = new mapboxgl.Marker({ color: 'blue' })
        .setLngLat([lng, lat])
        .addTo(this.map);

    } else {
      this.userMarker.setLngLat([lng, lat]);
    }

  }


  //Origen y destino

  //Creo o actualizo el marker rojo (origen)
  private setOriginMarker(lng: number, lat: number): void {
    if (!this.originMarker) {
      this.originMarker = new mapboxgl.Marker({ color: 'red' })
        .setLngLat([lng, lat])
        .addTo(this.map);

    } else {
      this.originMarker.setLngLat([lng, lat]);
    }
  }

  //Creo o actualizo el marker verde (Destino
  private setDestinationMarker(lng: number, lat: number): void {
    if (!this.destMarker) {
      this.destMarker = new mapboxgl.Marker({ color: 'green' })
        .setLngLat([lng, lat])
        .addTo(this.map);
    } else {
      this.destMarker.setLngLat([lng, lat]);
    }
  }

  //Click en mapa

  //Escucha clicks en el mapa y dependiendo si es origen o destino coloca
  // el marcador correspondiente
  private enableMapClickToPick(): void {
    this.map.on('click', async (e) => {
      if (!this.picking) return;

      const lng = e.lngLat.lng;
      const lat = e.lngLat.lat;

      const placeName = await this.reverseGeocode(lng, lat);

      if (this.picking === 'origin') {
        this.setOriginMarker(lng, lat);
        this.originCoords = { lng, lat };
        this.planner()?.setOriginFromMap({ lng, lat }, placeName);

      } else {
        this.setDestinationMarker(lng, lat);
        this.destinationCoords = { lng, lat };
        this.planner()?.setDestinationFromMap({ lng, lat }, placeName);
      }

      //Salgo del modo seleccion
      this.disablePickMode();

    });
  }

  //Para pasar de coordenadas a texto cuando hago click

  private async reverseGeocode(lng: number, lat: number): Promise<string> {
    const base =
      `https://api.mapbox.com/geocoding/v5/mapbox.places/${lng},${lat}.json` +
      `?language=es&access_token=${environment.mapboxKey}`;

    try {
      //  Calle
      const resAddress = await fetch(base + `&types=address&limit=1`);
      let street: string | null = null;

      if (resAddress.ok) {
        const dataAddress = await resAddress.json();
        street = dataAddress.features?.[0]?.text ?? null;
      }

      // Localidad
      const resPlace = await fetch(base + `&types=place&limit=1`);
      let city: string | null = null;

      if (resPlace.ok) {
        const dataPlace = await resPlace.json();
        city = dataPlace.features?.[0]?.text ?? null;
      }

      //  Resultado final
      if (street && city) return `${street}, ${city}`;
      if (city) return city;
      if (street) return street;

      return 'Ubicación seleccionada';
    } catch (err) {
      console.error(err);
      return 'Ubicación seleccionada';
    }
  }

  //Activa el modo de seleccion, cambia el cursor para que el usuario lo note

  enablePickMode(target: 'origin' | 'destination'): void {
    this.picking = target;
    this.map.getCanvas().style.cursor = 'crosshair';
  }

  //Desactiva el modo seleccion
  disablePickMode(): void {
    this.picking = null;
    this.map.getCanvas().style.cursor = '';
  }

  handleUseCurrentLocation(): void {
    if (this.currentLng == null || this.currentLat == null) {
      alert('Aún no tengo tu ubicación.');
      return;
    }

    this.setOriginMarker(this.currentLng, this.currentLat);
    this.originCoords = { lng: this.currentLng, lat: this.currentLat };
    this.planner()?.setOriginFromCurrentLocation({
      lng: this.currentLng,
      lat: this.currentLat,
    });
  }


  //LLamo al metodo trazar ruta despues de darle al boton

  async handleBuildRoute(ev: {
    originText: string;
    destinationText: string;
    originCoords: { lng: number; lat: number } | null;
    destinationCoords: { lng: number; lat: number } | null;

  }): Promise<void> {

    //Construimos el body que espera el backend
    const body: RouteRequestDTO = {

      direccionOrigen: ev.originText?.trim() || null,
      direccionDestino: ev.destinationText?.trim() || null,

      latOrigen: ev.originCoords?.lat ?? null,
      lngOrigen: ev.originCoords?.lng ?? null,
      latDestino: ev.destinationCoords?.lat ?? null,
      lngDestino: ev.destinationCoords?.lng ?? null,
    };


    //Validacion
    const hasOrigin =
      (body.latOrigen != null && body.lngOrigen != null) || !!body.direccionOrigen;

    const hasDest =
      (body.latDestino != null && body.lngDestino != null) || !!body.direccionDestino;

    if (!hasOrigin || !hasDest) {
      alert('Necesito ORIGEN y DESTINo para trazar la ruta');
      return;
    }

    this.loadingRoute = true;

    //Le pido al backend que calcule la ruta
    try {
      const res = await firstValueFrom(this.mapApi.calculate(body));

      console.log('========== RESPUESTA BACKEND ==========');
      console.log('Ruta principal:', res);
      console.log('Ruta alternativa:', res.rutaAlternativa);
      console.log('========================================');

      if (!res.coordenadasRuta?.length) {
        alert('No se encontró la ruta');
        return;
      }

      // guardar datos de la ruta principal
      this.mainRouteData = {
        coordenadas: res.coordenadasRuta,
        duracion: res.duracion ?? 0,
        distancia: res.distancia ?? 0,
        riesgo: res.riesgo ?? false,
        mensaje: res.mensaje ?? '',
      };

      //this.alerts()?.setAlerts(res.alertaClima ?? null);
      this.alertaClima = res.alertaClima ?? null;
      // para ruta alternativa
      this.rutaAlternativa = res.rutaAlternativa ?? null;

      //simulacro muestra totastr si no hay alternativa segura
      this.sinAlternativaSegura = (res.riesgo ?? false) && !res.rutaAlternativa;

      console.log('rutaAlternativa guardada en componente:', this.rutaAlternativa);

      // mostrar ruta principal por defecto
      this.alerts()?.setAlternativeRoute(this.rutaAlternativa);

      this.alerts()?.setShowingAlternative(false);

      // Dibujar ruta principal
      this.drawRoute(res.coordenadasRuta, res.riesgo ?? false);

      // Actualizar display
      this.updateRouteDisplay(
        res.duracion ?? 0,
        res.distancia ?? 0,
        res.riesgo ?? false,
        res.mensaje ?? ''
      );

      //MapBox necesita [lng , lat]
      const line: [number, number][] = res.coordenadasRuta.map(p => [p.lng, p.lat]);

      // Marcadores usando los extremos de la ruta
      const [startLng, startLat] = line[0];
      const [endLng, endLat] = line[line.length - 1];

      if (ev.originCoords) {
        this.setOriginMarker(ev.originCoords.lng, ev.originCoords.lat);
        this.originCoords = { lng: ev.originCoords.lng, lat: ev.originCoords.lat };
      }

      if (ev.destinationCoords) {
        this.setDestinationMarker(ev.destinationCoords.lng, ev.destinationCoords.lat);
        this.destinationCoords = { lng: ev.destinationCoords.lng, lat: ev.destinationCoords.lat };
      }

      // Si el usuario NO había seleccionado coords manualmente, usamos los extremos
      if (!ev.originCoords) {
        this.setOriginMarker(startLng, startLat);
        this.originCoords = { lng: startLng, lat: startLat };
      }

      if (!ev.destinationCoords) {
        this.setDestinationMarker(endLng, endLat);
        this.destinationCoords = { lng: endLng, lat: endLat };
      }

      // Ajustar zoom
      this.fitMapToRoute(res.coordenadasRuta);

    } catch (err) {
      console.error(err);
      alert('Error al calcular la ruta en el backend. Revisa login / conexión.');

    } finally {
      this.loadingRoute = false;
    }
  }


  //Metodo para limpiar el mapa
  clearMap(): void {
    //Borro origen y destino
    if (this.originMarker) { this.originMarker.remove(); this.originMarker = undefined; }
    if (this.destMarker) { this.destMarker.remove(); this.destMarker = undefined; }


    //Borro la ruta si existe
    const src = this.map.getSource('route') as mapboxgl.GeoJSONSource | undefined;
    if (src) {
      src.setData({
        type: 'Feature',
        properties: {},
        geometry: { type: 'LineString', coordinates: [] },
      });
    }

    //Reseteo coordenadas guardadas
    this.originCoords = undefined;
    this.destinationCoords = undefined;

    //Reseteo datos de la ruta
    this.routeDurationMin = null;
    this.routeDistanceKm = null;
    this.routeHasRisk = false;
    this.routeMessage = null;
    this.sinAlternativaSegura = false;
    this.alertaClima = null;


    // Reseteo panel
    this.planner()?.reset();
    this.alerts()?.reset();

  }

  //Creo la fuente y capa para la ruta
  private ensureRouteLayerExists(): void {
    if (this.map.getSource('route')) return;

    this.map.addSource('route', {
      type: 'geojson',
      data: {
        type: 'Feature',
        properties: {},
        geometry: { type: 'LineString', coordinates: [] },
      },
    });

    this.map.addLayer({
      id: 'route-line',
      type: 'line',
      source: 'route',
      layout: {
        'line-join': 'round',
        'line-cap': 'round',
      },
      paint: {
        'line-width': 5,
        'line-opacity': 0.9,
        'line-color': '#1f2937',
      },
    });

  }





  private drawRoute(coordenadas: CoordinateDTO[], hasRisk: boolean): void {

    this.ensureRouteLayerExists();
    this.map.moveLayer('route-line');

    const line: [number, number][] = coordenadas.map(p => [p.lng, p.lat]);

    //Dibujo la linea en el mapa
    const source = this.map.getSource('route') as mapboxgl.GeoJSONSource;
    source.setData({
      type: 'Feature',
      properties: {},
      geometry: { type: 'LineString', coordinates: line },
    });

    // Cambia de color según riesgo
    const color = hasRisk ? '#dc3545' : '#007bff';
    this.map.setPaintProperty('route-line', 'line-color', color);

  }


  // Método para mostrar ruta alternativa
  showAlternativeRoute(): void {

    if (!this.rutaAlternativa?.coordenadasRuta) {
      alert('No hay una ruta alternativa disponible')
      return;
    }

    // Dibujar ruta alternativa (sin riesgo)
    this.drawRoute(this.rutaAlternativa.coordenadasRuta, false);

    // Actualizar datos mostrados
    this.updateRouteDisplay(
      this.rutaAlternativa.duracion,
      this.rutaAlternativa.distancia,
      false,
      'Ruta alternativa segura'
    );

    this.alerts()?.setShowingAlternative(true);

    // ajustar zoom
    this.fitMapToRoute(this.rutaAlternativa.coordenadasRuta);
  }

  showMainRoute(): void {

    if (!this.mainRouteData) return;

    // Redibujar ruta principal
    this.drawRoute(this.mainRouteData.coordenadas, this.mainRouteData.riesgo);

    // Actualizar datos mostrados
    this.updateRouteDisplay(
      this.mainRouteData.duracion,
      this.mainRouteData.distancia,
      this.mainRouteData.riesgo,
      this.mainRouteData.mensaje
    )

    this.alerts()?.setShowingAlternative(false);

    //Ajustar zoom
    //const line: [number, number][] = this.rutaAlternativa?.coordenadasRuta.map(p => [p.lng, p.lat]);
    this.fitMapToRoute(this.mainRouteData.coordenadas);

  }

  // Método para actualizar los datos del panel
  private updateRouteDisplay(duracion: number, distancia: number, riesgo: boolean, mensaje: string): void {

    this.routeDurationMin = duracion;
    this.routeDistanceKm = distancia;
    this.routeHasRisk = riesgo;
    this.routeMessage = mensaje;
    this.alerts()?.setRouteRisk(mensaje, riesgo);
  }

  // Método para ajustar el zoom al mapa
  //private fitMapToRoute (line: [number, number][]): void {
  private fitMapToRoute(coordenadas: CoordinateDTO[]): void {
    const bounds = new mapboxgl.LngLatBounds();
    //line.forEach(([lng, lat]) => bounds.extend([lng, lat]));
    coordenadas.forEach(coord => bounds.extend([coord.lng, coord.lat]));
    this.map.fitBounds(bounds, { padding: 40 });
  }

  //Metodo para pinta ruta del historiañ
  viewSavedRoute(r: RouteDTO): void {
    if (!this.map || !r.coordenadasRuta?.length) return;

    // 1) dibujar la ruta guardada (usa tu capa 'route')
    this.drawRoute(r.coordenadasRuta, r.riesgo);

    // 2) actualizar panel (usa tu método)
    this.updateRouteDisplay(
      r.duracion ?? 0,
      r.distancia ?? 0,
      r.riesgo ?? false,
      r.riesgo ? 'Ruta guardada con riesgo' : 'Ruta guardada sin riesgo'
    );

    // 3) markers usando extremos
    const first = r.coordenadasRuta[0];
    const last = r.coordenadasRuta[r.coordenadasRuta.length - 1];

    this.setOriginMarker(first.lng, first.lat);
    this.originCoords = { lng: first.lng, lat: first.lat };

    this.setDestinationMarker(last.lng, last.lat);
    this.destinationCoords = { lng: last.lng, lat: last.lat };

    // 4) zoom a la ruta
    this.fitMapToRoute(r.coordenadasRuta);

    // 5) cerrar modo pick por si acaso
    this.disablePickMode();

    // 6) opcional: mostrar que NO estamos en alternativa
    this.alerts()?.setShowingAlternative(false);

    // 7) opcional: abrir panel si estaba cerrado
    this.panelOpen = true;

    // 8) opcional: borrar ruta alternativa mostrada
    this.rutaAlternativa = null;
  }

  alertTwClass(): string {
    const c = (this.alertaClima?.color ?? '').toLowerCase();
    if (c.includes('rojo')) return 'bg-red-50 border-red-200 text-red-900';
    if (c.includes('naranja')) return 'bg-orange-50 border-orange-200 text-orange-900';
    if (c.includes('amarillo')) return 'bg-yellow-50 border-yellow-200 text-yellow-900';
    if (c.includes('verde')) return 'bg-green-50 border-green-200 text-green-900';
    return 'bg-slate-50 border-slate-200 text-slate-900';
  }

  alertIcon(): string {
    const c = (this.alertaClima?.color ?? '').toLowerCase();
    if (c.includes('rojo')) return 'meteocons:code-red-fill';
    if (c.includes('naranja')) return 'meteocons:code-orange-fill';
    if (c.includes('amarillo')) return 'meteocons:code-yellow-fill';
    if (c.includes('verde')) return 'meteocons:code-green-fill';
    return 'meteocons:code-green-fill';
  }

  dismissToast(): void {
    this.alertaClima = null;
  }

}
