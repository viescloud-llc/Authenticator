package com.viescloud.eco.Authenticator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.RestController;
import com.viescloud.llc.viesspringutils.ViesApplication;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class AuthenticatorApplication extends ViesApplication {
	public static void main(String[] args) {
		SpringApplication.run(AuthenticatorApplication.class, args);
	}

	@Override
	public String getApplicationName() {
		return "Authentication";
	}
}
