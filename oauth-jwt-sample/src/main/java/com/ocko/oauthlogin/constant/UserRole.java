/**
 * 
 */
package com.ocko.oauthlogin.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author ok
 *
 */
@Getter
@RequiredArgsConstructor
public enum UserRole {
    ADMIN("ROLE_ADMIN", "ADMIN"),
    USER("ROLE_USER", "USER");

    private final String role;
    private final String name;
}
