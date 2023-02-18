/**
 * 
 */
package com.ocko.oauthlogin.config.auth;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import com.ocko.oauthlogin.constant.SocialType;
import com.ocko.oauthlogin.dto.OAuthRequest;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ok 
 * 각 소셜 서비스의  요청을 만드는 Factory
 */
@Component
@RequiredArgsConstructor
public class OAuthRequestFactory {

	private final GoogleOAuthInfo googleInfo;
	private final NaverOAuthInfo naverInfo;
	
	public String getAuthUrl(SocialType socialType) {
		
		switch (socialType){
        case GOOGLE: {
    		StringBuilder authUrl = new StringBuilder()
                .append(googleInfo.getAuthorizationUri())
                .append("?client_id=").append(googleInfo.getClientId())
                .append("&response_type=code")
                .append("&scope=").append(googleInfo.getAccessScope())
                .append("&redirect_uri=").append(googleInfo.getRedirectUri());
    		return authUrl.toString();
        }
        case NAVER: {
        	StringBuilder authUrl = new StringBuilder()
    			.append(naverInfo.getAuthorizationUri())
                .append("?client_id=").append(naverInfo.getClientId())
                .append("&response_type=code")
                .append("&redirect_uri=").append(naverInfo.getRedirectUri());
        	return authUrl.toString();
        }
        default: 
            throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
		}
		
	}
	
	public OAuthRequest getRequestAccessToken(SocialType socialType, String code) {
        LinkedMultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        
        switch (socialType){
        case GOOGLE: 
        	map.add("grant_type", "authorization_code");
            map.add("client_id", googleInfo.getClientId());
            map.add("client_secret", googleInfo.getClientSecret());
            map.add("redirect_uri", googleInfo.getRedirectUri());
            map.add("code", code);
            return new OAuthRequest(googleInfo.getTokenRequestUri(), map);
        case NAVER:
        	map.add("grant_type", "authorization_code");
            map.add("client_id", naverInfo.getClientId());
            map.add("client_secret", naverInfo.getClientSecret());
            map.add("code", code);
            return new OAuthRequest(naverInfo.getTokenRequestUri(), map);
        default: 
            throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }
    }
	
	public String getUserinfoRequestUrl(SocialType socialType) {
		switch (socialType){
        case GOOGLE: 
            return googleInfo.getUserinfoRequestUrl();
        case NAVER:
        	return naverInfo.getUserinfoRequestUrl();
        default: 
            throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }
	}

	@Getter
	@Component
	static class GoogleOAuthInfo {
		@Value("${oauth2.google.authorization-uri}")
		private String authorizationUri;
		@Value("${oauth2.google.client-id}")
		private String clientId;
		@Value("${oauth2.google.client-secret}")
		private String clientSecret;
		@Value("${oauth2.google.redirect-uri}")
		private String redirectUri;
		@Value("${oauth2.google.token-uri}")
		private String tokenRequestUri;
		@Value("${oauth2.google.user-info-uri}")
		private String userinfoRequestUrl;
		@Value("${oauth2.google.scope}")
		private String accessScope;
	}
	
	@Getter
	@Component
	static class NaverOAuthInfo {
		@Value("${oauth2.naver.authorization-uri}")
		private String authorizationUri;
		@Value("${oauth2.naver.client-id}")
		private String clientId;
		@Value("${oauth2.naver.client-secret}")
		private String clientSecret;
		@Value("${oauth2.naver.redirect-uri}")
		private String redirectUri;
		@Value("${oauth2.naver.token-uri}")
		private String tokenRequestUri;
		@Value("${oauth2.naver.user-info-uri}")
		private String userinfoRequestUrl;
//		@Value("${oauth2.naver.scope}")
//		private String accessScope;
	}
}
