import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../models/tutorial.model";

@Component({
  selector: 'app-azure-calendar',
  templateUrl: './azure-calendar.component.html',
  styleUrls: ['./azure-calendar.component.css']
})
export class AzureCalendarComponent implements OnInit {

  events: any;
  creatingEvent = false;
  newEvent: Event = {
    location: '',
    token: '',
    summary: '',
    startDate: new Date(),
    endDate: new Date()
  }

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
  }

  showCreateEventForm() {
    this.creatingEvent = true;

    this.newEvent = {
      location: '',
      token: '',
      summary: '',
      startDate: new Date(),
      endDate: new Date()
    }
  }

  onDayClick(selectedDay: string): void {
    this.authService.searchByDayAzureCalendar(selectedDay).subscribe(
      value => {
        //@ts-ignore
        this.events = value
      }
    )
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(
      value => {
        if (value["code"] !== undefined) {
          this.authService.azureOAuth2Calendar(value["code"].toString()).subscribe(
            value => {

              this.authService.setAzureAccessTokenCalendar(value).subscribe()
              this.authService.getEventsAzureCalendar().subscribe(value1 => {
                //@ts-ignore
                this.events = value1
                this.router.navigate(['/azure/calendar']);
              })

            }, () => {
              this.authService.getUrlAzureCalendar().subscribe(
                value1 => {
                  window.location.href = <string>value1.uri
                }
              )
            }
          )
        } else {
          this.authService.getEventsAzureCalendar().subscribe((events) => {
            // @ts-ignore
            this.events = events;
            this.router.navigate(['azure/calendar'])
          }, () => {
            this.authService.getUrlAzureCalendar().subscribe(
              value1 => {
                window.location.href = <string>value1.uri
              }
            )
          });

        }
      }
    )
  }

  createEvent() {
    this.authService.createEventAzureCalendar(this.newEvent)
      .subscribe(
        () => window.location.reload()
      )
  }

  formatDateTime(value: number, timeZone: string): string {
    const options: Intl.DateTimeFormatOptions = {
      weekday: 'long',
      year: 'numeric',
      month: 'long',
      day: 'numeric',
      hour: 'numeric',
      minute: 'numeric',
      second: 'numeric',
      timeZone: timeZone
    };

    return new Date(value).toLocaleDateString('en-EN', options);
  }

}
