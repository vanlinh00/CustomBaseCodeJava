package com.example.basecommon.security;

import com.example.basecommon.entity.OauthAccessToken;
import com.example.basecommon.entity.OauthRefreshToken;
import com.example.basecommon.repository.OauthAccessTokenRepository;
import com.example.basecommon.repository.OauthRefreshTokenRepository;
import com.example.basecommon.util.TokenUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;



@Service
@Transactional
@RequiredArgsConstructor
public class LegacyTokenStoreService {


    private final OauthAccessTokenRepository oauthAccessTokenRepository;
    private final OauthRefreshTokenRepository oauthRefreshTokenRepository;


    @Transactional(readOnly = true)
    public OauthAccessToken readAccessToken(String tokenValue) {
        String tokenId = TokenUtils.extractTokenId(tokenValue);
        return oauthAccessTokenRepository.findFirstByTokenId(tokenId);
    }


    @Transactional(readOnly = true)
    public OauthRefreshToken readRefreshToken(String tokenValue) {
        String tokenId = TokenUtils.extractTokenId(tokenValue);
        return oauthRefreshTokenRepository.findFirstByTokenId(tokenId);
    }


    public void saveAccessToken(String clientId,
                                String accessToken,
                                Authentication authentication,
                                String refreshToken) {


        byte[] accessTokenBytes = TokenUtils.serialize(accessToken);
        byte[] authenticationBytes = TokenUtils.serialize(authentication);


        String tokenId = DigestUtils.md5DigestAsHex(accessTokenBytes);


        String refreshTokenId = null;
        byte[] refreshTokenBytes = null;
        if (StringUtils.isNotBlank(refreshToken)) {
            refreshTokenBytes = TokenUtils.serialize(refreshToken);
            refreshTokenId = DigestUtils.md5DigestAsHex(refreshTokenBytes);
        }


        // legacy-style authentication_id
        String authenticationId = DigestUtils.md5DigestAsHex(
                (clientId + ":" + authentication.getName()).getBytes(StandardCharsets.UTF_8)
        );


        OauthAccessToken oauthAccessToken = new OauthAccessToken();
        oauthAccessToken.setTokenId(tokenId);
        oauthAccessToken.setToken(accessTokenBytes);
        oauthAccessToken.setAuthenticationId(authenticationId);
        oauthAccessToken.setUserName(authentication.getName());
        oauthAccessToken.setClientId(clientId);
        oauthAccessToken.setAuthentication(authenticationBytes);
        oauthAccessToken.setRefreshToken(refreshTokenId);
        oauthAccessTokenRepository.save(oauthAccessToken);


        if (refreshTokenBytes != null) {
            OauthRefreshToken oauthRefreshToken = new OauthRefreshToken();
            oauthRefreshToken.setTokenId(refreshTokenId);
            oauthRefreshToken.setToken(refreshTokenBytes);
            oauthRefreshToken.setAuthentication(authenticationBytes);
            oauthRefreshTokenRepository.save(oauthRefreshToken);
        }
    }


    public void removeAccessToken(OauthAccessToken oauthAccessToken) {
        oauthAccessTokenRepository.delete(oauthAccessToken);
    }


    public void removeRefreshToken(String refreshTokenId) {
        OauthRefreshToken oauthRefreshToken = oauthRefreshTokenRepository.findFirstByTokenId(refreshTokenId);
        if (Objects.nonNull(oauthRefreshToken)) {
            oauthRefreshTokenRepository.delete(oauthRefreshToken);
        }
    }


    public Authentication readAuthentication(String tokenValue) {
        String tokenId = TokenUtils.extractTokenId(tokenValue);
        return Optional.ofNullable(
                        oauthAccessTokenRepository.findFirstByTokenId(tokenId))
                .map(OauthAccessToken::getAuthentication)
                .map(TokenUtils::deserializeAuthentication)
                .orElse(null);
    }


    public Map<String, Object> extractAdditionalInfo(Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();


        if (Objects.nonNull(authentication)) {
            Object details = authentication.getDetails();
            if (details instanceof Map<?, ?> map) {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() != null) {
                        additionalInfo.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                }
            }
        }


        return additionalInfo;
    }


    public void revokeByRefreshToken(String refreshTokenValue) {
        byte[] refreshTokenBytes = TokenUtils.serialize(refreshTokenValue);
        String refreshTokenId = DigestUtils.md5DigestAsHex(refreshTokenBytes);


        oauthAccessTokenRepository.deleteByRefreshToken(refreshTokenId);
        oauthRefreshTokenRepository.deleteByTokenId(refreshTokenId);
    }
}

