import {Component, OnInit} from '@angular/core';
import {Order, User} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-admin-panel',
  templateUrl: './admin-panel.component.html',
  styleUrls: ['./admin-panel.component.css']
})
export class AdminPanelComponent implements OnInit {

  users: User[] = []
  pagedUsers: User[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 1;
  firstName = ''
  lastName = ''


  constructor(private userService: AuthService, private router: Router) {

  }

  ngOnInit(): void {
    this.userService.getAllUsers().subscribe(
      value => {
        this.users = value
        this.pagedUsers = this.users.slice(0, 1)
      },
      error => console.error()
    )
  }

  viewOrders(user: User): Order {
    user.showOrders = !user.showOrders;
    return <Order>user.order
  }

  blockUser(user: User): void {
    this.userService.changeUserStatus(user.id).subscribe(
      value => console.log(value),
      error => console.error()
    )
  }

  searchByFirstName(): void {
    this.userService.searchByFirstName(this.firstName).subscribe(
      value => this.pagedUsers = value,
      error => console.error()
    )
  }

  searchByLastName(): void {
    this.userService.searchByLastName(this.lastName).subscribe(
      value => this.users = value,
      error => console.error()
    )
  }

  getAllStatusEnabled(): void {
    this.userService.searchByIsEnabledTrue().subscribe(
      value => this.pagedUsers = value,
      error => console.error()
    )
  }

  pageChanged(event: any): void {
    console.log('Page changed:', event);
    this.currentPage = event;
    this.updatePagedUsers();
  }

  sendMessage(order: Order, user: User) {
    let message = 'Hi, I am admin, your order status is Failed, maybe you have some tech problems. Your order contains: '
    order.tutorialsOrder?.forEach(value => message += `${value.title}`)

    this.router.navigate(['/chat'], {queryParams: {userId: user.id, message: message}});
  }

  private updatePagedUsers(): void {
    console.log('Total pages:', Math.ceil(this.users.length / this.itemsPerPage));

    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.pagedUsers = this.users.slice(startIndex, endIndex);
  }
}
