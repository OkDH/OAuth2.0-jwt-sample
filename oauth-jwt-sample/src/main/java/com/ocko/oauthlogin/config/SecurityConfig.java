package com.ocko.oauthlogin.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ocko.oauthlogin.config.auth.jwt.JwtAccessDeniedHandler;
import com.ocko.oauthlogin.config.auth.jwt.JwtAuthenticationEntryPoint;
import com.ocko.oauthlogin.config.auth.jwt.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

/**
 * spring security 설정<br>
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Override
	public void configure(HttpSecurity http) throws Exception {
    	http
		.csrf().disable() // CSRF off : rest api이므로 csrf 보안이 필요없으므로 disable처리.
		.httpBasic().disable() // Basic Auth off : rest api 이므로 기본설정 사용안함. 기본설정은 비인증시 로그인폼 화면으로 리다이렉트 된다.
		
		// jwt token으로 인증하므로 세션을 사용하지 않기 때문에 세션 설정을 STATELESS로 설정
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		
		.and()
			.authorizeRequests() // request에 대한 권한 처리
			.antMatchers(
					"/private",
					"/private/**"
					).hasAnyRole("USER")
			.antMatchers("/**").permitAll()
		
		.and()
			.exceptionHandling() // JWT를 다룰 때 생길 excepion을 처리할 class를 지정
			.authenticationEntryPoint(jwtAuthenticationEntryPoint) // (401) 인증 과정에서 생길 exception을 처리
			.accessDeniedHandler(jwtAccessDeniedHandler) // (403) 인가 과정에서 생길 exception을 처리
			
			// 모든 request에서 JWT를 검사할 filter를 추가
			// UsernamePasswordAuthenticationFilter에서 클라이언트가 요청한 리소스의 접근권한이 없을 때 막는 역할을 하기 때문에 
			// 이 필터 전에 jwtAuthenticationFilter를 실행
		.and()
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); 
    }
}
