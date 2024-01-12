import {Component} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {User} from "../models/tutorial.model";

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

  constructor(private userService: AuthService) {
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
}
