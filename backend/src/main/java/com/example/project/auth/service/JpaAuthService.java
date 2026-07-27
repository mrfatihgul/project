package com.example.project.auth.service;
import com.example.project.auth.dto.AuthRequest;
import com.example.project.auth.dto.AuthResponse;
import com.example.project.security.jwt.TokenService;
import com.example.project.user.entity.AppUser;
import com.example.project.user.repository.AppUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JpaAuthService implements AuthService {

    private final AppUserRepository appUserRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtEncoder jwtEncoder;

    private final AuthenticationManager authenticationManager;

    private final TokenService tokenService;

    public JpaAuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            AuthenticationManager authenticationManager,
            TokenService tokenService,
            AppUserRepository userRepository
    ) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public AuthResponse login(AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );
        } catch (AuthenticationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Kullanıcı adı veya şifre yanlış"
            );
        }

        return new AuthResponse(
                tokenService.createToken(request.username())
        );
    }

    @Override
    public AuthResponse register(AuthRequest request) {
        if (appUserRepository.existsByUsername(request.username())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Kullanıcı adı zaten kullanılıyor"
            );
        }

        AppUser user = new AppUser();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        appUserRepository.save(user);

        return new AuthResponse(tokenService.createToken(user.getUsername()));
    }
}
