package com.example.basecommon.dto;

import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;


import jakarta.validation.constraints.NotEmpty;




@Data
public class OAuthLoginRequest {


    private String username;


    private String password;


    private String deviceToken;


    private String grant_type = "password";


    private boolean remember = true;


    private String refresh_token;


    public String getDeviceToken() {
        this.deviceToken = StringUtils.defaultIfEmpty(deviceToken, RandomStringUtils.randomAlphabetic(56));
        return deviceToken;
    }
}

