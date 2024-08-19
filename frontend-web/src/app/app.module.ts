import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClient, HttpClientModule } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { ActivateAccountComponent } from './pages/activate/activate.component';
import { HomeComponent } from './pages/home/home.component';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { InitResetPasswordComponent } from './pages/reset-password/init-reset-password.component'
import { FinishResetPasswordComponent } from './pages/reset-password/finish-reset-password.component';
import { HttpTokenInterceptor } from './services/interceptor/http-token.interceptor';


@NgModule({
  declarations: [
    AuthLayoutComponent,
    MainLayoutComponent,
    AppComponent,
    HomeComponent,
    LoginComponent,
    RegisterComponent,
    ActivateAccountComponent,
    InitResetPasswordComponent,
    FinishResetPasswordComponent
  ],
  imports: [
      BrowserModule,
      AppRoutingModule,
      FormsModule,
      HttpClientModule,
      NgbModule,
  ],
  providers: [
    HttpClient,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpTokenInterceptor,
      multi: true
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
