package com.viescloud.eco.Authenticator.model.openId;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenIdUserInfoResponse {
    private String sub;
    private String email;
    private String email_verified;
    private String name;
    private String given_name;
    private String preferred_username;
    private String nickname;
    private List<String> groups;
}
