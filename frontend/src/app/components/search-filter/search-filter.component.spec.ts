import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { SearchFilterComponent } from './search-filter.component';
import { SearchFilterType } from '../../models';

describe('SearchFilterComponent', () => {
  let component: SearchFilterComponent;
  let fixture: ComponentFixture<SearchFilterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchFilterComponent, ReactiveFormsModule, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(SearchFilterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar com tipo NFSE', () => {
    expect(component.tipoSelecionado).toBe(SearchFilterType.NFSE);
  });

  it('deve inicializar formulário com valor vazio', () => {
    expect(component.searchForm.get('valor')?.value).toBe('');
  });
  it('deve exigir mínimo de 3 caracteres', () => {
    const valorControl = component.searchForm.get('valor');
    valorControl?.setValue('ab');
    expect(valorControl?.hasError('minlength')).toBeTruthy();
  });

  it('deve ser válido com 3 ou mais caracteres', () => {
    component.searchForm.get('valor')?.setValue('123');
    expect(component.searchForm.valid).toBeTruthy();
  });

  it('deve alterar tipo e limpar formulário', () => {
    component.searchForm.get('valor')?.setValue('12345');
    component.onTipoChange(SearchFilterType.CREDITO);
    
    expect(component.tipoSelecionado).toBe(SearchFilterType.CREDITO);
    expect(component.searchForm.get('valor')?.value).toBe('');
  });
  it('deve emitir evento pesquisar em submit válido', () => {
    spyOn(component.pesquisar, 'emit');
    
    component.searchForm.get('valor')?.setValue('12345');
    component.onSubmit();
    
    expect(component.pesquisar.emit).toHaveBeenCalledWith({
      tipo: SearchFilterType.NFSE,
      valor: '12345'
    });
  });

  it('não deve emitir em submit inválido', () => {
    spyOn(component.pesquisar, 'emit');
    
    component.searchForm.get('valor')?.setValue('');
    component.onSubmit();
    
    expect(component.pesquisar.emit).not.toHaveBeenCalled();
  });
  it('deve mostrar erro quando campo é inválido e tocado', () => {
    const valorControl = component.searchForm.get('valor');
    valorControl?.setValue('');
    valorControl?.markAsTouched();
    
    expect(component.isFormInvalid).toBeTruthy();
  });

  it('deve desabilitar botão submit quando formulário inválido', () => {
    component.searchForm.get('valor')?.setValue('');
    fixture.detectChanges();
    
    const button = fixture.debugElement.query(By.css('button[type="submit"]'));
    expect(button.nativeElement.disabled).toBeTruthy();
  });
  it('deve alterar tipo quando toggle clicado', () => {
    spyOn(component, 'onTipoChange');
    
    const toggleGroup = fixture.debugElement.query(By.css('mat-button-toggle-group'));
    toggleGroup.triggerEventHandler('change', { value: SearchFilterType.CREDITO });
    
    expect(component.onTipoChange).toHaveBeenCalledWith(SearchFilterType.CREDITO);
  });

  it('deve atualizar placeholder baseado no tipo', () => {
    component.tipoSelecionado = SearchFilterType.CREDITO;
    fixture.detectChanges();
    
    const input = fixture.debugElement.query(By.css('input'));
    expect(input.nativeElement.placeholder).toBe('Ex: 654321');
  });
  it('deve atualizar label baseado no tipo', () => {
    component.tipoSelecionado = SearchFilterType.CREDITO;
    fixture.detectChanges();
    
    const label = fixture.debugElement.query(By.css('mat-label'));
    expect(label.nativeElement.textContent.trim()).toBe('Digite o número do crédito');
  });
});