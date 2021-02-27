package com.gcorp.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.gcorp.i18n.I18nMessage;
import com.gcorp.security.JwtAuthorizationFilter;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger configurations.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

	/**
	 * Create api doc scanner.
	 *
	 * @return {@link Docket}
	 */
	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.basePackage("com.gcorp"))
				.paths(PathSelectors.any()).build().globalOperationParameters(commonParameters()).apiInfo(getApiInfo())
				.securitySchemes(Collections.singletonList(apiKey()));
	}

	private ApiKey apiKey() {
		return new ApiKey(JwtAuthorizationFilter.AUTHORIZATION_TOKEN_HEADER, "Authorization", "header");
	}

	private ApiInfo getApiInfo() {
		return new ApiInfoBuilder().description("Here's the documentation for the Spring-Helper-Sample API")
				.title("Spring Helper Sample API").version("v1.0.0").build();
	}

	private List<Parameter> commonParameters() {
		List<Parameter> parameters = new ArrayList<Parameter>();
		parameters.add(new ParameterBuilder().name(I18nMessage.LANGUAGE_PARAMETER)
				.description(I18nMessage.LANGUAGE_PARAMETER_DESCRIPTION)
				.modelRef(new ModelRef(I18nMessage.STRING_DATA_TYPE)).parameterType(I18nMessage.QUERY_PARAM_TYPE)
				.required(false).build());
		return parameters;
	}
}
