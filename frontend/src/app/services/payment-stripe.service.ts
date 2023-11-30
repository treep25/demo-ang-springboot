import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PaymentStripeService {
  private apiUrl = 'http://localhost:8080/api/v1/payments/stripe';

  constructor(private http: HttpClient) {
  }

  createPaymentIntent(token: any, amount: any): Observable<any> {
    const body = {token, amount};
    return this.http.post(`${this.apiUrl}/create-payment-intent`, body, {withCredentials: true});
  }
}
