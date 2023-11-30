import {Component, OnInit} from '@angular/core';
import {Order, User} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  users: User[] = []
  firstName = ''
  lastName = ''

  //TODO contunie with .html
  constructor(private userService: AuthService) {
  }

  ngOnInit(): void {
    this.userService.getAllUsersAdmin().subscribe(
      value => this.users = value,
      error => console.error(error)
    )
  }

  viewOrders(user: User): Order {
    user.showOrders = !user.showOrders;
    return <Order>user.order
  }

  blockUser(user: User): void {
    this.userService.blockUser(user.id).subscribe(
      value => console.log(value),
      error => console.error(error)
    )
  }

  searchByFirstName(): void {
    this.userService.searchByFirstName(this.firstName).subscribe(
      value => this.users = value,
      error => console.error(error)
    )
  }

  searchByLastName(): void {
    this.userService.searchByLastName(this.lastName).subscribe(
      value => this.users = value,
      error => console.error(error)
    )
  }

  getAllStatusDisabled(): void {
    this.userService.searchByLastName(this.lastName).subscribe(
      value => this.users = value,
      error => console.error(error)
    )
  }

  // pagination
}
