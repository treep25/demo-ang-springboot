import {ComponentFixture, TestBed} from '@angular/core/testing';

import {UserRepresentationComponent} from './user-representation.component';

describe('UserRepresentationComponent', () => {
  let component: UserRepresentationComponent;
  let fixture: ComponentFixture<UserRepresentationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserRepresentationComponent]
    });
    fixture = TestBed.createComponent(UserRepresentationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
