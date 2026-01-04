package com.hms.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Shared minimal JWT helper used by Auth Service (generate) and API Gateway (validate).
 * HS256 with a single shared secret (demo/learning setup).
 */
public class JwtTokenService {

    private final SecretKey key;
    private final String issuer;

    public JwtTokenService(String secret, String issuer) {
        // Accept raw string secret (min 32 bytes recommended) OR base64 string.
        byte[] bytes;
        if (looksLikeBase64(secret)) {
            try {
                bytes = Decoders.BASE64.decode(secret);
            } catch (Exception ex) {
                bytes = secret.getBytes(StandardCharsets.UTF_8);
            }
        } else {
            bytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(bytes);
        this.issuer = issuer;
    }

    public String generateToken(long userId, String username, List<String> roles, long ttlSeconds) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        return Jwts.builder()
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim(JwtClaims.CLAIM_USER_ID, userId)
                .claim(JwtClaims.CLAIM_USERNAME, username)
                .claim(JwtClaims.CLAIM_ROLES, roles)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims validateAndGetClaims(String token) throws JwtException {
        Jws<Claims> jws = Jwts.parserBuilder()
                .requireIssuer(issuer)
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
        return jws.getBody();
    }

    @SuppressWarnings("unchecked")
    public static List<String> rolesFromClaims(Claims claims) {
        Object v = claims.get(JwtClaims.CLAIM_ROLES);
        if (v instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    private static boolean looksLikeBase64(String s) {
        if (s == null) return false;
        String t = s.trim();
        // quick heuristic
        return t.length() >= 40 && t.matches("^[A-Za-z0-9+/=]+$");
    }
}
