package com.example.basecommon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.StringUtils;


import java.io.Serializable;
import java.util.Set;



@Getter
@Setter
@JsonSerialize
@ToString
@Entity(name = "OauthClientDetails")
@Table(name = "oauth_client_details")
@SuppressWarnings("serial")
public class OauthClientDetails extends RegisteredClient implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;


    @Column(name = "client_secret", nullable = false, unique = true, columnDefinition = "TEXT")
    private String clientSecret;


    @Column(name = "resource_ids", columnDefinition = "TEXT")
    private String resourceIds;


    @Column(name = "scope", columnDefinition = "TEXT")
    private String scope;


    @Column(name = "authorized_grant_types", nullable = false, columnDefinition = "TEXT")
    private String authorizedGrantTypes;


    @Column(name = "web_server_redirect_uri", columnDefinition = "TEXT")
    private String webServerRedirectUri;


    @Column(name = "authorities", columnDefinition = "TEXT")
    private String authorities;


    @Column(name = "access_token_validity", nullable = false)
    private Integer accessTokenValiditySeconds;


    @Column(name = "refresh_token_validity", nullable = false)
    private Integer refreshTokenValiditySeconds;


    @Column(name = "autoapprove", nullable = false, columnDefinition = "TEXT")
    private String autoApproveScope;


    @Column(name = "additional_information", nullable = true, columnDefinition = "TEXT")
    private String additionalInformation;


    @Override
    public String getClientId() {
        return this.clientId;
    }


    @Override
    public String getClientSecret() {
        return this.clientSecret;
    }


    public void setScope(Set<String> scope) {
        this.scope = StringUtils.collectionToCommaDelimitedString(scope);
    }


}

