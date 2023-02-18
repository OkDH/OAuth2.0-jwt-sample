package com.ocko.oauthlogin.config.auth.user;

import java.util.Map;

public class NaverOAuth2UserInfo extends OAuth2UserInfo {

	public NaverOAuth2UserInfo(Map<String, Object> attributes) {
		super(attributes);
	}

	@Override
	public String getId() {
		return (String) getResponse().get("id");
	}

	@Override
	public String getName() {
		return (String) getResponse().get("name");
	}

	@Override
	public String getEmail() {
		return (String) getResponse().get("email");
	}

	@Override
	public String getImageUrl() {
		return (String) getResponse().get("profile_image");
	}
	
	private Map<String, Object> getResponse(){
		return (Map<String, Object>) attributes.get("response");
	}

}
