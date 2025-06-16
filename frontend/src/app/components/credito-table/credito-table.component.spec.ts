import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreditoTableComponent } from './credito-table.component';

describe('CreditoTableComponent', () => {
  let component: CreditoTableComponent;
  let fixture: ComponentFixture<CreditoTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreditoTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreditoTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
