import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { RegistrationRequest } from '../registration-request';
import { RegistrationService } from '../registration.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {email: '', firstname: '', lastname: '', password: ''};
  errorMsg: Array<string> = [];
  infoMsg: Array<string> = [];

  constructor(
    private router: Router,
    private regService: RegistrationService
  ) {
  }

  login() {
    this.router.navigate(['login']);
  }

  register() {
    console.log("Register button clicked.");
    this.errorMsg = [];
    this.regService.register({
      body: this.registerRequest
    })
      .subscribe({
        next: () => {
          this.errorMsg = [];
          this.infoMsg = ['You have successfully registered a new account. Please check your email to activate your account!']
        },
        error: (err) => {
          console.log(err);
          this.infoMsg = [];
          this.errorMsg = [err.error.detail];
        }
      });
  }
}
