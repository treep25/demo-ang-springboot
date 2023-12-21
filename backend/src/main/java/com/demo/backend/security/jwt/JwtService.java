package com.demo.backend.security.jwt;

import com.demo.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {
    @Value("${jwt.secret.key}")
    private String SECRET_KEY;
    private static final String ROLES = "roles";

    private static final int ONE_DAY_MILLIS = (1000 * 60 * 60 * 24) / 12;
    private static final int ONE_DAY_PLUS_15_MINUTES_MILLIS = ONE_DAY_MILLIS + 900 * 1000;

    public String extractUsername(String token, String ipAddr) {
        log.debug("Getting expiration");
        return extractClaim(token, Claims::getSubject, ipAddr);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String ipAddr) {
        final Claims claims = extractAllClaims(token, ipAddr);
        return claimsResolver.apply(claims);
    }

    public String generateToken(User user, String ipAddr) {
        log.debug("Generating access token");
        return generateToken(new HashMap<>(), user, ipAddr);
    }

    public String generateRefreshToken(User user, String ipAddr) {
        log.debug("Generating refresh token");
        return generateRefreshToken(new HashMap<>(), user, ipAddr);
    }

    private String generateToken(
            Map<String, Object> extraClaims, User user, String ipdAddr) {
        extraClaims.put(ROLES, user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(",")));
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setId(String.valueOf(user.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY_MILLIS))
                .signWith(getSignInKey(ipdAddr), SignatureAlgorithm.HS256)
                .compact();
    }

    private String generateRefreshToken(
            Map<String, Object> extraClaims,
            User user,
            String ipAddr) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ONE_DAY_PLUS_15_MINUTES_MILLIS))
                .signWith(getSignInKey(ipAddr), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails, String ipAddr) {
        log.debug("Validation of token");
        final String username = extractUsername(token, ipAddr);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, userDetails, ipAddr);
    }

    private boolean isTokenExpired(String token, UserDetails userDetails, String ipAddr) {
        log.debug("Is token expired");
        return extractExpiration(token, ipAddr).before(new Date());
    }

    private Date extractExpiration(String token, String ipAddr) {
        log.debug("Getting token expiration");
        return extractClaim(token, Claims::getExpiration, ipAddr);
    }

    private Claims extractAllClaims(String token, String ipAddr) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(ipAddr))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String ipAddr) {
        final String SECRET_KEY_MUTATION = SECRET_KEY + SECRET_KEY.hashCode();
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY_MUTATION);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}