import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ActivationService } from './activate.service';
import { skipUntil } from 'rxjs';

@Component({
  selector: 'app-activate',
  templateUrl: './activate.component.html',
  styleUrls: ['./activate.component.scss']
})
export class ActivateAccountComponent implements OnInit {
  @Input() key!:string;

  message = '';
  isOkay = true;
  submitted = false;
  constructor(
    private router: Router,
    private activateService: ActivationService
  ) {}

  ngOnInit(): void {
    console.info("activation key:",this.key);
    if (this.key) {
      this.activateAccount(this.key);
    }
  }

  private activateAccount(key: string) {
    this.activateService.activate({
      key: key
    }).subscribe({
      next: () => {
        this.message = 'Your account has been successfully activated.\nNow you can proceed to login';
        this.isOkay = true;
      },
      error: () => {
        this.message = 'Token has been expired or invalid';
        this.isOkay = false;
      }
    });
  }

  redirectToLogin() {
    this.router.navigate(['login']);
  }

  protected readonly skipUntil = skipUntil;
}
