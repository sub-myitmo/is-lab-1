//package ru.is1.config.utils;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//
//public class JwtUtil {
//    private static final String SECRET_KEY = "c3VwZXItc2VjcmV0LWtleS1oZXJlLW1ha2UtaXQtdmVyeS1sb25nLWFuZC1zZWN1cmUtYXQtbGVhc3QtMjU2LWJpdHM=";
//    private static final long EXPIRATION_TIME = 86400000; // 24 hours
//
//    private static Key getSigningKey() {
//        byte[] apiKeySecretBytes = Base64.getDecoder().decode(SECRET_KEY);
//        return Keys.hmacShaKeyFor(apiKeySecretBytes);
//    }
//
//    public static String generateToken(String username) {
//        return Jwts.builder()
//                .setSubject(username)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(getSigningKey())
//                .compact();
//    }
//
//    public static String validateToken(String token) {
//        try {
//            Claims claims = Jwts.parserBuilder()
//                    .setSigningKey(getSigningKey())
//                    .build()
//                    .parseClaimsJws(token)
//                    .getBody();
//            return claims.getSubject();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//}
