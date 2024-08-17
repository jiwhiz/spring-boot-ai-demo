import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';

import { BaseService } from '../../services/base-service';
import { ApiConfiguration } from '../../services/api-configuration';
import { RequestBuilder } from '../../services/request-builder';
import { StrictHttpResponse } from '../../services/strict-http-response';


/** Path part for operation `activate()` */
activate.PATH = '/activate';

export interface Activate$Params {
  key: string;
}

function activate(http: HttpClient, rootUrl: string, params: Activate$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
  const rb = new RequestBuilder(rootUrl, activate.PATH, 'get');
  if (params) {
    rb.query('key', params.key, {});
  }

  return http.request(
    rb.build({ responseType: 'text', accept: '*/*', context })
  ).pipe(
    filter((r: any): r is HttpResponse<any> => r instanceof HttpResponse),
    map((r: HttpResponse<any>) => {
      return (r as HttpResponse<any>).clone({ body: undefined }) as StrictHttpResponse<void>;
    })
  );
}

@Injectable({ providedIn: 'root' })
export class ActivationService extends BaseService {
  constructor(config: ApiConfiguration, http: HttpClient) {
    super(config, http);
  }

  activate$Response(params: Activate$Params, context?: HttpContext): Observable<StrictHttpResponse<void>> {
    return activate(this.http, this.rootUrl, params, context);
  }

  activate(params: Activate$Params, context?: HttpContext): Observable<void> {
    return this.activate$Response(params, context).pipe(
      map((r: StrictHttpResponse<void>): void => r.body)
    );
  }
}
