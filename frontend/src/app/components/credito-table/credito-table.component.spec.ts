import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { CreditoTableComponent } from './credito-table.component';
import { Credito } from '../../models';

describe('CreditoTableComponent', () => {
  let component: CreditoTableComponent;
  let fixture: ComponentFixture<CreditoTableComponent>;

  const mockCreditos: Credito[] = [
    {
      numeroCredito: '123456',
      numeroNfse: '789123',
      dataConstituicao: '2025-01-15',
      valorIssqn: 150.50,
      tipoCredito: 'NORMAL',
      simplesNacional: 'Sim',
      aliquota: 5.0,
      valorFaturado: 3010.00,
      valorDeducao: 0.00,
      baseCalculo: 3010.00
    },
    {
      numeroCredito: '654321',
      numeroNfse: '321987',
      dataConstituicao: '2025-02-20',
      valorIssqn: 75.25,
      tipoCredito: 'ESPECIAL',
      simplesNacional: 'Não',
      aliquota: 2.5,
      valorFaturado: 1505.00,
      valorDeducao: 100.00,
      baseCalculo: 1405.00
    }
  ];

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreditoTableComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(CreditoTableComponent);
    component = fixture.componentInstance;
  });
  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve inicializar com créditos vazios', () => {
    expect(component.creditos).toEqual([]);
    expect(component.loading).toBe(false);
    expect(component.error).toBeNull();
  });

  it('deve exibir spinner de loading', () => {
    component.loading = true;
    fixture.detectChanges();

    const loading = fixture.debugElement.query(By.css('.loading-container'));
    expect(loading).toBeTruthy();
  });
  it('deve exibir mensagem de erro', () => {
    component.error = 'Erro de teste';
    component.loading = false;
    fixture.detectChanges();

    const errorCard = fixture.debugElement.query(By.css('.error-card'));
    expect(errorCard).toBeTruthy();
  });

  it('deve exibir mensagem de nenhum resultado', () => {
    component.creditos = [];
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const noResults = fixture.debugElement.query(By.css('.no-results-card'));
    expect(noResults).toBeTruthy();
  });
  it('deve exibir tabela com dados', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const table = fixture.debugElement.query(By.css('.creditos-table'));
    const rows = fixture.debugElement.queryAll(By.css('tr[mat-row]'));
    
    expect(table).toBeTruthy();
    expect(rows.length).toBe(2);
  });

  it('deve exibir dados corretos nas células da tabela', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const firstRowCells = fixture.debugElement.queryAll(By.css('tr[mat-row]:first-child td'));
    expect(firstRowCells[0].nativeElement.textContent.trim()).toBe('123456');
    expect(firstRowCells[1].nativeElement.textContent.trim()).toBe('789123');
  });
  it('deve formatar valores de moeda', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const valorCells = fixture.debugElement.queryAll(By.css('.valor-cell'));
    expect(valorCells[0].nativeElement.textContent).toContain('R$');
  });

  it('deve exibir ícones corretos para Simples Nacional', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const simplesIcons = fixture.debugElement.queryAll(By.css('tr[mat-row] td:last-child mat-icon'));
    expect(simplesIcons[0].nativeElement.textContent.trim()).toBe('check_circle');
    expect(simplesIcons[1].nativeElement.textContent.trim()).toBe('cancel');
  });
  it('deve exibir cards mobile', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const mobileCards = fixture.debugElement.queryAll(By.css('.credito-card'));
    expect(mobileCards.length).toBe(2);
  });

  it('deve rastrear por numeroCredito', () => {
    const credito = mockCreditos[0];
    const result = component.trackByCredito(0, credito);
    expect(result).toBe(credito.numeroCredito);
  });

  it('deve exibir contagem de resultados', () => {
    component.creditos = mockCreditos;
    component.loading = false;
    component.error = null;
    fixture.detectChanges();

    const title = fixture.debugElement.query(By.css('mat-card-title'));
    expect(title.nativeElement.textContent).toContain('Resultados (2)');
  });
});