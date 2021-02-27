package com.gcorp.entity.enumeration;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
	/**
	 * An administrator can do everything, especially manage Work flows
	 */
	ADMINISTRATOR,
	/**
	 * A patient is a simple user
	 */
	SIMPLE,
	/**
	 * An anonymous can only access login pages and not secured pages
	 */
	ANONYMOUS;

	@Override
	public String getAuthority() {
		return toString();
	}

}
