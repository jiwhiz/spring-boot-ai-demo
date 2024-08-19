import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';
import { ResetPasswordService, FinishResetPassword } from './reset-password.service';

@Component({
  selector: 'app-finish-reset-password',
  templateUrl: './finish-reset-password.component.html',
  styleUrls: ['./finish-reset-password.component.scss']
})
export class FinishResetPasswordComponent {
  @Input() key!:string;

  passwordForm = { password: '', password2: ''};

  errorMsg: Array<string> = [];
  infoMsg: Array<string> = [];

  constructor(
    private router: Router,
    private resetPasswordService: ResetPasswordService
  ) {
  }

  login() {
    this.router.navigate(['login']);
  }

  reset() {
    console.log("Reset button clicked.");
    console.log("key="+this.key);
    var resetRequest: FinishResetPassword = {key: this.key, newPassword: this.passwordForm.password};

    this.errorMsg = [];
    this.resetPasswordService.finishResetPassword({
      body: resetRequest
    })
      .subscribe({
        next: () => {
          this.errorMsg = [];
          this.infoMsg = ['You have successfully reset your password. Please login with your new password.']
        },
        error: (err) => {
          console.log(err);
          this.infoMsg = [];
          this.errorMsg = ["Reset password failed!", err.error.detail];
        }
      });
  }
}
