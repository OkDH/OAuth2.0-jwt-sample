/**
 * 
 */
package com.ocko.oauthlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author ok
 * 클라이언트로 보낼 accessToken, refreshToken등이 담긴 객체
 */
@Getter
@Setter
@AllArgsConstructor
public class SocialOAuthResponse {
	private Integer userId;
    private String accessToken;
    private String refreshToken;
}
