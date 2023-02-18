package com.ocko.oauthlogin.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.ocko.oauthlogin.constant.SocialType;
import com.ocko.oauthlogin.constant.UserRole;
import com.ocko.oauthlogin.constant.UserStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * The persistent class for the user_info database table.
 * 
 */
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="user")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="user_id")
	private Integer userId;

	@Column(name="registered_time")
	private Timestamp registeredTime;
	
	@Column(name="social_id")
	private String socialId;

	@Column(name="social_type")
	@Enumerated(EnumType.STRING)
	private SocialType socialType;

	@Column(name="user_nickname")
	private String userNickname;

	@Column(name="user_status")
	@Enumerated(EnumType.STRING)
	private UserStatus userStatus;
	
	@Column(name="user_role")
	@Enumerated(EnumType.STRING)
	private UserRole userRole;
	
	@Column(name="user_image")
	private String userImage;
	
	@Column(name="refresh_token")
	private String refreshToken;

}