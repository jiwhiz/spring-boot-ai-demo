import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthenticationService } from './auth.service';
import { AuthRequest } from './auth-request';
import { TokenService } from '../../services/token/token.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authRequest: AuthRequest = {username: '', password: '', rememberMe : false};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
    private authService: AuthenticationService,
    private tokenService: TokenService
  ) {
  }

  login() {
    console.log("login with user " + JSON.stringify(this.authRequest));
    this.errorMsg = [];
    this.authService.authenticate({
      body: this.authRequest
    }).subscribe({
      next: (res) => {
        console.log(res);
        this.tokenService.token = res.token as string;
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.errorMsg);
        }
      }
    });
  }

  register() {
    this.router.navigate(['/register']);
  }

  resetPassword() {
    this.router.navigate(['/reset-password/init']);
  }
}
