import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Message} from "../models/tutorial.model";

@Component({
  selector: 'app-texting',
  templateUrl: './texting.component.html',
  styleUrls: ['./texting.component.css']
})
export class TextingComponent implements OnInit {
  messages: Message[] = [];
  newMessage: string = '';
  recipient: string = '';

  constructor(private authService: AuthService) {
    const storedRecipient = localStorage.getItem('recipient');
    if (storedRecipient) {
      this.recipient = storedRecipient;
    }
  }

  sendMessage() {
    this.authService.text(this.newMessage, this.recipient).subscribe(() => {
      this.authService.getConversationDialogContent(this.recipient).subscribe(
        value => {
          this.messages = value;
        }
      );
    });

    this.newMessage = '';
  }

  ngOnInit(): void {
    if (this.recipient) {
      this.authService.getConversationDialogContent(this.recipient).subscribe(
        value => {
          this.messages = value;
        }
      );
    }
  }

  startDialog() {
    localStorage.setItem('recipient', this.recipient);

    this.authService.getConversationDialogContent(this.recipient).subscribe(
      value => {
        this.messages = value;
      }
    );
  }
}
