import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { HeaderComponent } from './header.component';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HeaderComponent, NoopAnimationsModule]
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });
  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve renderizar toolbar com cor primária', () => {
    const toolbar = fixture.debugElement.query(By.css('mat-toolbar'));
    expect(toolbar.nativeElement.getAttribute('color')).toBe('primary');
  });

  it('deve exibir ícone account_balance', () => {
    const headerIcon = fixture.debugElement.query(By.css('.header-icon'));
    expect(headerIcon.nativeElement.textContent.trim()).toBe('account_balance');
  });
  it('deve exibir título correto', () => {
    const title = fixture.debugElement.query(By.css('.header-title'));
    expect(title.nativeElement.textContent.trim()).toBe('Sistema de Consulta de Créditos');
  });

  it('deve exibir ícone info', () => {
    const icons = fixture.debugElement.queryAll(By.css('mat-icon'));
    const infoIcon = icons.find(icon => 
      icon.nativeElement.textContent.trim() === 'info'
    );
    expect(infoIcon).toBeTruthy();
  });

  it('deve ter elemento spacer', () => {
    const spacer = fixture.debugElement.query(By.css('.spacer'));
    expect(spacer).toBeTruthy();
  });
});