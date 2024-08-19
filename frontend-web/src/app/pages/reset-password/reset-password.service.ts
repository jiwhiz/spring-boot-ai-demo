import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { BaseService } from '../../services/base-service';
import { ApiConfiguration } from '../../services/api-configuration';
import { RequestBuilder } from '../../services/request-builder';
import { StrictHttpResponse } from '../../services/strict-http-response';

export interface InitResetPassword {
  email: string;
}

/** Path part for operation `initResetPassword()` */
initResetPassword.PATH = '/reset-password/init';

interface InitResetPassword$Params {
  body: InitResetPassword
}

function initResetPassword(http: HttpClient, rootUrl: string, params: InitResetPassword$Params, context?: HttpContext)
          : Observable<StrictHttpResponse<{}>> {
  const rb = new RequestBuilder(rootUrl, initResetPassword.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<{
      }>;
    })
  );
}

export interface FinishResetPassword {
  key: string;
  newPassword: string
}

/** Path part for operation `finishResetPassword()` */
finishResetPassword.PATH = '/reset-password/finish';

interface FinishResetPassword$Params {
  body: FinishResetPassword
}

function finishResetPassword(http: HttpClient, rootUrl: string, params: FinishResetPassword$Params, context?: HttpContext)
          : Observable<StrictHttpResponse<{}>> {
  const rb = new RequestBuilder(rootUrl, finishResetPassword.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<{
      }>;
    })
  );
}

@Injectable({ providedIn: 'root' })
export class ResetPasswordService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  initResetPassword$Response(params: InitResetPassword$Params, context?: HttpContext)
      : Observable<StrictHttpResponse<{}>> {
      return initResetPassword(this.http, this.rootUrl, params, context);
    }

  initResetPassword(params: InitResetPassword$Params, context?: HttpContext)
      : Observable<{}> {
    return this.initResetPassword$Response(params, context).pipe(
      map((r: StrictHttpResponse<{}>): {} => r.body)
    );
  }

  finishResetPassword$Response(params: FinishResetPassword$Params, context?: HttpContext)
    : Observable<StrictHttpResponse<{}>> {
    return finishResetPassword(this.http, this.rootUrl, params, context);
  }

  finishResetPassword(params: FinishResetPassword$Params, context?: HttpContext)
    : Observable<{}> {
  return this.finishResetPassword$Response(params, context).pipe(
    map((r: StrictHttpResponse<{}>): {} => r.body)
  );
  }
}
