package com.viescloud.eco.Authenticator.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Jwt implements Serializable {
    private String jwt;
}
