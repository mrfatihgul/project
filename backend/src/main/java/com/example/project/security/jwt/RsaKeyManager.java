package com.example.project.security.jwt;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.nio.file.Path;
import java.nio.file.Files;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;

public class RsaKeyManager {
    public KeyPair generateKeyPair() {
        try {
            KeyPairGenerator generator =
                    KeyPairGenerator.getInstance("RSA");

            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            byte[] privateBytes = privateKey.getEncoded();
            byte[] publicBytes = publicKey.getEncoded();

            String privateBase64 = Base64.getEncoder()
                    .encodeToString(privateBytes);

            String publicBase64 = Base64.getEncoder()
                    .encodeToString(publicBytes);

            String privatePem =
                    "-----BEGIN PRIVATE KEY-----\n"
                            + privateBase64
                            + "\n-----END PRIVATE KEY-----\n";

            String publicPem =
                    "-----BEGIN PUBLIC KEY-----\n"
                            + publicBase64
                            + "\n-----END PUBLIC KEY-----\n";

            Path keyDirectory = Path.of("keys");
            try {
                Files.createDirectories(keyDirectory);

            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }

            Path privateKeyPath = keyDirectory.resolve("private.pem");
            Path publicKeyPath = keyDirectory.resolve("public.pem");

            boolean privateKeyExists = Files.exists(privateKeyPath);
            boolean publicKeyExists = Files.exists(publicKeyPath);

            if (privateKeyExists && publicKeyExists) {
                try {
                    String loadedPrivatePem = Files.readString(privateKeyPath);
                    String loadedPublicPem = Files.readString(publicKeyPath);
                    loadedPrivatePem = loadedPrivatePem
                            .replace("-----BEGIN PRIVATE KEY-----", "")
                            .replace("-----END PRIVATE KEY-----", "")
                            .replaceAll("\\s", "");

                    loadedPublicPem = loadedPublicPem
                            .replace("-----BEGIN PUBLIC KEY-----", "")
                            .replace("-----END PUBLIC KEY-----", "")
                            .replaceAll("\\s", "");

                    byte[] loadedPrivateBytes = Base64.getDecoder().decode(loadedPrivatePem);
                    byte[] loadedPublicBytes = Base64.getDecoder().decode(loadedPublicPem);
                    KeyFactory keyFactory = KeyFactory.getInstance("RSA");

                    PKCS8EncodedKeySpec privateKeySpec =
                            new PKCS8EncodedKeySpec(loadedPrivateBytes);
                    X509EncodedKeySpec publicKeySpec =
                            new X509EncodedKeySpec(loadedPublicBytes);

                    PrivateKey loadedPrivateKey =
                            keyFactory.generatePrivate(privateKeySpec);

                    PublicKey loadedPublicKey =
                            keyFactory.generatePublic(publicKeySpec);

                } catch (IOException e) {
                    throw new RuntimeException("Anahtar dosyaları okunamadı.", e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException("Private key verisi geçersiz.", e);
                }
            }

            try {
                Files.writeString(privateKeyPath, privatePem);
                Files.writeString(publicKeyPath, publicPem);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }



            return generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "RSA algoritması kullanılamıyor",
                    e
            );
        }
    }
}
