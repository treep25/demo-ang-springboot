import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Request} from "../models/tutorial.model";

@Component({
  selector: 'app-request-representation',
  templateUrl: './request-representation.component.html',
  styleUrls: ['./request-representation.component.css']
})
export class RequestRepresentationComponent implements OnInit {

  constructor(private authService: AuthService) {
  }

  requests: Request[] = []

  ngOnInit(): void {
    this.authService.getALlRequestsWithUsers().subscribe(
      value => this.requests = value
    )
  }

  unlockUser(userId: any, requestId: any) {
    this.authService.changeUserStatus(userId).subscribe(
      value => {
        this.authService.closeRequest(requestId).subscribe(
          value1 => {
            console.log(value1)
            window.location.reload()
          },
          error => {
            console.error()
          }
        )
      },
      error => console.error()
    )
  }

  canceleUserRequest(requestId: any) {
    this.authService.cancelRequest(requestId).subscribe(
      value => console.log(value),
      error => console.error()
    )
  }
}
