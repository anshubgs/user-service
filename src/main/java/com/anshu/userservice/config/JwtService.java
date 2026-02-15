package com.anshu.userservice.config;

import org.springframework.stereotype.Service;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
	
	   @Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.access-token-expiration}")
	    private long expiration;
	    
	    private Key getSigningKey() {
	        return Keys.hmacShaKeyFor(secret.getBytes());
	    }
	    
	    public String generateToken(UUID userUuid, UUID houseUuid) {
	    	
			return Jwts.builder()
					.claim("userUuid",userUuid.toString())
					.claim("houseUuid", houseUuid != null ? houseUuid.toString() : null)
					.setIssuedAt(new Date())
					.setExpiration(new Date(System.currentTimeMillis() + expiration))
					.signWith(getSigningKey(), SignatureAlgorithm.HS256)
					.compact();
	    	
	    }


	public Claims  extractAllClaims(String token) {
		// TODO Auto-generated method stub
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				//.parsePlaintextJws(token)
				.getBody();
	}

}
