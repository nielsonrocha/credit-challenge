import { Component, EventEmitter, Output } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatIconModule } from '@angular/material/icon';
import { SearchFilter, SearchFilterType } from '../../models';

@Component({
  selector: 'app-search-filter',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatCardModule,
    MatButtonModule,
    MatButtonToggleModule,
    MatFormFieldModule,
    MatInputModule,
    MatIconModule,
  ],
  templateUrl: './search-filter.component.html',
  styleUrl: './search-filter.component.scss',
})
export class SearchFilterComponent {

  searchForm: FormGroup;
  tipoSelecionado: SearchFilterType = SearchFilterType.NFSE;

  @Output() pesquisar = new EventEmitter<SearchFilter>();

  constructor(private fb: FormBuilder) {
    this.searchForm = this.fb.group({
      valor: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

  onTipoChange(tipo: SearchFilterType): void {
    this.tipoSelecionado = tipo;
    this.searchForm.get('valor')?.setValue('');
  }

  onSubmit(): void {
    if (this.searchForm.valid) {
      const filter: SearchFilter = {
        tipo: this.tipoSelecionado,
        valor: this.searchForm.get('valor')?.value
      };
      this.pesquisar.emit(filter);
    }
  }

  get isFormInvalid(): boolean {
    const valorControl = this.searchForm.get('valor');
    return !!(valorControl?.invalid && valorControl?.touched);
  }


}
