import {RideRequestFormComponent} from './RideRequestForm.component';

import {ComponentFixture, TestBed} from '@angular/core/testing';

describe('RideRequestFormComponent', () => {
  let component: RideRequestFormComponent;
  let fixture: ComponentFixture<RideRequestFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RideRequestFormComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RideRequestFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
