package com.example.project.security.jwt;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

	private final JwtEncoder jwtEncoder;
	private final String issuer;
	private final long expirationSeconds;

	public TokenService(
			JwtEncoder jwtEncoder,
			@Value("${app.jwt.issuer}") String issuer,
			@Value("${app.jwt.expiration-seconds}") long expirationSeconds
	) {
		this.jwtEncoder = jwtEncoder;
		this.issuer = issuer;
		this.expirationSeconds = expirationSeconds;
	}

	public String createToken(String username) {
		Instant now = Instant.now();

		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer(issuer)
				.subject(username)
				.issuedAt(now)
				.expiresAt(now.plusSeconds(expirationSeconds))
				.claim("scope", "USER")
				.build();

		JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();

		return jwtEncoder.encode(
				JwtEncoderParameters.from(header, claims)
		).getTokenValue();
	}
}
