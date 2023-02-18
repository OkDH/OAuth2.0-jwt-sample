/**
 * 
 */
package com.ocko.oauthlogin.dto;

import org.springframework.util.LinkedMultiValueMap;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author ok
 *
 */
@Getter
@AllArgsConstructor
public class OAuthRequest {
	private String url;
    private LinkedMultiValueMap<String, String> map;
}
