import {Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {Order} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";
import {PaymentStripeService} from "../services/payment-stripe.service";
import {StripeService} from "ngx-stripe";

@Component({
  selector: 'app-order-representation',
  templateUrl: './order-representation.component.html',
  styleUrls: ['./order-representation.component.css']
})
export class OrderRepresentationComponent implements OnInit {
  order: Order = {
    tutorialsOrder: []
  }
  @ViewChild('cardElement') cardElement?: ElementRef;

  constructor(
    private userService: AuthService,
    private paymentService: PaymentStripeService,
    private zone: NgZone,
    private stripeService: StripeService
  ) {

  }


  async ngOnInit() {
    this.userService.getOrders().subscribe({
      next: (res) => {
        console.log("User orders")
        this.order = res;
      },
      error: (e) => console.error("Error during getting user orders")
    });

    const stripe = await this.loadStripe();
    const elements = stripe.elements();
    const card = elements.create('card');
    card.mount(this.cardElement?.nativeElement);

  }

  private async loadStripe() {
    return await (window as any).Stripe('pk_test_51OHikCAiCn0BUr4JPSqR5FDOhT5nGSiIvCkqFv5urEAAg12ymu317v7gAkfbFAPfK0D10L8AhnJzzdScYZOFP5WX00aITNen2Z');
  }

  async handlePayment() {
    const stripe = await this.loadStripe();
    const elements = stripe.elements();
    const card = elements.create('card');
    const {token, error} = await stripe.createToken(card);

    if (error) {
      console.error(error);
    } else {
      this.zone.run(() => {
        this.submitToken(token.id);
      });
    }
  }

  private submitToken(token: string) {
    // this.paymentService.createPaymentIntent(token).subscribe(
    //   response => {
    //     console.log(response);
    //     alert('Success');
    //   },
    //   error => {
    //     console.error(error);
    //     alert('Error');
    //   }
    // );
  }
}
