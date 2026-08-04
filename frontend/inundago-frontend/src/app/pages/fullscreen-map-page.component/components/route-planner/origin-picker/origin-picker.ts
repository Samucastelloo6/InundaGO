import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output, signal, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { environment } from "../../../../../../environments/environment";
import { LatLng, Suggestion } from "../route-planner";




@Component({
  selector: 'app-origin',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './origin-picker.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class OriginComponent {

  @Output() requestUseCurrentLocation = new EventEmitter<void>();
  @Output() togglePick = new EventEmitter<void>();

  @Input() pickingActive: boolean = false;


  //Modo escribir
  originText = signal<string>('Ubicacion Actual');

  //Labels
  originLabel = signal<string>('Ubicación actual');

  //coords modo click
  originCoords = signal<LatLng | null>(null);

  // Listas de sugerencias para mostrar debajo de cada input
  originSuggestions = signal<Suggestion[]>([]);

  getOriginText(): string {
    return this.originText().trim();
  }

  getOriginCoords(): LatLng | null {
    return this.originCoords();
  }

  //Llamo desde la page
  setOriginFromMap(coords: LatLng, label: string = 'Origen seleccionado') {
    this.originCoords.set(coords);
    this.originLabel.set(label);
    this.originText.set(label);

  }

  setOriginFromCurrentLocation(coords: LatLng, label: string = 'Ubicación actual') {
    this.originCoords.set(coords);
    this.originLabel.set(label);
    this.originText.set(label);

  }
  useCurrentLocation() {
    this.requestUseCurrentLocation.emit();
  }


  reset(): void {
    this.originText.set('');
    this.originLabel.set('');
    this.originCoords.set(null);
    this.originSuggestions.set([]);
  }




  //Se ejecuta cada vez que el usuario escribe en el campo origen
  async onOriginInput(value: string) {
    //Guardo el texto que escribe el user
    this.originText.set(value);

    //Al escribir anulamos las coordenadas previas
    this.originCoords.set(null);
    this.originLabel.set('');

    // Si hay menos de 3 caracteres, no buscamos y limpiamos sugerencias
    if (value.trim().length < 3) {
      this.originSuggestions.set([]);
      return;
    }

    // Llamamos a Mapbox para obtener sugerencias
    const list = await this.forwardGeocode(value);

    // Guardamos las sugerencias (para mostrarlas luego en el HTML)
    this.originSuggestions.set(list);
  }

  // Cuando el usuario elige una sugerencia de ORIGEN
  selectOriginSuggestion(s: Suggestion) {
    const label = `${s.title}, ${s.subtitle}`;

    this.originText.set(label);
    this.originLabel.set(label);
    this.originCoords.set({ lng: s.lng, lat: s.lat });
    this.originSuggestions.set([]);
  }

  //Para que te sugiera sitios
  private async forwardGeocode(query: string): Promise<Suggestion[]> {
    const url =
      `https://api.mapbox.com/geocoding/v5/mapbox.places/${encodeURIComponent(query)}.json` +
      `?language=es&country=ES&types=address,place&limit=6&access_token=${environment.mapboxKey}`;

    try {
      const res = await fetch(url);


      if (!res.ok) return [];
      const data = await res.json();
      const features = data.features ?? [];

      return features
        .map((f: any) => {
          // title: texto corto (calle o sitio)
          const title = f.text ?? f.place_name;

          // subtitle: intentamos sacar ciudad/provincia desde "context"
          // context suele traer: place (ciudad), region (provincia/comunidad), country...
          const ctx: any[] = f.context ?? [];

          const place = ctx.find(c => (c.id ?? '').startsWith('place'))?.text;   // ciudad
          const region = ctx.find(c => (c.id ?? '').startsWith('region'))?.text; // provincia/comunidad

          // Construimos una segunda línea simple
          const subtitle =
            place && region ? `${place}, ${region}` :
              place ? place :
                region ? region :
                  'España';

          return {
            title,
            subtitle,
            lng: f.center?.[0],
            lat: f.center?.[1],
          };
        })
        .filter((s: Suggestion) => s.lng != null && s.lat != null);


    } catch (err) {
      console.error(err);
      return [];

    }

  }
  onOriginFocus(): void {
    if (this.originCoords() && this.originLabel()) {
      this.originText.set('');
      this.originCoords.set(null);
      this.originLabel.set('');
    }
  }




}


