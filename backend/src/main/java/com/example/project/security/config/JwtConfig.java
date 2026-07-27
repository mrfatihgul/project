package com.example.project.security.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import com.example.project.security.jwt.RsaKeyManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

@Configuration
public class JwtConfig {

	@Bean
	public KeyPair rsaKeyPair() {
		// RsaKeyManager keys/private.pem ve keys/public.pem yazar
		new RsaKeyManager().generateKeyPair();
		return loadKeyPairFromFiles();
	}

	@Bean
	public JwtEncoder jwtEncoder(KeyPair rsaKeyPair) {
		return NimbusJwtEncoder
				.withKeyPair(
						(RSAPublicKey) rsaKeyPair.getPublic(),
						(RSAPrivateKey) rsaKeyPair.getPrivate()
				)
				.build();
	}

	@Bean
	public JwtDecoder jwtDecoder(
			KeyPair rsaKeyPair,
			@Value("${app.jwt.issuer}") String issuer
	) {
		NimbusJwtDecoder decoder = NimbusJwtDecoder
				.withPublicKey((RSAPublicKey) rsaKeyPair.getPublic())
				.build();

		decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));
		return decoder;
	}

	private KeyPair loadKeyPairFromFiles() {
		try {
			Path privateKeyPath = Path.of("keys", "private.pem");
			Path publicKeyPath = Path.of("keys", "public.pem");

			String privatePem = Files.readString(privateKeyPath)
					.replace("-----BEGIN PRIVATE KEY-----", "")
					.replace("-----END PRIVATE KEY-----", "")
					.replaceAll("\\s", "");

			String publicPem = Files.readString(publicKeyPath)
					.replace("-----BEGIN PUBLIC KEY-----", "")
					.replace("-----END PUBLIC KEY-----", "")
					.replaceAll("\\s", "");

			byte[] privateBytes = Base64.getDecoder().decode(privatePem);
			byte[] publicBytes = Base64.getDecoder().decode(publicPem);

			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			RSAPrivateKey privateKey = (RSAPrivateKey) keyFactory.generatePrivate(
					new PKCS8EncodedKeySpec(privateBytes)
			);
			RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(
					new X509EncodedKeySpec(publicBytes)
			);

			return new KeyPair(publicKey, privateKey);
		} catch (IOException e) {
			throw new IllegalStateException("RSA anahtar dosyaları okunamadı", e);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			throw new IllegalStateException("RSA anahtarları yüklenemedi", e);
		}
	}
}
