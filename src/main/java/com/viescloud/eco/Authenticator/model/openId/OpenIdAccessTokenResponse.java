package com.viescloud.eco.Authenticator.model.openId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdAccessTokenResponse {
    private String access_token;
    private String token_type;
    private int expires_in;
    private String id_token;
}
