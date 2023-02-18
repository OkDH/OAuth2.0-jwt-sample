/**
 * 
 */
package com.ocko.oauthlogin.config.auth.user;

import java.util.Map;

import com.ocko.oauthlogin.constant.SocialType;

/**
 * @author ok
 *
 */
public class OAuth2UserInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(SocialType socialType, Map<String, Object> attributes) {
        switch (socialType) {
            case GOOGLE: return new GoogleOAuth2UserInfo(attributes);
            case NAVER: return new NaverOAuth2UserInfo(attributes);
            default: throw new IllegalArgumentException("알 수 없는 소셜 로그인 형식입니다.");
        }
    }
	
}
