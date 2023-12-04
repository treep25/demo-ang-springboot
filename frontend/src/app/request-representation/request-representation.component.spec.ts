import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RequestRepresentationComponent} from './request-representation.component';

describe('RequestRepresentationComponent', () => {
  let component: RequestRepresentationComponent;
  let fixture: ComponentFixture<RequestRepresentationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [RequestRepresentationComponent]
    });
    fixture = TestBed.createComponent(RequestRepresentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
