import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Message, User} from "../models/tutorial.model";

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
  unreadMessagesCount: { [key: string]: number } = {};

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.authService.getAllUsers().subscribe(
      value => {
        this.users = value;
        this.loadUnreadMessagesCount();
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
