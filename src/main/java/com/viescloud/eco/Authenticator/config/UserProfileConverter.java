package com.viescloud.eco.Authenticator.config;

import com.viescloud.eco.Authenticator.model.UserProfile;
import com.viescloud.llc.viesspringutils.util.Json;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserProfileConverter implements AttributeConverter<UserProfile, String> {

    @Override
    public String convertToDatabaseColumn(UserProfile attribute) {
        return Json.toJson(attribute);
    }

    @Override
    public UserProfile convertToEntityAttribute(String dbData) {
        return Json.fromJson(dbData, UserProfile.class);
    }
    
}
