/**
 * 
 */
package com.ocko.oauthlogin.config.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ok
 * 일회성 코드를 다시 보내 받아온 액세스 토큰을 포함한 정보를 담을 클래스
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessToken {
	private String access_token;
    private String refresh_token;
    private long expires_in;
    private long refresh_token_expires_in;
    private String token_type;
    private String scope;
}
