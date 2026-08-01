package com.example.basecommon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serializable;


@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "OauthAccessToken")
@Table(name = "oauth_access_token")
@SuppressWarnings("unused")
public class OauthAccessToken implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "authentication_id", nullable = false)
    private String authenticationId;


    @Lob
    @Column(name = "token", columnDefinition = "BLOB")
    private byte[] token;


    @Column(name = "token_id", columnDefinition = "TEXT")
    private String tokenId;


    @Column(name = "user_name", columnDefinition = "TEXT")
    private String userName;


    @Column(name = "client_id", columnDefinition = "TEXT")
    private String clientId;


    @Column(name = "authentication", columnDefinition = "BLOB")
    private byte[] authentication;


    @Column(name = "refresh_token", columnDefinition = "TEXT")
    private String refreshToken;


    @Column(name = "device_token", columnDefinition = "TEXT")
    private String deviceToken;


}



