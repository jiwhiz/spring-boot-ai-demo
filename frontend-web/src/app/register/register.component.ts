import {Component} from '@angular/core';
import {Router} from '@angular/router';
import {RegistrationRequest} from './registration-request';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {

  registerRequest: RegistrationRequest = {email: '', firstname: '', lastname: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private router: Router,
  ) {
  }

  login() {
    this.router.navigate(['login']);
  }

  register() {
    console.log("Register button clicked.")
  }
}
