export interface Credito {
  numeroCredito: string;
  numeroNfse: string;
  dataConstituicao: string;
  valorIssqn: number;
  tipoCredito: string;
  simplesNacional: string;
  aliquota: number;
  valorFaturado: number;
  valorDeducao: number;
  baseCalculo: number;
}

export enum SearchFilterType {
  NFSE = 'NFSE',
  CREDITO = 'CREDITO',
}

export interface SearchFilter {
  tipo: SearchFilterType;
  valor: string;
}
