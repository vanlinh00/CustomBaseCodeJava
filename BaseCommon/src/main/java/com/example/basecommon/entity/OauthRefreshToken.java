package com.example.basecommon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import java.io.Serializable;


@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "OauthRefreshToken")
@Table(name = "oauth_refresh_token")
@SuppressWarnings("unused")
public class OauthRefreshToken implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "token_id", nullable = false)
    private String tokenId;


    @Lob
    @Column(name = "token", columnDefinition = "BLOB")
    private byte[] token;


    @Column(name = "authentication", columnDefinition = "BLOB")
    private byte[] authentication;


}



