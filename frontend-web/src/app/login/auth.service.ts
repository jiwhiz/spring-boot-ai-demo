/* tslint:disable */
/* eslint-disable */
import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { BaseService } from '../services/base-service';
import { ApiConfiguration } from '../services/api-configuration';
import { RequestBuilder } from '../services/request-builder';
import { StrictHttpResponse } from '../services/strict-http-response';

import { AuthRequest } from './auth-request';
import { AuthResponse } from './auth-response';

/** Path part for operation `authenticate()` */
authenticate.PATH = '/authenticate';

interface Authenticate$Params {
  body: AuthRequest
}

function authenticate(http: HttpClient, rootUrl: string, params: Authenticate$Params, context?: HttpContext)
          : Observable<StrictHttpResponse<AuthResponse>> {
  const rb = new RequestBuilder(rootUrl, authenticate.PATH, 'post');
  if (params) {
    rb.body(params.body, 'application/json');
  }

  return http.request(
    rb.build({ responseType: 'json', accept: 'application/json', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return r as StrictHttpResponse<AuthResponse>;
    })
  );
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }


  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `authenticate()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate$Response(params: Authenticate$Params, context?: HttpContext)
      : Observable<StrictHttpResponse<AuthResponse>> {
    return authenticate(this.http, this.rootUrl, params, context);
  }

  /**
   * This method provides access only to the response body.
   * To access the full response (for headers, for example), `authenticate$Response()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  authenticate(params: Authenticate$Params, context?: HttpContext)
      : Observable<AuthResponse> {
    return this.authenticate$Response(params, context).pipe(
      map((r: StrictHttpResponse<AuthResponse>): AuthResponse => r.body)
    );
  }


}
