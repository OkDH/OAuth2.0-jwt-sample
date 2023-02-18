/**
 * 
 */
package com.ocko.oauthlogin.config.auth;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import javax.naming.CommunicationException;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocko.oauthlogin.config.auth.jwt.JwtTokenProvider;
import com.ocko.oauthlogin.config.auth.user.CustomUserDetails;
import com.ocko.oauthlogin.config.auth.user.OAuth2UserInfo;
import com.ocko.oauthlogin.config.auth.user.OAuth2UserInfoFactory;
import com.ocko.oauthlogin.constant.SocialType;
import com.ocko.oauthlogin.constant.UserRole;
import com.ocko.oauthlogin.constant.UserStatus;
import com.ocko.oauthlogin.dto.OAuthRequest;
import com.ocko.oauthlogin.dto.SocialOAuthResponse;
import com.ocko.oauthlogin.entity.User;
import com.ocko.oauthlogin.exception.OAuthProcessingException;
import com.ocko.oauthlogin.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author ok
 * OAuth 인증 Service
 */
@Service
@RequiredArgsConstructor
public class OAuthService {
	
	private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

	private final OAuthRequestFactory oAuthRequestFactory;
    private final HttpServletResponse response;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    /** 
     * 각 소셜 로그인을 요청하면 소셜로그인 페이지로 리다이렉트
     * @param socialType
     * @throws IOException
     */
    public void redirectSocialLogin(SocialType socialType) throws IOException {
        String authURL = oAuthRequestFactory.getAuthUrl(socialType);
        response.sendRedirect(authURL);
    }

	/**
	 * 로그인 처리
	 * 각 소셜에서 받아온 1회성 코드를 이용하여 로그인 처리
	 * @param socialType
	 * @param code
	 * @return
	 * @throws JsonProcessingException 
	 * @throws JsonMappingException 
	 * @throws CommunicationException 
	 */
	public SocialOAuthResponse oAuthLogin(SocialType socialType, String code) throws JsonMappingException, JsonProcessingException, CommunicationException {
		
		// 받아온 일회성 코드로 요쳥해 액세스 토큰이 담긴 응답객체를 받아옴
		AccessToken accessToken = getAccessToken(socialType, code);
		
		// AccessToken으로 사용자 정보를 받아옴
		OAuth2UserInfo oAuth2UserInfo = getUserInfo(socialType, accessToken.getAccess_token());
		
		if (oAuth2UserInfo.getId().isEmpty()) {
            throw new OAuthProcessingException("Social ID not found from OAuth2 provider");
        }
        
		// userInfo 조회
        Example<User> example = Example.of(
        		User.builder()
        		.socialId(oAuth2UserInfo.getId())
        		.socialType(socialType)
        		.build()
        		);
        Optional<User> userOptional = userRepository.findOne(example);
        User user;

        if (userOptional.isPresent()) {	// 이미 가입된 경우
            user = userOptional.get();
            if (socialType != user.getSocialType()) {
                throw new OAuthProcessingException("Wrong Match Auth Provider");
            }

        } else { // 가입되지 않은 경우
             user = createUser(oAuth2UserInfo, socialType);
        }
        
        // Authentication 생성
        OAuth2User OAuth2User = CustomUserDetails.create(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(OAuth2User, "", OAuth2User.getAuthorities());
        
        // JWT 생성
        String jwtAccessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication, response);
        
		return new SocialOAuthResponse(user.getUserId(), jwtAccessToken, refreshToken); 
    }
	
	/**
	 * 받아온 일회성 코드로 요쳥해 AccessToken이 담긴 응답객체를 받아옴
	 * @param socialType
	 * @param code
	 * @return
	 * @throws CommunicationException
	 */
	private AccessToken getAccessToken(SocialType socialType, String code) throws CommunicationException {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

	    OAuthRequest oAuthRequest = oAuthRequestFactory.getRequestAccessToken(socialType, code);
	    HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(oAuthRequest.getMap(), httpHeaders);

	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.postForEntity(oAuthRequest.getUrl(), request, String.class);
	    
	    try {
	        if (response.getStatusCode() == HttpStatus.OK) {
	        	ObjectMapper objectMapper = new ObjectMapper();
	            return objectMapper.readValue(response.getBody(), AccessToken.class);
	        }
	    } catch (Exception e) {
	        throw new CommunicationException();
	    }
	    throw new CommunicationException();
	}
	
	/**
	 * AccessToken으로 사용자 정보를 받아옴
	 * @param socialType
	 * @param accessToken
	 * @return
	 * @throws CommunicationException
	 */
	private OAuth2UserInfo getUserInfo(SocialType socialType, String accessToken) throws CommunicationException {
	    HttpHeaders httpHeaders = new HttpHeaders();
	    httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
	    httpHeaders.set("Authorization", "Bearer " + accessToken);
	    
	    String requestUserInfoUrl = oAuthRequestFactory.getUserinfoRequestUrl(socialType);
	    
	    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
	    
	    RestTemplate restTemplate = new RestTemplate();
	    ResponseEntity<String> response = restTemplate.postForEntity(requestUserInfoUrl, request, String.class);

	    try {
	        if (response.getStatusCode() == HttpStatus.OK) {
	        	ObjectMapper objectMapper = new ObjectMapper();
	        	
	        	log.info("body : {}", response.getBody());
	        	
	        	Map<String, Object> attributes = objectMapper.readValue(response.getBody(), new TypeReference<Map<String,Object>>(){}); 
	            return OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, attributes);
	        }
	    } catch (Exception e) {
	        throw new CommunicationException();
	    }
	    throw new CommunicationException();
	}
	
	/**
	 * 신규 유저 생성
	 * @param userInfo
	 * @param socialType
	 * @return
	 */
	private User createUser(OAuth2UserInfo userInfo, SocialType socialType) {
    	User user = User.builder()
                .socialId(userInfo.getId())
                .userImage(userInfo.getImageUrl())
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .socialType(socialType)
                .registeredTime(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return userRepository.save(user);
    }
}
