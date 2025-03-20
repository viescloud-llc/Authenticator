package com.viescloud.eco.Authenticator.model;

import java.io.Serializable;

import com.viescloud.llc.viesspringutils.config.jpa.BooleanConverter;
import com.viescloud.llc.viesspringutils.config.jpa.DateTimeConverter;
import com.viescloud.llc.viesspringutils.util.DateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class UserApi implements Serializable {

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String apiKey;

    @Column(length = 10)
    @Convert(converter = BooleanConverter.class)
    private Boolean expirable = false;

    @Column(length = 10)
    @Convert(converter = BooleanConverter.class)
    private Boolean enable = true;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = DateTimeConverter.class)
    private DateTime expireTime;
}
