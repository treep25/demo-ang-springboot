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

  showComposeForm = false;

  openComposeForm() {
    this.showComposeForm = true;
  }

  closeComposeForm() {
    this.showComposeForm = false;
  }

  sendEmail() {
    this.authService.sendEmail(this.newEmail).subscribe(
      value => window.location.reload()
    )
  }

  ngOnInit(): void {

  }

  getEmailHeaderValue(email: Email, headerName: string): string {
    const header = email.payload.headers.find((h) => h.name === headerName);
    return header ? header.value : '';
  }

  getOutgoingMessages() {
    this.authService.getOutgoingMessages().subscribe(
      value => {
        this.emails = value
        window.location.reload()
      }
    )
  }
}
