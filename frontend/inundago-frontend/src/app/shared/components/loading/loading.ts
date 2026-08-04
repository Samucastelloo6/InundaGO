import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-loading',
  imports: [CommonModule],
  templateUrl: './loading.html',
})
export class Loading {
  @Input() logo: string = '/icons/icon-48x48.png';
  @Input() message: string = 'Cargando...';
  @Input() darkBackground : boolean = false;
 }
