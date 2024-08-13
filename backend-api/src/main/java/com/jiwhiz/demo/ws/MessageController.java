package com.jiwhiz.demo.ws;

import java.util.function.Function;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;

import com.jiwhiz.demo.ws.MessageController.MockWeatherService.WeatherRequest;
import com.jiwhiz.demo.ws.MessageController.MockWeatherService.WeatherResponse;


@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final ChatClient.Builder chatClientBuilder;

    @MessageMapping("/chat")
    @SendToUser("/queue")
    public MessageDTO chat(@Payload MessageDTO message) {
        log.info("Message received: {}", message);

        var response = chatClientBuilder.build()
                .prompt()
                .user(message.content())
                .functions("weatherFunction") // reference by bean name.
                .call()
                .content();
        log.info("Message response: {}", response);

        return new MessageDTO(response);
    }

    @Bean
    @Description("Get the weather in location")
    public Function<WeatherRequest, WeatherResponse> weatherFunction() {
        return new MockWeatherService();
    }

    public static class MockWeatherService
            implements Function<MockWeatherService.WeatherRequest, MockWeatherService.WeatherResponse> {

        public record WeatherRequest(String location, String unit) {
        }

        public record WeatherResponse(double temp, String unit) {
        }

        @Override
        public WeatherResponse apply(WeatherRequest request) {
            double temperature = request.location().contains("Amsterdam") ? 20 : 25;
            return new WeatherResponse(temperature, request.unit);
        }

    }

}
