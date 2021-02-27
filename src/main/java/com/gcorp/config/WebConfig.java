package com.gcorp.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig extends ApiConfig {
	/**
	 * We can override multiple parameters: <br/>
	 * RequestId header's name <br/>
	 * Search/field filters, language & page/size parameters name
	 */
	@Override
	protected String requestIdHeaderName() {
		return "Shop-Request-Id";
	}
}
