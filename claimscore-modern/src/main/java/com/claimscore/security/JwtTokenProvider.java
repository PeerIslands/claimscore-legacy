package com.claimscore.security;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates and validates JWT tokens for the ClaimsCore authentication flow.
 *
 * <p>Tokens are signed with HMAC-SHA256 using a configurable secret key. User roles are stored as
 * a {@code roles} claim. No hard-coded credentials are used — the secret is injected via {@link
 * Value}.
 */
@Component
public class JwtTokenProvider {

    /** Claim name used to store the list of user roles inside the JWT. */
    static final String ROLES_CLAIM = "roles";

    private final NimbusJwtEncoder encoder;
    private final JwtDecoder decoder;
    private final long expirySeconds;

    /**
     * Constructs a {@code JwtTokenProvider} from injected configuration.
     *
     * @param secret Base64-encoded HMAC-SHA256 secret key (minimum 256 bits)
     * @param expirySeconds token lifetime in seconds
     * @param decoder the shared {@link JwtDecoder} bean provided by {@link SecurityConfig}
     */
    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiry-seconds:3600}") long expirySeconds,
            JwtDecoder decoder) {
        SecretKey key = decodeSecret(secret);
        this.encoder = NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
        this.decoder = decoder;
        this.expirySeconds = expirySeconds;
    }

    /**
     * Package-private constructor for unit tests — allows injecting a custom secret and expiry
     * without a Spring context.
     *
     * @param secret Base64-encoded HMAC-SHA256 secret key
     * @param expirySeconds token lifetime in seconds
     */
    JwtTokenProvider(String secret, long expirySeconds) {
        SecretKey key = decodeSecret(secret);
        this.encoder = NimbusJwtEncoder.withSecretKey(key).algorithm(MacAlgorithm.HS256).build();
        this.decoder = buildDecoder(key);
        this.expirySeconds = expirySeconds;
    }

    /**
     * Creates a signed JWT for the given user.
     *
     * @param username the authenticated user's username (becomes the JWT {@code sub} claim)
     * @param roles the roles to embed in the {@code roles} claim
     * @return the compact serialised JWT string
     */
    public String createToken(String username, List<UserRole> roles) {
        Instant now = Instant.now();
        Instant expiry = now.plusSeconds(expirySeconds);

        List<String> roleNames = roles.stream().map(UserRole::name).collect(Collectors.toList());

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        JwtClaimsSet claims =
                JwtClaimsSet.builder()
                        .subject(username)
                        .issuedAt(now)
                        .expiresAt(expiry)
                        .claim(ROLES_CLAIM, roleNames)
                        .build();

        return encoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
    }

    /**
     * Validates a JWT string — checks signature and expiry.
     *
     * @param token the compact serialised JWT
     * @return {@code true} if the token is valid and not expired; {@code false} otherwise
     */
    public boolean isValid(String token) {
        try {
            Jwt jwt = decoder.decode(token);
            Instant expiry = jwt.getExpiresAt();
            return expiry != null && Instant.now().isBefore(expiry);
        } catch (JwtException ex) {
            return false;
        }
    }

    /**
     * Extracts the username (JWT {@code sub} claim) from a token.
     *
     * @param token the compact serialised JWT
     * @return the subject claim value
     * @throws JwtException if the token cannot be decoded
     */
    public String extractUsername(String token) {
        return decoder.decode(token).getSubject();
    }

    // ---- private helpers ----

    private static SecretKey decodeSecret(String base64Secret) {
        byte[] keyBytes = Base64.getDecoder().decode(base64Secret);
        return new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    private static JwtDecoder buildDecoder(SecretKey key) {
        return NimbusJwtDecoder.withSecretKey(key).macAlgorithm(MacAlgorithm.HS256).build();
    }
}
