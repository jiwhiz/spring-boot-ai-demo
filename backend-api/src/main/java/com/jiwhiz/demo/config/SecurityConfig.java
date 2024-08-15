package com.jiwhiz.demo.config;

import com.jiwhiz.demo.common.ApplicationProperties;
import com.jiwhiz.demo.common.Constants;
import com.jiwhiz.demo.security.JWTTokenFilter;
import com.jiwhiz.demo.security.JWTTokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableWebSocketSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {

    private final ApplicationProperties appProperties;

    private final JWTTokenService jwtTokenService;

    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // @formatter:off
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors((cors) -> cors
                .configurationSource(request -> appProperties.getCors())
            )
            .addFilterBefore(new JWTTokenFilter(jwtTokenService), UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers(Constants.API_ENDPOINT_BASE + "/authenticate").permitAll()
                .requestMatchers(Constants.API_ENDPOINT_BASE + "/register").permitAll()
                .requestMatchers(Constants.API_ENDPOINT_BASE + "/activate").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(Customizer.withDefaults())
            .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            ;
        // @formatter:on
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(passwordEncoder());
        provider.setUserDetailsService(userDetailsService);
        return new ProviderManager(provider);
    }

    /**
     * Create an empty ChannelInterceptor for CSRF, so we can disable csrf in Websocket.
     * @return
     */
    @Bean
    public ChannelInterceptor csrfChannelInterceptor() {
        return new ChannelInterceptor() {};
    }

    @Bean
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
                .simpTypeMatchers(SimpMessageType.CONNECT,
                        SimpMessageType.DISCONNECT, SimpMessageType.OTHER).permitAll()
                //.simpDestMatchers("/app/**").hasRole("USER")
                .anyMessage().permitAll()
                ;

        return messages.build();
    }
}
