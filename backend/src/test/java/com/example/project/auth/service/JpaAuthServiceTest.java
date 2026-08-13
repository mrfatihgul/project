package com.example.project.auth.service;
import com.example.project.auth.dto.AuthRequest;
import com.example.project.auth.dto.AuthResponse;
import com.example.project.security.jwt.TokenService;
import com.example.project.user.entity.AppUser;
import com.example.project.user.repository.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JpaAuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private JpaAuthService jpaAuthService;

    @Test
    public void register_ShouldCreateUser_WhenRequestIsValid() {

        AuthRequest request = new AuthRequest("fatih", "123456");

        when(appUserRepository.existsByUsername("fatih"))
                .thenReturn(false);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(tokenService.createToken("fatih"))
                .thenReturn("jwt-token");

        AuthResponse response = jpaAuthService.register(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());

        verify(appUserRepository).save(any(AppUser.class));
    }

    @Test
    public void register_ShouldThrowException_WhenUsernameAlreadyExists() {

        AuthRequest request = new AuthRequest("fatih", "123456");

        when(appUserRepository.existsByUsername("fatih"))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> jpaAuthService.register(request)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals("Kullanıcı adı zaten kullanılıyor", exception.getReason());
    }

    @Test
    public void login_ShouldReturnToken_WhenCredentialsAreValid() {

        AuthRequest request = new AuthRequest("fatih", "123456");

        when(authenticationManager.authenticate(any()))
                .thenReturn(null);

        when(tokenService.createToken("fatih"))
                .thenReturn("jwt-token");

        AuthResponse response = jpaAuthService.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.token());

        verify(authenticationManager).authenticate(any());

        verify(tokenService).createToken("fatih");
    }

    @Test
    public void login_ShouldThrowException_WhenPasswordIsWrong() {

        AuthRequest request = new AuthRequest("fatih", "yanlisSifre");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Hatalı şifre"));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> jpaAuthService.login(request)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals(
                "Kullanıcı adı veya şifre yanlış",
                exception.getReason()
        );
    }
}
