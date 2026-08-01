package com.example.basecommon.util;

import com.example.basecommon.entity.OauthClientDetails;
import com.example.basecommon.security.PrincipalAdminDetail;
import jakarta.servlet.http.HttpServletRequest;


import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.util.DigestUtils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


public class TokenUtils {


    private static final int BEARER_PREFIX_LENGTH = 7; // length of "Bearer "


    public static String extractTokenId(String tokenValue) {
        return DigestUtils.md5DigestAsHex(serialize(tokenValue));
    }


    public static byte[] serialize(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }


    public static byte[] serialize(Authentication authentication) {
        return serializeObject(authentication);
    }


    public static byte[] serializeObject(Object object) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (IOException ex) {
            throw new IllegalArgumentException("Failed to serialize object", ex);
        }
    }


    public static Authentication deserializeAuthentication(byte[] data) {
        return SerializationUtils.deserialize(data);
    }


    public static Optional<String> extractBearerToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(header) || header.length() <= BEARER_PREFIX_LENGTH) {
            return Optional.empty();
        }
        if (!header.startsWith("Bearer ")) {
            return Optional.empty();
        }
        String token = header.substring(BEARER_PREFIX_LENGTH);
        return Optional.of(token);
    }


    public static JwtClaimsSet createAccessTokenClaims(String clientId
            , OauthClientDetails oauthClient
            , PrincipalAdminDetail principal
    ) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();


        JwtClaimsSet.Builder accessTokenClaimBuilders = JwtClaimsSet.builder()
                .issuer("tomo")
                .issuedAt(now)
                .id(jti)
                .subject(principal.getUsername())
                .claim("aud", List.of(clientId))
                .claim("user_name", principal.getUsername())
                .claim("name", principal.getName())
                .claim("token_type", "access")
                .claim("client_id", clientId)
                .claim("authorities", principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()))
                .claim("scope", Arrays.asList(oauthClient.getScope().split(",")));


        if (Objects.nonNull(principal.getAdmin())) {
            accessTokenClaimBuilders.claim("admin_id", principal.getAdmin().getId());
        }


        if (Objects.nonNull(principal.getUser())) {
            accessTokenClaimBuilders.claim("user_id", principal.getUser().getId());
        }


        if (oauthClient.getAccessTokenValiditySeconds() > 0) {
            accessTokenClaimBuilders.expiresAt(now.plusSeconds(oauthClient.getAccessTokenValiditySeconds()));
        }


        return accessTokenClaimBuilders.build();
    }


    public static JwtClaimsSet createRefreshTokenClaims(String clientId
            , OauthClientDetails oauthClient
            , PrincipalAdminDetail principal
            , String ati
    ) {
        Instant now = Instant.now();
        String jti = UUID.randomUUID().toString();


        JwtClaimsSet.Builder refreshTokenClaimBuilders = JwtClaimsSet.builder()
                .issuer("tomo")
                .issuedAt(now)
                .id(jti)
                .subject(principal.getUsername())
                .claim("client_id", clientId)
                .claim("token_type", "refresh")
                .claim("ati", ati)
                .claim("scope", Arrays.asList(oauthClient.getScope().split(",")));


        if (oauthClient.getRefreshTokenValiditySeconds() > 0) {
            refreshTokenClaimBuilders.expiresAt(now.plusSeconds(oauthClient.getRefreshTokenValiditySeconds()));
        }


        return refreshTokenClaimBuilders.build();
    }


}

