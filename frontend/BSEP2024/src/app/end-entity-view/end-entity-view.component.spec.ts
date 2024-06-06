import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EndEntityViewComponent } from './end-entity-view.component';

describe('EndEntityViewComponent', () => {
  let component: EndEntityViewComponent;
  let fixture: ComponentFixture<EndEntityViewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [EndEntityViewComponent]
    });
    fixture = TestBed.createComponent(EndEntityViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
