import {ComponentFixture, TestBed} from '@angular/core/testing';

import {TutorialsInformationComponent} from './tutorials.information.component';

describe('TutorialsInformationComponent', () => {
  let component: TutorialsInformationComponent;
  let fixture: ComponentFixture<TutorialsInformationComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TutorialsInformationComponent]
    });
    fixture = TestBed.createComponent(TutorialsInformationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
