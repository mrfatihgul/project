package com.example.project.auth.service;

import com.example.project.auth.dto.AuthRequest;
import com.example.project.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse login(AuthRequest request);

    AuthResponse register(AuthRequest request);
}
