package com.gcorp.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.LocaleResolver;

import com.gcorp.entity.User;
import com.google.common.base.Strings;

import lombok.Setter;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

	public static final String AUTHORIZATION_TOKEN_HEADER = "Shop-Session-Token";
	public static final String EXPIRATION_TIME_HEADER = "Shop-Expiration-Time";
	public static final String ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
	public static final String ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
	public static final String CONTENT_TYPE_HEADER = "Content-Type";

	@Setter
	LocaleResolver localeResolver;
	@Setter
	private JwtTokenProvider jwtTokenProvider;

	public JwtAuthorizationFilter(AuthenticationManager authManager) {
		super(authManager);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		String token = req.getHeader(AUTHORIZATION_TOKEN_HEADER);
		Locale locale = localeResolver.resolveLocale(req);
		String language = locale.getLanguage();
		res.addHeader(ALLOW_HEADERS_HEADER, AUTHORIZATION_TOKEN_HEADER);
		res.addHeader(ALLOW_HEADERS_HEADER, EXPIRATION_TIME_HEADER);
		res.addHeader(ALLOW_HEADERS_HEADER, ALLOW_ORIGIN_HEADER);
		res.addHeader(ALLOW_HEADERS_HEADER, CONTENT_TYPE_HEADER);

		UsernamePasswordAuthenticationToken authentication = getAuthentication(req, res, token, language);

		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		chain.doFilter(req, res);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req, HttpServletResponse res,
			String token, String language) {
		User defaultUser = User.anonymous(language);
		UsernamePasswordAuthenticationToken anonymous = new UsernamePasswordAuthenticationToken(defaultUser, null,
				defaultUser.getAuthorities());
		if (Strings.isNullOrEmpty(token)) {
			return anonymous;
		}
		// parse the token.
		User user = jwtTokenProvider.getUserFromToken(token, res, language);
		if (user != null) {
			MDC.put("user", user.getUsername());
			MDC.put("role", user.getRole().toString());
			return new UsernamePasswordAuthenticationToken(user, user.getUsername(), Arrays.asList(user.getRole()));
		}
		return anonymous;
	}
}
