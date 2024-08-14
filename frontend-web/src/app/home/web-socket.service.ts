import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { webSocket, WebSocketSubject } from 'rxjs/webSocket';
import { Client, IFrame, Message, StompConfig } from '@stomp/stompjs';
import { TokenService } from '../services/token/token.service';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  tokenService = inject(TokenService);

  stompConfig : StompConfig = {
    brokerURL: 'ws://localhost:8080/ws',

    connectHeaders: {
      Authorization: 'Bearer ' + this.tokenService.token
    },

    debug: function (str:string) {
      console.log('Stomp:' + str);
    },

    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,

    //onConnect: this.onConnect,

    onStompError: this.onStompError,
  };

  public stompClient : Client = new Client(this.stompConfig);

  onStompError(frame: IFrame) {
    // Will be invoked in case of error encountered at Broker
    // Bad login/passcode typically will cause an error
    // Complaint brokers will set `message` header with a brief message. Body may contain details.
    // Compliant brokers will terminate the connection after any error
    console.log('Broker reported error: ' + frame.headers['message']);
    console.log('Additional details: ' + frame.body);
  };

  sendMessage(message: string) {
    const payload = {content: message};

    this.stompClient.publish({
      destination: '/app/chat',
      body: JSON.stringify(payload)
    })
  }
}
