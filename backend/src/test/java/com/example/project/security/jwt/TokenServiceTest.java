package com.example.project.security.jwt;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.time.Duration;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @Mock
    private JwtEncoder jwtEncoder;

    @Test
    public void createToken_ShouldCreateCorrectToken() {

        TokenService tokenService = new TokenService(
                jwtEncoder,
                "https://project.example.com",
                3600
        );

        Jwt jwt = mock(Jwt.class);

        when(jwt.getTokenValue())
                .thenReturn("jwt-token");

        when(jwtEncoder.encode(any(JwtEncoderParameters.class)))
                .thenReturn(jwt);

        String result = tokenService.createToken("fatih");

        assertEquals("jwt-token", result);

        ArgumentCaptor<JwtEncoderParameters> captor =
                ArgumentCaptor.forClass(JwtEncoderParameters.class);

        verify(jwtEncoder).encode(captor.capture());

        JwtEncoderParameters parameters = captor.getValue();

        JwtClaimsSet claims = parameters.getClaims();

        assertEquals(
                "https://project.example.com",
                claims.getIssuer().toString()
        );

        assertEquals("fatih", claims.getSubject());

        assertEquals("USER", claims.getClaim("scope"));

        assertNotNull(claims.getIssuedAt());
        assertNotNull(claims.getExpiresAt());

        Instant issuedAt = claims.getIssuedAt();
        Instant expiresAt = claims.getExpiresAt();

        assertEquals(
                3600,
                Duration.between(issuedAt, expiresAt).getSeconds()
        );
    }
}