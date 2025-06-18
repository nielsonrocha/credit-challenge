import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting, HttpTestingController } from '@angular/common/http/testing';
import { CreditoService } from './credito.service';
import { Credito } from '../models';
import { environment } from '../../environments/environment';

describe('CreditoService', () => {
  let service: CreditoService;
  let httpMock: HttpTestingController;

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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CreditoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(CreditoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });
  it('deve ser criado', () => {
    expect(service).toBeTruthy();
  });

  it('deve buscar créditos por NFSE', () => {
    const numeroNfse = '789012';

    service.buscarPorNfse(numeroNfse).subscribe(creditos => {
      expect(creditos).toEqual(mockCreditoList);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/${numeroNfse}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCreditoList);
  });
  it('deve buscar crédito por número', () => {
    const numeroCredito = '123456';

    service.buscarPorCredito(numeroCredito).subscribe(credito => {
      expect(credito).toEqual(mockCredito);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/credito/${numeroCredito}`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCredito);
  });

  it('deve lidar com erro 404', () => {
    const numeroNfse = '999999';

    service.buscarPorNfse(numeroNfse).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(404);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/${numeroNfse}`);
    req.flush('Not Found', { status: 404, statusText: 'Not Found' });
  });
  it('deve lidar com erro 500', () => {
    const numeroCredito = '123456';

    service.buscarPorCredito(numeroCredito).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(500);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/credito/${numeroCredito}`);
    req.flush('Server Error', { status: 500, statusText: 'Internal Server Error' });
  });

  it('deve lidar com erro de rede', () => {
    const numeroCredito = '123456';

    service.buscarPorCredito(numeroCredito).subscribe({
      next: () => fail('should have failed'),
      error: (error) => {
        expect(error.status).toBe(0);
      }
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/credito/${numeroCredito}`);
    req.error(new ProgressEvent('timeout'), { status: 0, statusText: 'Unknown Error' });
  });
  it('deve retornar array vazio para NFSE', () => {
    const numeroNfse = '999999';

    service.buscarPorNfse(numeroNfse).subscribe(creditos => {
      expect(creditos).toEqual([]);
    });

    const req = httpMock.expectOne(`${environment.apiUrl}/api/creditos/${numeroNfse}`);
    req.flush([]);
  });
});