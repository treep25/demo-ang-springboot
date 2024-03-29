import {Component, OnInit} from '@angular/core';
import {TranslateService} from "@ngx-translate/core";
import {Router} from "@angular/router";
import {AuthService} from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'Angular tutorials implementation';
  role ? = '';
  numberOdUnreadMessaged?: number | 0

  constructor(private translate: TranslateService, private router: Router, private authService: AuthService) {
    translate.setDefaultLang('en');
  }

  switchLanguage(language: string) {
    this.translate.use(language);
  }

  redirectToUserRepresentation() {
    this.router.navigate(['/userInfo']);
  }

  redirectToUserOrderRepresentation() {
    this.router.navigate(['/orders']);
  }

  redirectToChatPage() {
    this.router.navigate(['/chat']);
  }

  adminPanel() {
    this.router.navigate(['/admin/panel'])
  }

  userRequests() {
    this.router.navigate(['/admin/requests'])
  }

  ngOnInit(): void {
    this.authService.meInfo().subscribe(
      value => {
        this.role = value.role;
        this.authService.getUnreadMessagesOfUser().subscribe(
          value1 => this.numberOdUnreadMessaged = value1
        )
      }
    );
  }

  logOut(): void {
    document.cookie = 'accessToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
    document.cookie = 'refreshToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';

    this.router.navigate(['/login']).then(() => window.location.reload());
  }

}
