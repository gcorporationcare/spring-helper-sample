package com.gcorp.security;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.gcorp.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@NoArgsConstructor
public class JwtTokenProvider {

	private static final String SECRET = "OA_SUPER_SECRET_KEY95s]%@";

	@Autowired
	private UserDetailsService userDetailsService;

	public String generateToken(User user) {
		log.info("Generating token for user {}", user);
		LocalDateTime expiresOn = LocalDateTime.now().plusMinutes(10);
		return Jwts.builder().setId(user.getPassword()).setSubject(user.getUsername())
				.setExpiration(Date.from(expiresOn.atZone(ZoneId.systemDefault()).toInstant())).setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
	}

	public User getUserFromToken(@NonNull String token, HttpServletResponse response, String language) {
		Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
		String username = claims.getSubject();
		User user = (User) userDetailsService.loadUserByUsername(username);
		response.setHeader(JwtAuthorizationFilter.AUTHORIZATION_TOKEN_HEADER, token);
		return user;
	}
}
