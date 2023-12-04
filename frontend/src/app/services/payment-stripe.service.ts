import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class PaymentStripeService {
  private apiUrl = 'http://localhost:8080/api/v1/payments';

  constructor(private http: HttpClient) {
  }

  createFakePayment(token: string): Observable<any> {
    const payload = {token};

    return this.http.post<any>(`${this.apiUrl}/stripe`, payload);
  }
}
