import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Credito } from '../models';

@Injectable({
  providedIn: 'root'
})
export class CreditoService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  buscarPorNfse(numeroNfse: string): Observable<Credito[]> {
    return this.http.get<Credito[]>(`${this.apiUrl}/api/creditos/${numeroNfse}`);
  }

  buscarPorCredito(numeroCredito: string): Observable<Credito> {
    return this.http.get<Credito>(`${this.apiUrl}/api/creditos/credito/${numeroCredito}`);
  }
}