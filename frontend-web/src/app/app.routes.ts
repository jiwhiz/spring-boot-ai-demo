import { Routes } from '@angular/router';
import { LoginComponent } from './login/page/login.component';
import { RegisterComponent } from './register/register.component';

export const routes: Routes = [
  {
    path: 'login',
    title: 'AI Chatbot Demo - Login',
    component: LoginComponent
  },
  {
    path: 'register',
    title: 'AI Chatbot Demo - Register New User',
    component: RegisterComponent
  },
];
