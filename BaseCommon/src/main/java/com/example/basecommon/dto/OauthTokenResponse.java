package com.example.basecommon.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;




@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class OauthTokenResponse {


    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType = "bearer";
    @JsonProperty("expires_in")
    private Long expiresIn;
    @JsonProperty("refresh_token")
    private String refreshToken;
    private String scope = "read write trust";
    private String jti;


}




