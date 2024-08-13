package com.jiwhiz.demo;

import org.springframework.boot.SpringApplication;

public class TestAiChatbotDemoApplication {

    public static void main(String[] args) {
        SpringApplication.from(AiChatbotDemoApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
