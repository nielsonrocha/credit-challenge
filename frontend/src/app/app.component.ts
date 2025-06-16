import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { SearchFilterComponent } from './components/search-filter/search-filter.component';
import { Credito } from './models';
import { CreditoService } from './services';
import { CreditoTableComponent } from './components/credito-table/credito-table.component';

@Component({
  selector: 'app-root',
  imports: [HeaderComponent, SearchFilterComponent, CreditoTableComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {

  creditos: Credito[] = [];
  loading = false;
  error: string | null = null;

  constructor(private creditoService: CreditoService) {}
}
