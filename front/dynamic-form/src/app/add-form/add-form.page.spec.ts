import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AddFormPage } from './add-form.page';

describe('AddFormPage', () => {
  let component: AddFormPage;
  let fixture: ComponentFixture<AddFormPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(AddFormPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
