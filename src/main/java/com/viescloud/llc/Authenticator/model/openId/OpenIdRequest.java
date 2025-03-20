package com.viescloud.llc.Authenticator.model.openId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdRequest {
    private String code;
    private String state;
    private String redirectUri;
}
