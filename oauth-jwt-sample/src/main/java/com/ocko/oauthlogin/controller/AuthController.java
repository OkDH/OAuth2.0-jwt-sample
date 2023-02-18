package com.ocko.oauthlogin.controller;

import java.io.IOException;

import javax.naming.CommunicationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocko.oauthlogin.config.auth.OAuthService;
import com.ocko.oauthlogin.constant.SocialType;
import com.ocko.oauthlogin.dto.SocialOAuthResponse;

import lombok.RequiredArgsConstructor;

/**
 * @author ok
 *
 */
@RestController
@RequiredArgsConstructor
public class AuthController {
	
	private final OAuthService oAuthService;

	/**
	 * 유저 소셜 로그인으로 리다이렉트 해주는 url
	 * @param socialTypePath
	 * @throws IOException
	 */
    @GetMapping("/login/oauth2/authorization/{socialType}") 
    public void redirectSocialLogin(@PathVariable(name="socialType") String socialTypePath) throws IOException {
         SocialType socialType = SocialType.valueOf(socialTypePath.toUpperCase());
         oAuthService.redirectSocialLogin(socialType);
    }
    
    /**
     * 소셜 로그인 이후 받아온 1회용 code를 통한 로그인 처리
     * @param socialTypePath
     * @param code
     * @return
     * @throws IOException
     * @throws CommunicationException 
     */
    @GetMapping(value = "/login/oauth2/code/{socialType}")
    public ResponseEntity<SocialOAuthResponse> oAuthLogin (
            @PathVariable(name = "socialType") String socialTypePath,
            @RequestParam(name = "code") String code) throws IOException, CommunicationException {
    	SocialType socialType = SocialType.valueOf(socialTypePath.toUpperCase());
        SocialOAuthResponse res = oAuthService.oAuthLogin(socialType, code);
        return new ResponseEntity<SocialOAuthResponse>(res, HttpStatus.OK);
    }
    
}
