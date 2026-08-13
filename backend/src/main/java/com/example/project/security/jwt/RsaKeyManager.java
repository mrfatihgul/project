package com.example.project.security.jwt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class RsaKeyManager {

    public void generateKeyPair(String publicKeyCopyPath) {
        try {
            Path keyDirectory = Path.of("keys");
            try {
                Files.createDirectories(keyDirectory);
            } catch (IOException e) {
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

                    validatePem(loadedPrivatePem, loadedPublicPem);
                    copyPublicKey(loadedPublicPem, publicKeyCopyPath);
                    return;
                } catch (IOException e) {
                    throw new RuntimeException("Anahtar dosyaları okunamadı.", e);
                } catch (InvalidKeySpecException e) {
                    throw new RuntimeException("Private key verisi geçersiz.", e);
                }
            }

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            KeyPair keyPair = generator.generateKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();

            String privatePem =
                    "-----BEGIN PRIVATE KEY-----\n"
                            + Base64.getEncoder().encodeToString(privateKey.getEncoded())
                            + "\n-----END PRIVATE KEY-----\n";

            String publicPem =
                    "-----BEGIN PUBLIC KEY-----\n"
                            + Base64.getEncoder().encodeToString(publicKey.getEncoded())
                            + "\n-----END PUBLIC KEY-----\n";

            try {
                Files.writeString(privateKeyPath, privatePem);
                Files.writeString(publicKeyPath, publicPem);
                copyPublicKey(publicPem, publicKeyCopyPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("RSA algoritması kullanılamıyor", e);
        }
    }

    private void validatePem(String privatePem, String publicPem)
            throws InvalidKeySpecException, NoSuchAlgorithmException {
        String loadedPrivatePem = privatePem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        String loadedPublicPem = publicPem
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] loadedPrivateBytes = Base64.getDecoder().decode(loadedPrivatePem);
        byte[] loadedPublicBytes = Base64.getDecoder().decode(loadedPublicPem);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        keyFactory.generatePrivate(new PKCS8EncodedKeySpec(loadedPrivateBytes));
        keyFactory.generatePublic(new X509EncodedKeySpec(loadedPublicBytes));
    }

    private void copyPublicKey(String publicPem, String publicKeyCopyPath) {
        if (publicKeyCopyPath == null || publicKeyCopyPath.isBlank()) {
            return;
        }

        try {
            Path copyPath = Path.of(publicKeyCopyPath);
            if (copyPath.getParent() != null) {
                Files.createDirectories(copyPath.getParent());
            }
            Files.writeString(copyPath, publicPem);
        } catch (IOException e) {
            throw new RuntimeException(
                    "Public key kopyası yazılamadı: " + publicKeyCopyPath,
                    e
            );
        }
    }
}
