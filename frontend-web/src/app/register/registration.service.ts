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

import { RegistrationRequest } from './registration-request';

/** Path part for operation `register()` */
register.PATH = '/register';

interface Register$Params {
  body: RegistrationRequest
}

function register(http: HttpClient, rootUrl: string, params: Register$Params, context?: HttpContext)
          : Observable<StrictHttpResponse<{}>> {
  const rb = new RequestBuilder(rootUrl, register.PATH, 'post');
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
export class RegistrationService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  /**
   * This method provides access to the full `HttpResponse`, allowing access to response headers.
   * To access only the response body, use `register()` instead.
   *
   * This method sends `application/json` and handles request body of type `application/json`.
   */
  register$Response(params: Register$Params, context?: HttpContext)
      : Observable<StrictHttpResponse<{}>> {
      return register(this.http, this.rootUrl, params, context);
    }

    /**
     * This method provides access only to the response body.
     * To access the full response (for headers, for example), `register$Response()` instead.
     *
     * This method sends `application/json` and handles request body of type `application/json`.
     */
    register(params: Register$Params, context?: HttpContext)
        : Observable<{}> {
      return this.register$Response(params, context).pipe(
        map((r: StrictHttpResponse<{}>): {} => r.body)
      );
    }

}
