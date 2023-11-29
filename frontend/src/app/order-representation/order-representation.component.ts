import {Component} from '@angular/core';
import {Order} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";
import {PaymentStripeService} from "../services/payment-stripe.service";
import {StripeService} from "ngx-stripe";

@Component({
  selector: 'app-order-representation',
  templateUrl: './order-representation.component.html',
  styleUrls: ['./order-representation.component.css']
})
export class OrderRepresentationComponent {
  order: Order = {
    tutorialsOrder: []
  }

  // @ViewChild(StripeCardComponent) card?: StripeCardComponent;
  // @ViewChild('cardElement') cardElement?: ElementRef;
  // stripeKey = 'pk_test_51OHikCAiCn0BUr4JPSqR5FDOhT5nGSiIvCkqFv5urEAAg12ymu317v7gAkfbFAPfK0D10L8AhnJzzdScYZOFP5WX00aITNen2Z';

  constructor(private userService: AuthService, private paymentService: PaymentStripeService, private stripeService: StripeService) {
  }

  ngOnInit(): void {
    this.userService.getOrders().subscribe({
      next: (res) => {
        console.log("User orders")
        this.order = res;
      },
      error: (e) => console.error("Error during getting user orders")
    });
    // this.stripeService.setKey(this.stripeKey);
  }

  makePayment(amountCent: any) {

  }
}
