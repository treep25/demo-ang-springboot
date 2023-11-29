import {ComponentFixture, TestBed} from '@angular/core/testing';

import {OrderRepresentationComponent} from './order-representation.component';

describe('OrderRepresentationComponent', () => {
  let component: OrderRepresentationComponent;
  let fixture: ComponentFixture<OrderRepresentationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [OrderRepresentationComponent]
    });
    fixture = TestBed.createComponent(OrderRepresentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
