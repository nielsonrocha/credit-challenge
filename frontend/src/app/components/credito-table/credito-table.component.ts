import { Component, Input } from '@angular/core';
import { Credito } from '../../models';
import { MatCardModule } from '@angular/material/card';
import { MatSpinner } from '@angular/material/progress-spinner';
import { MatIcon } from '@angular/material/icon';
import { MatTable } from '@angular/material/table';
import { MatChip } from '@angular/material/chips';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-credito-table',
  templateUrl: './credito-table.component.html',
  imports: [MatCardModule, MatSpinner, MatIcon, MatTable, MatChip, CommonModule],
  styleUrls: ['./credito-table.component.scss'],
})
export class CreditoTableComponent {

  @Input() creditos: Credito[] = [];
  @Input() loading = false;
  @Input() error: string | null = null;

  displayedColumns: string[] = [
    'numeroCredito',
    'numeroNfse',
    'dataConstituicao',
    'valorIssqn',
    'tipoCredito',
    'simplesNacional',
    'aliquota',
  ];

  trackByCredito(index: number, credito: Credito): string {
    return credito.numeroCredito;
  }
}
