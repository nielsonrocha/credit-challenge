import { TestBed, ComponentFixture } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { AppComponent } from './app.component';
import { CreditoService } from './services/credito.service';
import { Credito, SearchFilter, SearchFilterType } from './models';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let creditoService: jasmine.SpyObj<CreditoService>;

  const mockCredito: Credito = {
    numeroCredito: '123456',
    numeroNfse: '789012',
    dataConstituicao: '2024-01-01',
    valorIssqn: 100.00,
    tipoCredito: 'TIPO_A',
    simplesNacional: 'SIM',
    aliquota: 5.0,
    valorFaturado: 2000.00,
    valorDeducao: 0.00,
    baseCalculo: 2000.00
  };

  const mockCreditoList: Credito[] = [mockCredito];

  beforeEach(async () => {
    const creditoServiceSpy = jasmine.createSpyObj('CreditoService', ['buscarPorNfse', 'buscarPorCredito']);

    await TestBed.configureTestingModule({
      providers: [
        { provide: CreditoService, useValue: creditoServiceSpy },
        provideHttpClient(),
        provideHttpClientTesting()
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).overrideComponent(AppComponent, {
      set: {
        template: `
          <div class="app-container">
            <div>Header Mock</div>
            <main class="main-content">
              <div>Search Filter Mock</div>
              <div>Credito Table Mock</div>
            </main>
          </div>
        `
      }
    }).compileComponents();

    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    creditoService = TestBed.inject(CreditoService) as jasmine.SpyObj<CreditoService>;
  });
  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar com valores padrão', () => {
    expect(component.creditos).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
  });

  it('deve buscar por NFSE', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.NFSE,
      valor: '789012'
    };

    creditoService.buscarPorNfse.and.returnValue(of(mockCreditoList));

    component.onPesquisar(filter);

    expect(component.creditos).toEqual(mockCreditoList);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(creditoService.buscarPorNfse).toHaveBeenCalledWith('789012');
  });
  it('deve buscar por CREDITO', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.CREDITO,
      valor: '123456'
    };

    creditoService.buscarPorCredito.and.returnValue(of(mockCredito));

    component.onPesquisar(filter);

    expect(component.creditos).toEqual([mockCredito]);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
    expect(creditoService.buscarPorCredito).toHaveBeenCalledWith('123456');
  });

  it('deve lidar com erro 404', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.NFSE,
      valor: '789012'
    };

    creditoService.buscarPorNfse.and.returnValue(throwError(() => ({ status: 404 })));

    component.onPesquisar(filter);

    expect(component.loading).toBe(false);
    expect(component.error).toBe('Nenhum crédito encontrado para os parâmetros informados.');
    expect(component.creditos).toEqual([]);
  });
  it('deve lidar com erro 400', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.CREDITO,
      valor: '123456'
    };

    creditoService.buscarPorCredito.and.returnValue(throwError(() => ({ status: 400 })));

    component.onPesquisar(filter);

    expect(component.loading).toBe(false);
    expect(component.error).toBe('Parâmetros de consulta inválidos.');
    expect(component.creditos).toEqual([]);
  });

  it('deve lidar com erro 500', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.NFSE,
      valor: '789012'
    };

    creditoService.buscarPorNfse.and.returnValue(throwError(() => ({ status: 500 })));

    component.onPesquisar(filter);

    expect(component.loading).toBe(false);
    expect(component.error).toBe('Erro interno do servidor. Tente novamente mais tarde.');
    expect(component.creditos).toEqual([]);
  });
  it('deve lidar com erro desconhecido', () => {
    const filter: SearchFilter = {
      tipo: SearchFilterType.NFSE,
      valor: '789012'
    };

    creditoService.buscarPorNfse.and.returnValue(throwError(() => ({ status: 0 })));

    component.onPesquisar(filter);

    expect(component.loading).toBe(false);
    expect(component.error).toBe('Erro na consulta. Verifique sua conexão e tente novamente.');
    expect(component.creditos).toEqual([]);
  });

  it('deve resetar o estado em nova busca', () => {
    component.error = 'Previous error';
    component.creditos = [mockCredito];

    const filter: SearchFilter = {
      tipo: SearchFilterType.NFSE,
      valor: '789012'
    };

    creditoService.buscarPorNfse.and.returnValue(of(mockCreditoList));

    component.onPesquisar(filter);

    expect(component.error).toBeNull();
    expect(component.creditos).toEqual(mockCreditoList);
  });
});