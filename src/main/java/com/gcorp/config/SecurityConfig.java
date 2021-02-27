package com.gcorp.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.LocaleResolver;

import com.gcorp.common.RequestIdGenerator;
import com.gcorp.security.JwtAuthorizationFilter;
import com.gcorp.security.JwtTokenProvider;
import com.gcorp.security.UserDetailsServiceImpl;

/**
 * Security configuration.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	LocaleResolver localeResolver;
	@Autowired
	JwtTokenProvider jwtTokenProvider;
	@Autowired
	RequestIdGenerator requestIdGenerator;

	@Override
	public void configure(WebSecurity web) {
		web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/**",
				"/swagger-ui.html", "/webjars/**", "/h2-console");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		JwtAuthorizationFilter filter = new JwtAuthorizationFilter(authenticationManager());
		filter.setJwtTokenProvider(jwtTokenProvider);
		filter.setLocaleResolver(localeResolver);
		http.headers().frameOptions().sameOrigin();
		http.cors().configurationSource(corsConfigurationSource()).and().csrf().disable().authorizeRequests()
				.anyRequest().authenticated().and().addFilter(filter)
				// this disables session creation on Spring Security
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService()).passwordEncoder(bCryptPasswordEncoder());
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.addExposedHeader(JwtAuthorizationFilter.AUTHORIZATION_TOKEN_HEADER);
		configuration.addExposedHeader(JwtAuthorizationFilter.EXPIRATION_TIME_HEADER);
		configuration.addExposedHeader(requestIdGenerator.getHeaderName());
		configuration.addExposedHeader(JwtAuthorizationFilter.ALLOW_HEADERS_HEADER);
		configuration.addExposedHeader(JwtAuthorizationFilter.ALLOW_ORIGIN_HEADER);
		configuration.addAllowedHeader("*");
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowCredentials(true);
		configuration.addAllowedOrigin("*");
		configuration.setMaxAge(3600L);
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Bean
	@Override
	public UserDetailsServiceImpl userDetailsService() {
		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
