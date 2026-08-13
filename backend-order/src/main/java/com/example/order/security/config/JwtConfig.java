package com.example.order.security.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

@Configuration
public class JwtConfig {

    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.jwt.issuer}") String issuer) {
        RSAPublicKey publicKey = loadPublicKey();

        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(publicKey).build();
        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
        return decoder;
    }

    private RSAPublicKey loadPublicKey() {
        try {
            Path publicKeyPath = Path.of("keys", "public.pem");
            String publicPem = Files.readString(publicKeyPath)
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");

            byte[] publicBytes = Base64.getDecoder().decode(publicPem);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(
                    new X509EncodedKeySpec(publicBytes)
            );
        } catch (IOException e) {
            throw new IllegalStateException(
                    "Public key okunamadı. Önce birinci backend'i çalıştırıp public.pem kopyasının oluşmasını bekleyin.",
                    e
            );
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Public key yüklenemedi", e);
        }
    }
}
