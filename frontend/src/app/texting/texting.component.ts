import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {GroupMessage, Message, User} from "../models/tutorial.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-texting',
  templateUrl: './texting.component.html',
  styleUrls: ['./texting.component.css']
})
export class TextingComponent implements OnInit {
  users: User[] = [];
  groupMessages: GroupMessage[] = [];
  contentInGroupMessages: GroupMessage[] = [];
  selectedUser: User | null = null;
  selectedUsers: User[] | undefined;
  messages: Message[] = [];
  newMessage: string = '';
  userIdWhenStatusOfOrderFailed?: number
  unreadMessagesCount: { [key: string]: number } = {};

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
  }

  getRecipientsNames(recipients: User[] | undefined): string {
    if (!recipients || recipients.length === 0) {
      return '';
    }

    const names = recipients.map(user => user.firstName);
    return names.join(', ');
  }

  isCurrentUser(user: User | undefined): boolean {
    return user?.id === this.authService.meInfo();
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
          });
        }
      }
    );
    this.authService.getllGroupsOfCurrentUserDialogs().subscribe(
      value1 => {
        this.groupMessages = value1;
        console.log(value1);
      },
      error => console.error()
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

  startDialogInGroups(recipients: User[] | undefined): void {
    if (recipients) {
      let emails = recipients.map(value => value.email);
      this.authService.getConversationDialogContentInGroups(emails).subscribe(
        value => {
          this.contentInGroupMessages = value
          console.log(value);
        }
      );
      this.selectedUsers = this.groupMessages.map(value => value.recipients)
        .find(user => user === recipients);
    } else {
      this.selectedUsers = [];
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
        this.router.navigate(['/chat']);
      });
    }
  }

  sendMessageGroup() {
    // @ts-ignore
    let emails = this.selectedUsers.map(value => value.email);
    if (this.selectedUsers) {
      this.authService.textGroup(emails, this.newMessage).subscribe(
        () => {
          this.authService.getConversationDialogContentInGroups(emails)
            .subscribe(
              value => this.contentInGroupMessages = value
            )
        })
      this.newMessage = '';
      this.router.navigate(['/chat']);
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

  getGroupTitle(recipients: User[] | undefined): string {
    // @ts-ignore
    return recipients?.map(value => value.firstName + ' ' + value.lastName).join(' & ').toString()
  }
}
