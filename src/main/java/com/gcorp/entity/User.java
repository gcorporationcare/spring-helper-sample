package com.gcorp.entity;

import java.util.Arrays;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gcorp.common.Utils;
import com.gcorp.constraint.InvalidWhen;
import com.gcorp.constraint.ValidationStep;
import com.gcorp.entity.enumeration.UserRole;
import com.gcorp.exception.ValidationException;
import com.gcorp.i18n.I18nMessage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * LomBok annotations for avoiding vanilla code
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

/**
 * Persistence annotations
 */
@Entity
@Table(name = "user")
public class User extends BaseEntity implements UserDetails {

	protected static final String NAME_COLUMN = "name";
	protected static final String PASSWORD_COLUMN = "password";
	protected static final String EMAIL_COLUMN = "email";
	protected static final String ROLE_COLUMN = "role";

	private static final long serialVersionUID = 1L;

	// -------------------------------------------------
	@Id
	@Column(name = User.EMAIL_COLUMN, nullable = false)
	@Email(message = I18nMessage.DataError.EMAIL_VALUE_EXPECTED, groups = { ValidationStep.Simple.class })
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String email;
	// -------------------------------------------------
	@JsonIgnore
	@Column(name = User.PASSWORD_COLUMN, nullable = false)
	private String password;
	// -------------------------------------------------
	@Column(name = User.NAME_COLUMN, nullable = false)
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String name;
	// -------------------------------------------------
	@Enumerated(EnumType.STRING)
	@Column(name = User.ROLE_COLUMN, nullable = false)
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private UserRole role;
	// -------------------------------------------------
	@Transient
	private String token;

	public static User anonymous(final String language) {
		return new User("no-email@mail.com", UserRole.ANONYMOUS.toString(), null, UserRole.ANONYMOUS, null);
	}

	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Arrays.asList(role);
	}

	@Override
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@Override
	@JsonIgnore
	public String getUsername() {
		return email;
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return !UserRole.ANONYMOUS.equals(role);
	}

	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return !UserRole.ANONYMOUS.equals(role);
	}

	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return !UserRole.ANONYMOUS.equals(role);
	}

	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return !UserRole.ANONYMOUS.equals(role);
	}

	@Override
	public void format() {
		if (UserRole.ANONYMOUS.equals(role)) {
			throw new ValidationException(I18nMessage.DataError.FORBIDDEN_VALUE_GIVEN, Utils.generateViolations(
					InvalidWhen.class, I18nMessage.DataError.FORBIDDEN_VALUE_GIVEN, this, ROLE_COLUMN, role));
		}
	}
}
