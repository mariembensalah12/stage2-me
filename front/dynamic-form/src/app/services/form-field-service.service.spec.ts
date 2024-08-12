import { TestBed } from '@angular/core/testing';

import { FormFieldServiceService } from './form-field-service.service';

describe('FormFieldServiceService', () => {
  let service: FormFieldServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FormFieldServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
