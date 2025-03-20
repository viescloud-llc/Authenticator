package com.viescloud.eco.Authenticator.model;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.viescloud.llc.viesspringutils.config.jpa.BooleanConverter;
import com.viescloud.llc.viesspringutils.config.jpa.DateTimeConverter;
import com.viescloud.llc.viesspringutils.interfaces.annotation.Decoding;
import com.viescloud.llc.viesspringutils.interfaces.annotation.Encoding;
import com.viescloud.llc.viesspringutils.model.DecodingType;
import com.viescloud.llc.viesspringutils.model.EncodingType;
import com.viescloud.llc.viesspringutils.util.DateTime;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, columnDefinition = "TEXT")
    private String sub;

    @Column(unique = true, columnDefinition = "TEXT")
    private String email;

    @Column(columnDefinition = "TEXT")
    private String name;

    @Column(unique = true, columnDefinition = "TEXT")
    private String username;

    @Column(columnDefinition = "TEXT")
    @Encoding(EncodingType.SHA256)
    @Decoding(DecodingType.NULL)
    private String password;

    @Embedded
    private UserProfile userProfile;

    @ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.DETACH}, fetch = FetchType.EAGER)
    private List<Role> userRoles;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<UserApi> userApis;

    @Column(length = 10)
    @Convert(converter = BooleanConverter.class)
    private Boolean expirable = false;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = DateTimeConverter.class)
    private DateTime expireTime;

    @Column(length = 10)
    @Convert(converter = BooleanConverter.class)
    private Boolean enable = true; 
}
