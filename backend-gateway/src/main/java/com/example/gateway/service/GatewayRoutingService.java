package com.example.gateway.service;

import java.io.IOException;

import com.example.gateway.client.DownstreamHttpClient;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GatewayRoutingService {

    private final DownstreamHttpClient downstreamHttpClient;
    private final String backendBaseUrl;
    private final String orderBaseUrl;

    public GatewayRoutingService(
            DownstreamHttpClient downstreamHttpClient,
            @Value("${app.backend.base-url}") String backendBaseUrl,
            @Value("${app.order.base-url}") String orderBaseUrl
    ) {
        this.downstreamHttpClient = downstreamHttpClient;
        this.backendBaseUrl = backendBaseUrl;
        this.orderBaseUrl = orderBaseUrl;
    }

    public ResponseEntity<byte[]> route(
            HttpServletRequest request,
            byte[] body
    ) throws IOException, InterruptedException {
        String path = request.getRequestURI();
        String targetBaseUrl = path.startsWith("/api/orders")
                ? orderBaseUrl
                : backendBaseUrl;
        return downstreamHttpClient.forward(targetBaseUrl, request, body);
    }
}
