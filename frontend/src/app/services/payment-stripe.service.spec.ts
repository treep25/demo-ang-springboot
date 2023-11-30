import {TestBed} from '@angular/core/testing';

import {PaymentStripeService} from './payment-stripe.service';

describe('PaymentStripeService', () => {
  let service: PaymentStripeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PaymentStripeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
