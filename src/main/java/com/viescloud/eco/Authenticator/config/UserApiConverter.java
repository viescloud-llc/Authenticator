package com.viescloud.eco.Authenticator.config;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.viescloud.eco.Authenticator.model.UserApi;
import com.viescloud.llc.viesspringutils.util.Json;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserApiConverter implements AttributeConverter<List<UserApi>, String> {

    @Override
    public String convertToDatabaseColumn(List<UserApi> attribute) {
        return Json.toJson(attribute);
    }

    @Override
    public List<UserApi> convertToEntityAttribute(String dbData) {
        return Json.fromJson(dbData, new TypeReference<List<UserApi>>() { });
    }
    
}
