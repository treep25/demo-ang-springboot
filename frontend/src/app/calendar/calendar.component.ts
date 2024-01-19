import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {Event} from "../models/tutorial.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css']
})
export class CalendarComponent implements OnInit {
  events: any[] = [];
  creatingEvent = false;
  newEvent: Event = {
    token: '',
    summary: '',
    startDate: new Date(),
    endDate: new Date()
  }

  constructor(private authService: AuthService, private route: ActivatedRoute, private router: Router) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(
      value => {
        if (value["code"] !== undefined) {
          this.authService.googleOAuth2Calendar(value["code"].toString()).subscribe(
            value => {

              this.authService.setAccessTokenCalendar(value).subscribe()
              this.authService.getEventsGoogleCalendar().subscribe(value1 => {
                //@ts-ignore
                this.events = value1
                this.router.navigate(['/calendar']);
              })

            }, () => {
              this.authService.getUrlGoogleCalendar().subscribe(
                value1 => {
                  window.location.href = <string>value1.uri
                }
              )
            }
          )
        } else {
          this.authService.getEventsGoogleCalendar().subscribe((events) => {
            // @ts-ignore
            this.events = events;
            this.router.navigate(['/calendar'])
          }, () => {
            this.authService.getUrlGoogleCalendar().subscribe(
              value1 => {
                window.location.href = <string>value1.uri
              }
            )
          });

        }
      }
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

  showCreateEventForm() {
    this.creatingEvent = true;
    // @ts-ignore
    this.newEvent = {
      token: '',
      summary: '',
      startDate: new Date(),
      endDate: new Date()
    }
  }

  createEvent() {
    this.authService.createEventCalendar(this.newEvent)
      .subscribe(
        () => window.location.reload()
      )
  }

  onDayClick(selectedDay: string): void {
    this.authService.searchByDay(selectedDay).subscribe(
      value => {
        //@ts-ignore
        this.events = value
      }
    )
  }
}
