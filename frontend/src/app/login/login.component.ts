import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from "../services/auth.service";
import {LoginRequest} from "../models/tutorial.model";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;
  googleUrl: string | undefined = ''
  facebookUrl: string | undefined = ''
  githubUrl: string | undefined = ''


  signInWithGoogle() {
    document.cookie = 'provider=GOOGLE; expires=' + new Date(new Date().getTime() + 60000).toUTCString() + '; path=/; SameSite=None; Secure';
    if (typeof this.googleUrl === "string") {
      window.location.href = this.googleUrl
    }
  }

  signInWithFacebook() {
    document.cookie = 'provider=FACEBOOK; expires=' + new Date(new Date().getTime() + 60000).toUTCString() + '; path=/; SameSite=None; Secure';
    if (typeof this.facebookUrl === "string") {
      window.location.href = this.facebookUrl
    }
  }

  signInWithGithub() {
    document.cookie = 'provider=GITHUB; expires=' + new Date(new Date().getTime() + 60000).toUTCString() + '; path=/; SameSite=None; Secure';
    if (typeof this.githubUrl === "string") {
      window.location.href = this.githubUrl
    }
  }

  extractCookieProvider(): string | null {
    const cookie = document.cookie.split(';').map(cookie => cookie.trim());
    const providerCookie = cookie.find(cookie => cookie.startsWith('provider='));

    if (providerCookie) {
      return providerCookie.split('=')[1];
    } else {
      return null;
    }
  }

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router, private route: ActivatedRoute) {
    this.authService.getUrlGoogle().subscribe(
      value => {
        this.googleUrl = value.uri
      }
    )

    this.authService.getUrlFacebook().subscribe(
      value => {
        this.facebookUrl = value.uri
      }
    )

    this.authService.getUrlGithub().subscribe(
      value => {
        this.githubUrl = value.uri
      }
    )

    this.route.queryParams.subscribe(
      value => {

        if (value["code"] !== undefined) {
          if (this.extractCookieProvider() == "GOOGLE") {
            this.authService.googleAuth(value["code"].toString()).subscribe(
              access_token => {
                this.authService.loginOAuth2Google(access_token).subscribe(
                  authResponse => {
                    document.cookie = `accessToken=${authResponse.accessToken}; path=/; SameSite=None; Secure`;
                    document.cookie = `refreshToken=${authResponse.refreshToken}; path=/; SameSite=None; Secure`;

                    this.router.navigate(['tutorials'])
                  }
                )
              }
            );
          } else if (this.extractCookieProvider() == "FACEBOOK") {
            this.authService.facebookAuth(value["code"].toString()).subscribe(
              access_token => {
                this.authService.loginOAuth2Facebook(access_token).subscribe(
                  authResponse => {
                    document.cookie = `accessToken=${authResponse.accessToken}; path=/; SameSite=None; Secure`;
                    document.cookie = `refreshToken=${authResponse.refreshToken}; path=/; SameSite=None; Secure`;

                    this.router.navigate(['tutorials'])
                  }
                )
              }
            );
          } else if (this.extractCookieProvider() == "GITHUB") {
            this.authService.githubAuth(value["code"].toString()).subscribe(
              access_token => {
                this.authService.loginOAuth2Github(access_token).subscribe(
                  authResponse => {
                    document.cookie = `accessToken=${authResponse.accessToken}; path=/; SameSite=None; Secure`;
                    document.cookie = `refreshToken=${authResponse.refreshToken}; path=/; SameSite=None; Secure`;

                    this.router.navigate(['tutorials'])
                  }
                )
              }
            );
          }

        }
      }
    )

    this.loginForm = this.formBuilder.group({
      login: ['', Validators.required],
      password: ['', Validators.required],
      agree: [false, Validators.requiredTrue]
    });
  }

  login() {
    this.submitted = true;

    if (this.loginForm.invalid) {
      return;
    }

    const loginRequest: LoginRequest = {
      email: this.loginForm.get('login')?.value,
      password: this.loginForm.get('password')?.value,
    };

    this.authService.login(loginRequest).subscribe({
      next: (res) => {
        document.cookie = `accessToken=${res.accessToken}; path=/; SameSite=None; Secure`;
        document.cookie = `refreshToken=${res.refreshToken}; path=/; SameSite=None; Secure`;

        this.router.navigate(['tutorials']).then(r => window.location.reload());

      }
    })
  }

  makeRequest(): void {
    this.router.navigate(['/make-request'])
  }
}
