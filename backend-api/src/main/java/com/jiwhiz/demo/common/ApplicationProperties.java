package com.jiwhiz.demo.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Properties specific to AI Chatbot Demo. Copied from jHipster.
 *
 * <p> Properties are configured in the application.yml file. </p>
 * <p> This class also load properties in the Spring Environment from the git.properties and META-INF/build-info.properties
 * files if they are found in the classpath.</p>
 */
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
@Getter
public class ApplicationProperties {

    private final Security security = new Security();

    private final CorsConfiguration cors = new CorsConfiguration();

    @Getter
    public static class Security {

        private String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

        private final ClientAuthorization clientAuthorization = new ClientAuthorization();

        private final Authentication authentication = new Authentication();

        private final RememberMe rememberMe = new RememberMe();

        private final OAuth2 oauth2 = new OAuth2();

        @Getter
        @Setter
        public static class ClientAuthorization {

            private String accessTokenUri = null;

            private String tokenServiceId = null;

            private String clientId = null;

            private String clientSecret = null;

        }

        @Getter
        @Setter
        public static class Authentication {

            private final Jwt jwt = new Jwt();

            @Getter
            @Setter
            public static class Jwt {

                private String secret = null;

                private String base64Secret = null;

                private long tokenValidityInSeconds = 1800; // 30 minutes

                private long tokenValidityInSecondsForRememberMe = 2592000; // 30 days

            }
        }

        @Getter
        @Setter
        public static class RememberMe {

            private String key = null;
        }

        @Getter
        @Setter
        public static class OAuth2 {
            private List<String> audience = new ArrayList<>();
        }
    }

}
