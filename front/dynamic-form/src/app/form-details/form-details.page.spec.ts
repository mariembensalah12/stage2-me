import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormDetailsPage } from './form-details.page';

describe('FormDetailsPage', () => {
  let component: FormDetailsPage;
  let fixture: ComponentFixture<FormDetailsPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(FormDetailsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
