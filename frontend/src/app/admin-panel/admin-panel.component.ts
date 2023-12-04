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
  pagedUsers: User[] = [];
  currentPage: number = 1;
  itemsPerPage: number = 5;
  firstName = ''
  lastName = ''


  constructor(private userService: AuthService) {
    this.updatePagedUsers();
  }

  ngOnInit(): void {
    this.userService.getAllUsersAdmin().subscribe(
      value => {
        this.users = value
        this.pagedUsers = value
      },
      error => console.error(error)
    )
  }

  viewOrders(user: User): Order {
    user.showOrders = !user.showOrders;
    return <Order>user.order
  }

  blockUser(user: User): void {
    this.userService.changeUserStatus(user.id).subscribe(
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

  getAllStatusEnabled(): void {
    this.userService.searchByIsEnabledTrue().subscribe(
      value => this.users = value,
      error => console.error(error)
    )
  }

  // pagination

  pageChanged(event: any): void {
    this.currentPage = event;
    this.updatePagedUsers();
  }

  private updatePagedUsers(): void {
    const startIndex = (this.currentPage - 1) * this.itemsPerPage;
    const endIndex = startIndex + this.itemsPerPage;
    this.pagedUsers = this.users.slice(startIndex, endIndex);
  }
}
