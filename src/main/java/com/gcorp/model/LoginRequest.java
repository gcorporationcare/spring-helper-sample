package com.gcorp.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.gcorp.constraint.ValidationStep;
import com.gcorp.i18n.I18nMessage;

import lombok.Data;

@Data
public class LoginRequest {
	@Email(message = I18nMessage.DataError.EMAIL_VALUE_EXPECTED, groups = { ValidationStep.Simple.class })
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String email;
	// -------------------------------------------------
	@NotEmpty(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	@NotNull(message = I18nMessage.DataError.FIELD_REQUIRED, groups = { ValidationStep.Simple.class })
	private String password;
}
