package com.jiwhiz.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.jiwhiz.demo.common.ApplicationProperties;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
@Slf4j
public class JWTTokenService {
    private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

    private final SecretKey key;

    private final JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    public JWTTokenService(ApplicationProperties appProperties) {
        byte[] keyBytes;
        String secret = appProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();
        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                "Warning: the JWT key used is not Base64-encoded. " +
                "We recommend using the `app.security.authentication.jwt.base64-secret` key for optimum security."
            );
            secret = appProperties.getSecurity().getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parser().verifyWith(key).build();
        this.tokenValidityInMilliseconds =
            1000 * appProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
            1000 * appProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();

    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication
            .getAuthorities()
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        Date now = new Date();
        Date validity;
        if (rememberMe) {
            validity = new Date(now.getTime() + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now.getTime() + this.tokenValidityInMilliseconds);
        }

        return Jwts
            .builder()
            .subject(authentication.getName())
            .claim("auth", authorities)
            .signWith(key, SIG.HS512)
            .issuedAt(now)
            .expiration(validity)
            .compact();

    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        Collection<? extends GrantedAuthority> authorities = Arrays
            .stream(claims.get("auth").toString().split(","))
            .filter(auth -> !auth.trim().isEmpty())
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseSignedClaims(authToken);

            return true;
        } catch (ExpiredJwtException e) {
            log.trace(INVALID_JWT_TOKEN, e);
        } catch (UnsupportedJwtException e) {
            log.trace(INVALID_JWT_TOKEN, e);
        } catch (MalformedJwtException e) {
            log.trace(INVALID_JWT_TOKEN, e);
        } catch (SignatureException e) {
            log.trace(INVALID_JWT_TOKEN, e);
        } catch (IllegalArgumentException e) { // TODO: should we let it bubble (no catch), to avoid defensive programming and follow the fail-fast principle?
            log.error("Token validation error {}", e.getMessage());
        }

        return false;
    }
}
