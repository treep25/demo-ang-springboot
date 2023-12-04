import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from "../services/auth.service";
import {LoginRequest} from "../models/tutorial.model";
import {Router} from "@angular/router";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  loginForm: FormGroup;
  submitted = false;

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router) {
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
