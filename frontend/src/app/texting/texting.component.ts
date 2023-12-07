import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Message, User} from "../models/tutorial.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-texting',
  templateUrl: './texting.component.html',
  styleUrls: ['./texting.component.css']
})
export class TextingComponent implements OnInit {
  users: User[] = [];
  selectedUser: User | null = null;
  messages: Message[] = [];
  newMessage: string = '';
  userIdWhenStatusOfOrderFailed?: number
  unreadMessagesCount: { [key: string]: number } = {};

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const userId = params['userId'];
      const message = params['message'];

      this.userIdWhenStatusOfOrderFailed = userId;
      this.newMessage = message;
    })

    this.authService.getAllUsers().subscribe(
      value => {
        this.users = value;
        this.loadUnreadMessagesCount();

        if (this.userIdWhenStatusOfOrderFailed != null || this.newMessage != null) {

          this.users.forEach(value1 => {
            if (value1.id == this.userIdWhenStatusOfOrderFailed) {
              this.selectedUser = value1;
            }
          })
        }
      }
    );
  }

  startDialog(email?: string): void {
    if (email) {
      this.authService.getConversationDialogContent(email).subscribe(
        value => {
          this.messages = value;
        }
      );
      this.selectedUser = this.users.find(user => user.email === email) || null;
    } else {
      this.selectedUser = null;
    }
  }

  sendMessage(): void {
    if (this.selectedUser) {
      this.authService.text(this.newMessage, this.selectedUser.email).subscribe(() => {
        this.authService.getConversationDialogContent(this.selectedUser!.email).subscribe(
          value => {
            this.messages = value;
          }
        );
        this.newMessage = '';
        this.userIdWhenStatusOfOrderFailed = undefined;
        this.router.navigate(['/chat'])
      });
    }
  }

  loadUnreadMessagesCount() {
    const usersWithEmail = this.users.filter(user => user.email);

    usersWithEmail.forEach((user) => {
      this.authService.getUnreadMessagesOfCurrentConversation(user.email!).subscribe(
        (count) => {
          this.unreadMessagesCount = {...this.unreadMessagesCount, [user.email!]: count};
        }
      );
    });
  }
}
