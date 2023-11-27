import {Injectable} from '@angular/core';
import {CanActivate, Router} from "@angular/router";
import {AuthService} from "./auth.service";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(): boolean {
    const accessToken = this.getCookie('accessToken');
    const refreshToken = this.getCookie('refreshToken');

    if (accessToken && refreshToken) {
      //todo this does`nt work
      this.authService.meInfo().subscribe({
        next: (res) => {
          console.log("user info representation")
        },
        error: (e) => {
          console.log(e)

          this.authService.refreshToken(refreshToken).subscribe({
              next: (res) => {
                return true;
              },
              error: (e) => {
                console.log("Error during refreshing token")

                this.router.navigate(['/login']);
                return false;
              }
            }
          )
        }
      })
    }
    this.router.navigate(['/login']);
    return false;

  }

  private getCookie(name: string): any {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) {
      return parts.pop()?.split(';').shift();
    }
    return null;
  }
}
