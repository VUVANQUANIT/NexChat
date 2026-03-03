package com.Spring_chat.Spring_chat.security;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import io.jsonwebtoken.security.Keys;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;

    private SecretKey getSingingKey(){
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration());
        return Jwts.builder().subject(username).issuedAt(now).expiration(expiryDate).signWith(getSingingKey()).compact();
    }
    public String valitdatAccessTokenWithUsername(String token){
        try {
            return Jwts.parser()
                    .verifyWith(getSingingKey())
                    .build()
                    .parseClaimsJws(token).getPayload()
                    .getSubject();
        }catch (Exception e) {
            log.debug(e.getMessage());
            return null;
        }
    }
    public long getAccessTokenExpirationSeconds() {
        return jwtConfig.getExpiration() / 1000;
    }

    public long getRefreshTokenExpirationMillis() {
        return jwtConfig.getRefreshExpiration();
    }
}
