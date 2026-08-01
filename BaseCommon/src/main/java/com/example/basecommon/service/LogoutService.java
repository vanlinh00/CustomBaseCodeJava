package com.example.basecommon.service;

import com.example.basecommon.dto.LogoutUsersRequest;
import com.example.basecommon.entity.DeviceToken;
import com.example.basecommon.entity.OauthAccessToken;
import com.example.basecommon.entity.User;
import com.example.basecommon.repository.DeviceTokenRepository;
import com.example.basecommon.security.LegacyTokenStoreService;
import com.example.basecommon.security.PrincipalAdminDetail;
import com.example.basecommon.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LogoutService {


    private final LegacyTokenStoreService tokenStore;
    private final DeviceTokenRepository deviceTokenRepository;


    @Transactional
    public void logout(HttpServletRequest request) {
        Optional<String> tokenOpt = TokenUtils.extractBearerToken(request);
        if (tokenOpt.isEmpty()) {
            return;
        }


        String token = tokenOpt.get();
        OauthAccessToken accessToken = tokenStore.readAccessToken(token);
        if (accessToken == null) {
            return;
        }


        deleteDeviceToken(accessToken);


        tokenStore.removeAccessToken(accessToken);
        if (accessToken.getRefreshToken() != null) {
            tokenStore.removeRefreshToken(accessToken.getRefreshToken());
        }
    }


    private void deleteDeviceToken(OauthAccessToken accessToken) {
        if (accessToken == null) {
            return;
        }


        Authentication auth = tokenStore.readAuthentication(accessToken.getTokenId());


        Long userId = Optional.ofNullable(auth)
                .map(Authentication::getPrincipal)
                .filter(PrincipalAdminDetail.class::isInstance)
                .map(p -> (PrincipalAdminDetail) p)
                .map(PrincipalAdminDetail::getUser)
                .map(User::getId)
                .orElse(0L);


        String jti = StringUtils.defaultString((String) tokenStore.extractAdditionalInfo(auth).get("jti"));
        deviceTokenRepository.deleteByJtiAndUserId(jti, userId);
    }


    @Transactional
    public void logoutByUserList(LogoutUsersRequest request) {
        Boolean isAdmin = request.getIsAdmin();


        for (Long userId : request.getUserIds()) {
            log.info("Logout userId = {}, isAdmin = {}", userId, isAdmin);


            List<DeviceToken> deviceTokens = deviceTokenRepository.findAllByUserIdAndIsAdmin(userId, isAdmin);


            for (DeviceToken deviceToken : deviceTokens) {
                String tokenValue = deviceToken.getAccessToken();
                OauthAccessToken accessToken = tokenStore.readAccessToken(tokenValue);


                if (accessToken != null) {
                    deviceTokenRepository.delete(deviceToken);
                    tokenStore.removeAccessToken(accessToken);
                    if (accessToken.getRefreshToken() != null) {
                        tokenStore.removeRefreshToken(accessToken.getRefreshToken());
                    }
                }
            }
        }
    }
}

