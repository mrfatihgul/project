package com.example.gateway.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Enumeration;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class DownstreamHttpClient {

    private static final Set<String> HOP_BY_HOP_HEADERS = Set.of(
            "connection",
            "keep-alive",
            "proxy-authenticate",
            "proxy-authorization",
            "te",
            "trailers",
            "transfer-encoding",
            "upgrade",
            "host",
            "content-length"
    );

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    public ResponseEntity<byte[]> forward(
            String baseUrl,
            HttpServletRequest request,
            byte[] body
    ) throws IOException, InterruptedException {
        String query = request.getQueryString();
        String targetUrl = baseUrl + request.getRequestURI()
                + (query == null || query.isBlank() ? "" : "?" + query);

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(targetUrl))
                .timeout(Duration.ofSeconds(30));

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            if (HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(name);
            while (values.hasMoreElements()) {
                builder.header(name, values.nextElement());
            }
        }

        String method = request.getMethod();
        if (body != null && body.length > 0) {
            builder.method(method, HttpRequest.BodyPublishers.ofByteArray(body));
        } else {
            builder.method(method, HttpRequest.BodyPublishers.noBody());
        }

        HttpResponse<byte[]> response = httpClient.send(
                builder.build(),
                HttpResponse.BodyHandlers.ofByteArray()
        );

        HttpHeaders responseHeaders = new HttpHeaders();
        response.headers().map().forEach((name, values) -> {
            if (!HOP_BY_HOP_HEADERS.contains(name.toLowerCase())) {
                responseHeaders.put(name, values);
            }
        });

        byte[] responseBody = response.body() != null ? response.body() : new byte[0];
        return ResponseEntity.status(HttpStatusCode.valueOf(response.statusCode()))
                .headers(responseHeaders)
                .body(responseBody);
    }
}
