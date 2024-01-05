import {ComponentFixture, TestBed} from '@angular/core/testing';

import {AzureCalendarComponent} from './azure-calendar.component';

describe('AzureCalendarComponent', () => {
  let component: AzureCalendarComponent;
  let fixture: ComponentFixture<AzureCalendarComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AzureCalendarComponent]
    });
    fixture = TestBed.createComponent(AzureCalendarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
