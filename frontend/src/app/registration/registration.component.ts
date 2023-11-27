import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {RegisterRequest} from "../models/tutorial.model";
import {AuthService} from "../services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-register',
  templateUrl: 'registration.component.html',
  styleUrls: ['registration.component.html']
})
export class RegisterComponent {
  registerForm: FormGroup;
  submitted = false;
  registerRequest: RegisterRequest = {
    email: '',
    firstName: '',
    lastName: '',
    password: '',
    repeatPassword: ''
  }

  constructor(private formBuilder: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.formBuilder.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
      repeatPassword: ['', Validators.required]
    }, {
      validators: this.passwordsMatchValidator
    });
  }

  register() {
    this.submitted = true;

    if (this.registerForm.invalid) {
      return;
    }

    const registerRequest: RegisterRequest = {
      firstName: this.registerForm.get('firstName')?.value,
      lastName: this.registerForm.get('lastName')?.value,
      email: this.registerForm.get('email')?.value,
      password: this.registerForm.get('password')?.value,
      repeatPassword: this.registerForm.get('repeatPassword')?.value,
    };

    this.authService.register(registerRequest)

    this.router.navigate(['login']);
  }

  private passwordsMatchValidator(form: FormGroup) {
    const password = form.get('password')?.value;
    const repeatPassword = form.get('repeatPassword')?.value;

    if (password !== repeatPassword) {
      form.get('repeatPassword')?.setErrors({passwordsNotMatch: true});
    } else {
      form.get('repeatPassword')?.setErrors(null);
    }

    return null;
  }
}
