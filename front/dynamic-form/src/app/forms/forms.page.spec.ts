import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormcvPage } from './forms.page';

describe('FormcvPage', () => {
  let component: FormcvPage;
  let fixture: ComponentFixture<FormcvPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(FormcvPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
