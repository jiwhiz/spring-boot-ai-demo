package com.jiwhiz.demo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import com.jiwhiz.demo.common.ApplicationProperties;

import static net.logstash.logback.marker.Markers.append;

@SpringBootApplication
@EnableConfigurationProperties({ApplicationProperties.class})
@EnableAsync
@Slf4j
public class AiChatbotDemoApplication {

    public static final String SPRING_PROFILE_DEVELOPMENT = "dev";
    public static final String SPRING_PROFILE_PRODUCTION = "prod";

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AiChatbotDemoApplication.class);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

        private static void logApplicationStartup(Environment env) {
        String[] profiles = env.getActiveProfiles().length == 0 ? env.getDefaultProfiles() : env.getActiveProfiles();
        if (Arrays.asList(profiles).contains(SPRING_PROFILE_PRODUCTION)) {
            Map<String, Object> appPropMap = new HashMap<>();
            appPropMap.put("name", env.getProperty("spring.application.name"));
            appPropMap.put("version", env.getProperty("spring.application.version"));
            appPropMap.put("dbURL", env.getProperty("spring.datasource.url"));
            appPropMap.put("dbUsername", env.getProperty("spring.datasource.username"));
            appPropMap.put("profiles", profiles);
            log.info(append("application", appPropMap), "Application is running.");
        } else {
            log.info("\n----------------------------------------------------------\n\t" +
                            "Application '{}' is running with version {}!\n\t" +
                            "DB URL:      \t{} \n\t" +
                            "DB Username: \t{} \n\t" +
                            "Profile(s):  \t{}\n----------------------------------------------------------",
                    env.getProperty("spring.application.name"),
                    env.getProperty("spring.application.version"),
                    env.getProperty("spring.datasource.url"),
                    env.getProperty("spring.datasource.username"),
                    profiles
            );
        }
    }

}
