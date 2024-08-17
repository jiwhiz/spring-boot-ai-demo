import { Injectable, inject } from '@angular/core';
import { Client, IFrame, StompConfig } from '@stomp/stompjs';
import { ApiConfiguration } from '../../services/api-configuration';
import { TokenService } from '../../services/token/token.service';

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompConfig : StompConfig;
  public stompClient : Client;

  constructor(private config: ApiConfiguration, private tokenService: TokenService){
    this.stompConfig = {
      brokerURL: this.config.wsBrokerUrl,

      connectHeaders: {
        Authorization: 'Bearer ' + this.tokenService.token
      },

      debug: function (str:string) {
        console.log('Stomp:' + str);
      },

      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onStompError: this.onStompError,
    };

    this.stompClient = new Client(this.stompConfig);

  }

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
