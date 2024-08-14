import { NgModule } from '@angular/core';
import { RouterModule, Routes, ExtraOptions } from '@angular/router';
import { HomeComponent } from './home/page/home.component';
import { LoginComponent } from './login/page/login.component';
import { RegisterComponent } from './register/page/register.component';
import { ActivateAccountComponent } from './activate/page/activate.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    component: HomeComponent
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: 'register',
    component: RegisterComponent
  },
  {
    path: 'activate',
    component: ActivateAccountComponent
  },
];

const options: ExtraOptions = {
  bindToComponentInputs: true
}

@NgModule({
  imports: [RouterModule.forRoot(routes, options)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
