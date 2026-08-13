package com.example.gateway.controller;

import java.io.IOException;

import com.example.gateway.service.GatewayRoutingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    private final GatewayRoutingService gatewayRoutingService;

    public GatewayController(GatewayRoutingService gatewayRoutingService) {
        this.gatewayRoutingService = gatewayRoutingService;
    }

    @RequestMapping({
            "/api/orders",
            "/api/orders/**",
            "/api/auth/**",
            "/api/items",
            "/api/items/**"
    })
    public ResponseEntity<byte[]> proxy(
            HttpServletRequest request,
            @RequestBody(required = false) byte[] body
    ) throws IOException, InterruptedException {
        return gatewayRoutingService.route(request, body);
    }
}
