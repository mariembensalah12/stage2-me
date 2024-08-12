import { TestBed } from '@angular/core/testing';

import { CvformService } from './cvform.service';

describe('CvformService', () => {
  let service: CvformService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CvformService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
