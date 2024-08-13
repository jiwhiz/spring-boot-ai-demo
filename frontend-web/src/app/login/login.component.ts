import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {AuthRequest} from './auth-request';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {

  authRequest: AuthRequest = {email: '', password: '', rememberMe : false};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
  ) {
  }

  login() {
    console.log("login button clicked");
  }

  register() {
    this.router.navigate(['register']);
  }
}
