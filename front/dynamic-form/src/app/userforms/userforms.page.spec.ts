import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UserformsPage } from './userforms.page';

describe('UserformsPage', () => {
  let component: UserformsPage;
  let fixture: ComponentFixture<UserformsPage>;

  beforeEach(() => {
    fixture = TestBed.createComponent(UserformsPage);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
