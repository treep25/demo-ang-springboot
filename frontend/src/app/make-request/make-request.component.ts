import {Component, OnInit} from '@angular/core';
import {AuthService} from "../services/auth.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";

@Component({
  selector: 'app-make-request',
  templateUrl: './make-request.component.html',
  styleUrls: ['./make-request.component.css']
})
export class MakeRequestComponent implements OnInit {


  issueForm!: FormGroup;
  submitted = false;

  constructor(private authService: AuthService, private formBuilder: FormBuilder, private router: Router) {
  }

  ngOnInit(): void {
    this.issueForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      issue: ['', Validators.required]
    });
  }

  submitForm() {
    this.submitted = true;
    if (this.issueForm?.valid) {
      const requestBody = {
        email: this.issueForm.get('email')?.value,
        issue: this.issueForm.get('issue')?.value
      };

      this.authService.createRequest(requestBody.email, requestBody.issue).subscribe(
        value => {
          console.log(value);
          this.router.navigate(['/login']);
        },
        error => console.error()
      );
    }
  }
}
