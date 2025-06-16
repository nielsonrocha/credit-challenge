import { Component } from '@angular/core';
import { HeaderComponent } from './components/header/header.component';
import { SearchFilterComponent } from './components/search-filter/search-filter.component';
import { Credito, SearchFilter } from './models';
import { CreditoService } from './services';
import { CreditoTableComponent } from './components/credito-table/credito-table.component';
import { catchError, EMPTY, map, Observable } from 'rxjs';

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

  onPesquisar(filter: SearchFilter): void {
    this.loading = true;
    this.error = null;
    this.creditos = [];

    let busca$: Observable<Credito[]>;

    if (filter.tipo === 'NFSE') {
      busca$ = this.creditoService.buscarPorNfse(filter.valor);
    } else {
      busca$ = this.creditoService.buscarPorCredito(filter.valor).pipe(
        map(credito => [credito]) 
      );
    }

    busca$.pipe(
      catchError(error => {
        this.error = this.getErrorMessage(error);
        this.loading = false;
        return EMPTY;
      })
    ).subscribe({
      next: (resultados) => {
        this.creditos = resultados;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  private getErrorMessage(error: any): string {
    if (error.status === 404) {
      return 'Nenhum crédito encontrado para os parâmetros informados.';
    } else if (error.status === 400) {
      return 'Parâmetros de consulta inválidos.';
    } else if (error.status === 500) {
      return 'Erro interno do servidor. Tente novamente mais tarde.';
    } else {
      return 'Erro na consulta. Verifique sua conexão e tente novamente.';
    }
  }
}
