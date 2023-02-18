/**
 * 
 */
package com.ocko.oauthlogin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ocko.oauthlogin.entity.User;

/**
 * @author ok
 *
 */
public interface UserRepository extends JpaRepository<User, Integer> {

	@Query("SELECT u.refreshToken FROM User u WHERE u.id=:id")
    String getRefreshTokenById(@Param("id") Integer id);
	
    @Transactional
    @Modifying
    @Query("UPDATE User u SET u.refreshToken=:token WHERE u.id=:id")
    void updateRefreshToken(@Param("id") Integer id, @Param("token") String token);
}
