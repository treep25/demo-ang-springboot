import {Component} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {User} from "../models/tutorial.model";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-user-representation',
  templateUrl: './user-representation.component.html',
  styleUrls: ['./user-representation.component.css']
})
export class UserRepresentationComponent {
  user: User = {
    email: '',
    firstName: '',
    lastName: '',
    role: ''
  }

  constructor(private userService: AuthService, private authService: AuthService) {
  }

  ngOnInit(): void {
    this.userService.meInfo().subscribe({
      next: (res) => {
        console.log("User representation")
        this.user = res;
      },
      error: (e) => console.error()
    });
  }

  generateReport() {
    this.userService.generateReport().subscribe(
      (response: any) => {
        const blob = new Blob([response], {type: 'application/pdf'});
        const url = window.URL.createObjectURL(blob);
        window.open(url);
      },
      (error) => {
        if (error instanceof HttpErrorResponse) {
          console.error('HTTP error occurred:', error.status);
          console.error('Response body:', error.error);
        } else {
          console.error('An error occurred:', error);
        }
      }
    );
  }

  generateReportAndSendGmail() {
    this.userService.generateReportAndSendViaGmail().subscribe(

    );
  }
}
