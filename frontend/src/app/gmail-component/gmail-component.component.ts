import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Email} from "../models/tutorial.model";

@Component({
  selector: 'app-gmail-component',
  templateUrl: './gmail-component.component.html',
  styleUrls: ['./gmail-component.component.css']
})
export class GmailComponentComponent implements OnInit {

  // @ts-ignore
  emails: Email[];

  newEmail = {
    to: '',
    subject: '',
    body: ''
  };
  attachmentFile: File | null = null;

  searchText: string = '';
  searchType: string = '';

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
    this.route.queryParams.subscribe(
      value => {
        if (value["code"] !== undefined) {
          this.authService.googleGmailApis(value["code"].toString()).subscribe(
            value => {

              this.authService.setAccessTokenCalendar(value.toString()).subscribe()
              this.authService.googleGmailBox().subscribe(value1 => {
                //@ts-ignore
                this.emails = value1
                this.router.navigate(['/google/gmail']);
              })

            }, error => {
              this.authService.getUrlGoogleGmail().subscribe(
                value1 => {
                  window.location.href = <string>value1.uri
                }
              )
            }
          )
        } else {
          this.authService.googleGmailBox().subscribe((events) => {
            // @ts-ignore
            this.emails = events;
            this.router.navigate(['/google/gmail'])
          }, error => {
            this.authService.getUrlGoogleGmail().subscribe(
              value1 => {
                window.location.href = <string>value1.uri
              }
            )
          });

        }
      }
    )
  }

  ngOnInit(): void {

  }

  showComposeForm = false;

  openComposeForm() {
    this.showComposeForm = true;
  }

  closeComposeForm() {
    this.showComposeForm = false;
  }

  sendEmail() {
    this.showUnreadMessages = false;
    // @ts-ignore
    this.authService.sendEmail(this.newEmail, this.attachmentFile).subscribe(
      value => window.location.reload()
    )
  }

  handleAttachmentChange(event: any): void {
    const fileList: FileList | null = event.target.files;
    if (fileList && fileList.length > 0) {
      this.attachmentFile = fileList[0];
    }
  }

  getEmailHeaderValue(email: Email, headerName: string): string {
    const header = email.payload.headers.find((h) => h.name === headerName);
    return header ? header.value : '';
  }

  getOutgoingMessages() {
    this.showUnreadMessages = false;
    this.authService.getOutgoingMessages().subscribe(
      value => {
        this.emails = value
      }
    )
  }


  search() {
    this.showUnreadMessages = false;
    this.authService.searchByParamsGmail(this.searchType, this.searchText).subscribe(
      value => {
        this.emails = value
      }
    )
  }

  showUnreadMessages = false;

  getUnreadMessages() {
    this.authService.getUnreadMessages().subscribe(
      value => {
        this.showUnreadMessages = true
        this.emails = value
      }
    )
  }

  showDatepicker = false;
  selectedDate: any

  openDatepicker(): void {
    this.showDatepicker = true;
  }

  handleDateChange(event: any): void {
    this.selectedDate = event.target.value;
    this.showDatepicker = false;
    this.searchByDateMessages(this.selectedDate);
  }

  searchByDateMessages(selectedDate: string): void {
    this.showUnreadMessages = false;

    this.authService.searchMessagesByDate(selectedDate).subscribe(
      value => {
        this.emails = value
      }
    )
  }
}
