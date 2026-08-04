import { CommonModule } from "@angular/common";
import { Component, EventEmitter, Input, Output, signal, CUSTOM_ELEMENTS_SCHEMA } from "@angular/core";
import { environment } from "../../../../../../environments/environment";
import { LatLng, Suggestion } from "../route-planner";




@Component({
  selector: 'app-destination',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './destination-picker.html',
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class DestinationComponent {

  @Output() togglePick = new EventEmitter<void>();

  @Input() pickingActive: boolean = false;


  //Modo escribir
  destinationText = signal<string>('');

  //coords modo click
  destinationCoords = signal<LatLng | null>(null);

  //Labels
  destinationLabel = signal<string>('');

  // Listas de sugerencias para mostrar debajo de cada input
  destinationSuggestions = signal<Suggestion[]>([]);

  getDestinationText(): string {
    return this.destinationText().trim();
  }

  getDestinationCoords(): LatLng | null {
    return this.destinationCoords();
  }

  setDestinationFromMap(coords: LatLng, label: string = 'Destino seleccionado') {
    this.destinationCoords.set(coords);
    this.destinationLabel.set(label);
    this.destinationText.set(label);
  }

  reset(): void {
    this.destinationLabel.set('');
    this.destinationText.set('');
    this.destinationCoords.set(null);
    this.destinationSuggestions.set([]);
  }


  // Se ejecuta cada vez que el usuario escribe en el campo DESTINO
  async onDestinationInput(value: string) {

    // Guardamos el texto que escribe el usuario
    this.destinationText.set(value);

    // Al escribir anulamos coordenadas previas
    this.destinationCoords.set(null);
    this.destinationLabel.set('');

    // Si hay menos de 3 caracteres, no buscamos y limpiamos sugerencias
    if (value.trim().length < 3) {
      this.destinationSuggestions.set([]);
      return;
    }

    // Llamamos a Mapbox para obtener sugerencias
    const list = await this.forwardGeocode(value);

    // Guardamos las sugerencias (para mostrarlas luego en el HTML)
    this.destinationSuggestions.set(list);
  }
  // Cuando el usuario elige una sugerencia de DESTINO
  selectDestinationSuggestion(s: Suggestion) {
    const label = `${s.title}, ${s.subtitle}`;

    this.destinationText.set(label);
    this.destinationLabel.set(label);
    this.destinationCoords.set({ lng: s.lng, lat: s.lat });
    this.destinationSuggestions.set([]); // cerramos el desplegable
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
}


