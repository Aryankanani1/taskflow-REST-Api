package com.example.taskflow.security.jwt;

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

@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expiration}")
    private Long expiration;


    private SecretKey getSignInKey(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    // generate token
    public String generateToken(Authentication authentication){
        UserDetails userPrinciple = (UserDetails)  authentication.getPrincipal();
        return Jwts.builder().
                subject(userPrinciple.getUsername())
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + expiration))
                .signWith(getSignInKey())
                .compact();

    }

    // extract token
    public String extractUserName(String token){
        return Jwts.parser().verifyWith(getSignInKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    // validate token
    public boolean validateToken(String token){
            try{
                Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token);
                return true;
            }catch (Exception e){
                throw new JwtException(e.getMessage());
            }
    }

}
