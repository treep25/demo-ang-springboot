import {Component, ElementRef, NgZone, OnInit, ViewChild} from '@angular/core';
import {Order} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";
import {PaymentStripeService} from "../services/payment-stripe.service";

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
    private zone: NgZone
  ) {

  }


  async ngOnInit() {
    this.userService.getOrders().subscribe({
      next: (res) => {
        console.log("User orders")
        this.order = res;
      },
      error: () => console.error()
    });

    const stripe = await this.loadStripe();
    const elements = stripe.elements();
    const card = elements.create('card');
    card.mount(this.cardElement?.nativeElement);

  }

  private async loadStripe() {
    if (!window.Stripe) {
      const script = document.createElement('script');
      script.src = 'https://js.stripe.com/v3/';
      document.body.appendChild(script);

      await new Promise((resolve) => {
        script.onload = resolve;
      });
      return (window as any).Stripe('pk_test_51OHikCAiCn0BUr4JPSqR5FDOhT5nGSiIvCkqFv5urEAAg12ymu317v7gAkfbFAPfK0D10L8AhnJzzdScYZOFP5WX00aITNen2Z');
    }
  }

  async handlePayment() {
    const stripe = await this.loadStripe();

    // @ts-ignore
    stripe.elements().create('card').then((card) => {
      card.mount(this.cardElement?.nativeElement);
    });

    const {token, error} = await stripe.createToken();

    if (error) {
      console.error();
    } else {
      this.zone.run(() => {
        this.submitToken(token.id);
      });
    }
  }

  private submitToken(token: string) {
    this.paymentService.createFakePayment(token).subscribe(
      response => {
        console.log(response);
        alert('Success');
      },
      () => {
        console.error();
        alert('Error');
      }
    );
  }


  clearBucketOrder() {
    this.userService.clearBucketOrder().subscribe(
      () => window.location.reload(),
      () => console.error()
    )
  }

  paidOrder() {
    this.userService.payOrder().subscribe();
  }

  cancelOrder() {
    this.userService.cancelOrder().subscribe();
  }
}
