/**
 * 
 */
package com.ocko.oauthlogin.config.auth.user;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.ocko.oauthlogin.constant.SocialType;
import com.ocko.oauthlogin.constant.UserRole;
import com.ocko.oauthlogin.constant.UserStatus;
import com.ocko.oauthlogin.entity.User;
import com.ocko.oauthlogin.exception.OAuthProcessingException;
import com.ocko.oauthlogin.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author ok
 *
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	
	private final UserRepository userRepository;

	// OAuth2UserRequest에 있는 Access Token으로 유저정보 get
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        return process(oAuth2UserRequest, oAuth2User);
    }

    // 획득한 유저정보를 Java Model과 맵핑하고 프로세스 진행
    private OAuth2User process(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        SocialType socialType = SocialType.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId().toUpperCase());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(socialType, oAuth2User.getAttributes());

        if (userInfo.getEmail().isEmpty()) {
            throw new OAuthProcessingException("Email not found from OAuth2 provider");
        }
        
        Example<User> example = Example.of(
        		User.builder()
        		.socialId(userInfo.getEmail())
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
            user = createUser(userInfo, socialType);
        }
        return CustomUserDetails.create(user, oAuth2User.getAttributes());
    }

    private User createUser(OAuth2UserInfo userInfo, SocialType socialType) {
    	User user = User.builder()
                .socialId(userInfo.getEmail())
                .userImage(userInfo.getImageUrl())
                .userRole(UserRole.USER)
                .userStatus(UserStatus.ACTIVE)
                .socialType(socialType)
                .registeredTime(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return userRepository.save(user);
    }
}
