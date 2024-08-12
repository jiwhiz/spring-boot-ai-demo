package com.jiwhiz.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.jiwhiz.demo.common.ApplicationProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@Slf4j
public class AiChatbotDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiChatbotDemoApplication.class, args);
    }

}
