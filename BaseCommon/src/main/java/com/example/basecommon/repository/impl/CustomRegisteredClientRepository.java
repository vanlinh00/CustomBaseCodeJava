package com.example.basecommon.repository.impl;

import com.example.basecommon.entity.OauthClientDetails;
import com.example.basecommon.repository.OauthClientDetailsRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;


import java.util.stream.Collectors;



@Repository
@RequiredArgsConstructor
public class CustomRegisteredClientRepository implements RegisteredClientRepository {


    private final OauthClientDetailsRepository repository;


    @Override
    public void save(RegisteredClient registeredClient) {
        OauthClientDetails entity = new OauthClientDetails();
        entity.setClientId(registeredClient.getClientId());
        entity.setClientSecret(registeredClient.getClientSecret());


        entity.setScope(registeredClient.getScopes());


        String grantTypes = registeredClient.getAuthorizationGrantTypes()
                .stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.joining(","));
        entity.setAuthorizedGrantTypes(grantTypes);


        String redirectUris = String.join(",", registeredClient.getRedirectUris());
        entity.setWebServerRedirectUri(redirectUris);


        repository.save(entity);
    }


    @Override
    public RegisteredClient findById(String id) {
        return repository.findById(id)
                .map(this::toRegisteredClient)
                .orElse(null);
    }


    @Override
    public RegisteredClient findByClientId(String clientId) {
        return repository.findByClientId(clientId)
                .map(this::toRegisteredClient)
                .orElse(null);
    }


    private RegisteredClient toRegisteredClient(OauthClientDetails entity) {
        RegisteredClient.Builder builder = RegisteredClient.withId(entity.getClientId())
                .clientId(entity.getClientId())
                .clientSecret(entity.getClientSecret())
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC);


        if (entity.getAuthorizedGrantTypes() != null) {
            for (String grant : entity.getAuthorizedGrantTypes().split(",")) {
                String trimmed = grant.trim();
                if (!trimmed.isEmpty()) {
                    builder.authorizationGrantType(new AuthorizationGrantType(trimmed));
                }
            }
        }


        if (entity.getScope() != null) {
            for (String scope : entity.getScope().split(",")) {
                String trimmed = scope.trim();
                if (!trimmed.isEmpty()) {
                    builder.scope(trimmed);
                }
            }
        }


        if (entity.getWebServerRedirectUri() != null) {
            for (String uri : entity.getWebServerRedirectUri().split(",")) {
                String trimmed = uri.trim();
                if (!trimmed.isEmpty()) {
                    builder.redirectUri(trimmed);
                }
            }
        }


        return builder.build();
    }
}

