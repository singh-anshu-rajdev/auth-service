package com.smartBankElite.authserver.ServiceImpl;

import com.smartBankElite.authserver.DTO.CacheDTO;
import com.smartBankElite.authserver.Model.User;
import com.smartBankElite.authserver.Repositories.UserRepository;
import com.smartBankElite.authserver.Service.JwtService;
import com.smartBankElite.authserver.Utils.SmartBankEliteConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private Long jwtExpiration;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, claims -> claims.get(SmartBankEliteConstants.USERNAME.getValue(), String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    @Override
    public Long getExpirationTime() {
        return jwtExpiration;
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        User user = userRepository.findActiveUserByEmailOrUsername(userDetails.getUsername())
                .orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put(SmartBankEliteConstants.USER_ID.getValue(), user.getId());
        claims.put(SmartBankEliteConstants.NAME.getValue(), user.getFullName());
        claims.put(SmartBankEliteConstants.USERNAME.getValue(), user.getUsername());
        claims.put(SmartBankEliteConstants.EMAIL_ID.getValue(), user.getEmailId());
        claims.put(SmartBankEliteConstants.CREATED_AT.getValue(), user.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return generateToken(claims, userDetails);
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {

        return Jwts.builder().setClaims(extraClaims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public CacheDTO getCacheDTOFromToken(HttpServletRequest httpServletRequest) {
        Claims claims = extractAllClaims(httpServletRequest.getHeader(SmartBankEliteConstants.AUTHORIZATION.getValue()).substring(7));
        CacheDTO cacheDTO = new CacheDTO();
        cacheDTO.setUserId(claims.get(SmartBankEliteConstants.USER_ID.getValue(), Integer.class));
        cacheDTO.setUserName(claims.get(SmartBankEliteConstants.USERNAME.getValue(), String.class));
        cacheDTO.setEmailId(claims.get(SmartBankEliteConstants.EMAIL_ID.getValue(), String.class));
        cacheDTO.setName(claims.get(SmartBankEliteConstants.NAME.getValue(), String.class));
        cacheDTO.setCreatedAt(claims.get(SmartBankEliteConstants.CREATED_AT.getValue(), Long.class));
        return cacheDTO;
    }
}
