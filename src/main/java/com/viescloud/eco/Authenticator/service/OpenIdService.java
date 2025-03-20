package com.viescloud.eco.Authenticator.service;

import java.net.URI;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.viescloud.eco.Authenticator.model.openId.OpenIdAccessTokenResponse;
import com.viescloud.eco.Authenticator.model.openId.OpenIdRequest;
import com.viescloud.eco.Authenticator.model.openId.OpenIdUserInfoResponse;
import com.viescloud.eco.viesspringutils.exception.HttpResponseThrowers;
import com.viescloud.eco.viesspringutils.util.WebCall;

@Service
public class OpenIdService {

    @Value("${openId.client.id}")
    private String openIdClientId;

    @Value("${openId.client.secret}")
    private String openIdClientSecret;

    @Value("${openId.uri.token}")
    private String openIdTokenUri;

    @Value("${openId.uri.userInfo}")
    private String openIdUserInfoUri;

    @Value("${spring.profiles.active}")
    private String env = "?";

    @Autowired
    private RestTemplate restTemplate;
    
    public OpenIdUserInfoResponse getUserInfo(OpenIdRequest openIdRequest) {
        var token = this.getAccessToken(openIdRequest);

        URI uri = URI.create(openIdUserInfoUri);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", String.format("%s %s", token.getToken_type(), token.getAccess_token()));
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);

        var response = WebCall.of(restTemplate, OpenIdUserInfoResponse.class)
                              .logRequest(!this.env.equalsIgnoreCase("prod"))
                              .exchange(uri, HttpMethod.GET, entity)
                              .getOptionalResponseBody();

        if(!response.isPresent())
            HttpResponseThrowers.throwServerError("server encounter unknown error when trying to get user info with openId");

        return response.get();
    }

    public OpenIdAccessTokenResponse getAccessToken(OpenIdRequest openIdRequest) {
        URI uri = URI.create(openIdTokenUri);
        String encodingAuthentication = Base64.getEncoder().encodeToString(String.format("%s:%s", openIdClientId, openIdClientSecret).getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", String.format("Basic %s", encodingAuthentication));

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "authorization_code");
        map.add("code", openIdRequest.getCode());
        map.add("redirect_uri", openIdRequest.getRedirectUri());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(map, headers);

        var response = WebCall.of(restTemplate, OpenIdAccessTokenResponse.class)
                              .logRequest(!env.equalsIgnoreCase("prod"))
                              .exchange(uri, HttpMethod.POST, entity)
                              .getOptionalResponseBody();

        if(!response.isPresent())
            HttpResponseThrowers.throwServerError("server encounter unknown error when trying to authenticate with openId");

        return response.get();
    }
}
