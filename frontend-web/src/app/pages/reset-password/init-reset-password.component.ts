import { Component } from '@angular/core';
import { Router } from '@angular/router';
//import { ResetPasswordRequest } from './reset-password-request';
import { ResetPasswordService, InitResetPassword } from './reset-password.service';

@Component({
  selector: 'app-init-reset-password',
  templateUrl: './init-reset-password.component.html',
  styleUrls: ['./init-reset-password.component.scss']
})
export class InitResetPasswordComponent {

  initResetPassword: InitResetPassword = {email: ''};
  errorMsg: Array<string> = [];
  infoMsg: Array<string> = [];

  constructor(
    private router: Router,
    private resetPasswordService: ResetPasswordService
  ) {
  }

  requestResetPassword() {
    console.log("Reset Password button clicked.");
    this.errorMsg = [];
    this.resetPasswordService.initResetPassword({
      body: this.initResetPassword
    })
      .subscribe({
        next: () => {
          this.errorMsg = [];
          this.infoMsg = ['You sucessfully requested password reset, please check your email!']
        },
        error: (err) => {
          console.log(err);
          this.infoMsg = [];
          this.errorMsg = [err.error.detail];
        }
      });
  }
}
