package com.example.taskflow.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long expiration;


    private SecretKey getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // generate token
    public String generateToken(Authentication authentication) {
//
//        Map<String, Object> claims = new HashMap<>();
//
//        // Add roles to claims
//        List<String> roles = userDetails.getAuthorities().stream()
//                .map(GrantedAuthority::getAuthority)
//                .map(auth -> auth.replace("ROLE_", ""))
//                .toList();
//        claims.put("roles", roles);
//
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .issuer("https://myapp.com")
//                .signWith(getSigningKey())
//                .compact();

        UserDetails userPrinciple = (UserDetails) authentication.getPrincipal();
        assert userPrinciple != null;
        return Jwts.builder().
                subject(userPrinciple.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiration))
                .signWith(getSignInKey())
                .compact();

    }

    // extract token
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
//        return Jwts.parser().verifyWith(getSignInKey()).build()
//                .parseSignedClaims(token).getPayload().getSubject();
    }

    // validate token
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            throw new JwtException(e.getMessage());
        }
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);

    }
}