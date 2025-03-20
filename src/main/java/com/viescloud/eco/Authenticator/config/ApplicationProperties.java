package com.viescloud.eco.Authenticator.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Configuration
@Getter
@Setter(AccessLevel.PACKAGE)
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationProperties {
    @Value("${openId.client.id}")
    private String openIdClientId;

    @Value("${openId.client.secret}")
    private String openIdClientSecret;

    @Value("${openId.uri.token}")
    private String openIdTokenUri;

    @Value("${openId.uri.userInfo}")
    private String openIdUserInfoUri;

    @Value("${spring.profiles.active:?}")
    private String env = "?";

    @Value("${jwt.secret}")
	private String jwtSecret;
}
