import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthService} from "./auth.service";
import {catchError, map, Observable, of} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  constructor(private router: Router, private authService: AuthService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
    const accessToken = this.getCookie('accessToken');
    const refreshToken = this.getCookie('refreshToken');

    if (accessToken && refreshToken) {
      return this.authService.meInfo().pipe(
        map(res => {
          console.log('User info representation');
          return true;
        }),
        catchError(() => {
          console.log('Error during getting user info');

          return this.authService.refreshToken(refreshToken).pipe(
            map((res) => {

              document.cookie = `accessToken=${res.accessToken}; path=/; SameSite=None; Secure`;
              document.cookie = `refreshToken=${res.refreshToken}; path=/; SameSite=None; Secure`;

              return true;
            }),
            catchError(() => {
              console.log('Error during refreshing token');
              this.router.navigate(['/login']);
              return [false];
            })
          );
        })
      );
    } else {
      this.router.navigate(['/login']);
      return of(false);
    }
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
