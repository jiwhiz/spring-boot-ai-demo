import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Client, IFrame, Message, StompConfig } from '@stomp/stompjs';
import { WebSocketService } from './web-socket.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  messages: string[] = [];
  newMsg: string = '';
  isConnected: boolean = false;

  constructor(
    private router: Router,
    private webSocketService: WebSocketService
  ) {

  }

  ngOnInit() : void {
    console.log('home page OnInit()');
    const stompClient = this.webSocketService.stompClient;

    stompClient.onConnect = (frame: IFrame) => {
      console.log('call onConnect()');
      stompClient.subscribe('/user/queue',
        (message) => {
          console.log('got message: ' + message);
          if (message?.body) {
            const payload = JSON.parse(message.body);
            console.log(payload);
            this.messages.push(payload.content);
          }
        }
      )
    }
    stompClient.activate();
  }

  sendMessage(): void {
    if (this.newMsg.trim()) {
      this.messages.push(this.newMsg);
      this.webSocketService.sendMessage(this.newMsg);
      this.newMsg = '';
    }
  }

}
