package com.example.baseoauth.security;

import com.example.basecommon.constants.DateConst;
import com.example.basecommon.constants.OAuthConstants;
import com.example.basecommon.dto.OAuthLoginRequest;
import com.example.basecommon.dto.OauthTokenResponse;
import com.example.basecommon.entity.Admin;
import com.example.basecommon.entity.DeviceToken;
import com.example.basecommon.entity.OauthClientDetails;
import com.example.basecommon.entity.User;
import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.enums.VerifyStatusUser;
import com.example.basecommon.exception.BasicException;
import com.example.basecommon.heplers.TokenCredentialHelper;
import com.example.basecommon.repository.AdminRepository;
import com.example.basecommon.repository.DeviceTokenRepository;
import com.example.basecommon.repository.OauthClientDetailsRepository;
import com.example.basecommon.repository.UserRepository;
import com.example.basecommon.security.LegacyTokenStoreService;
import com.example.basecommon.security.PrincipalAdminDetail;
import com.example.basecommon.util.DateUtil;
import com.example.basecommon.util.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;




@Service
@Transactional
@RequiredArgsConstructor
public class AdminOauthService {


    private final HttpServletRequest httpServletRequest;
    private final DeviceTokenRepository deviceTokenRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final OauthClientDetailsRepository oauthClientDetailsRepository;
    private final PasswordEncoder passwordEncoder;
    private final LegacyTokenStoreService legacyTokenStoreService;
    private final JwtEncoder jwtEncoder;


    @Autowired
    @Qualifier("jwtRefreshDecoder")
    private JwtDecoder jwtRefreshDecoder;


    public OauthTokenResponse getToken(OAuthLoginRequest request) throws IOException {
        String grantType = request.getGrant_type();


        if ("refresh_token".equalsIgnoreCase(grantType)) {
            return refreshToken(request);
        }


        return passwordToken(request);
    }


    private void validateAccount(String clientId, OAuthLoginRequest request) {
        boolean isDeleted;
        String username = request.getUsername();
        String password;


        if (username != null) {
            if (OAuthConstants.ADMIN_CLIENT.equals(clientId)) {
                Admin admin = (username.contains("@"))
                        ? adminRepository.findByEmail(username).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT))
                        : adminRepository.findByLoginId(username).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
                isDeleted = Boolean.TRUE.equals(admin.getIsDeleted());
                password = admin.getPasswordHash();
            } else if (OAuthConstants.MOBILE_CLIENT.equals(clientId)) {
                User user;
                if (username.contains("@")) {
                    List<User> userList = userRepository.findAllByEmailAndIsDeletedFalse(username);
                    user = userList.stream()
                            .filter(u -> u.getRegVerifyStatus() != VerifyStatusUser.DELETED.getValue())
                            .findFirst()
                            .orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
                } else {
                    user = userRepository.findByLoginId(username)
                            .orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
                }
                isDeleted = Boolean.TRUE.equals(user.getIsDeleted())
                        || (user.getRegVerifyStatus() != null && user.getRegVerifyStatus() == VerifyStatusUser.DELETED.getValue());
                password = user.getPasswordHash();
            } else {
                throw new BasicException(MessageCode.ACCOUNT_INCORRECT);
            }


            if (!passwordEncoder.matches(request.getPassword(), password)) {
                throw new BasicException(MessageCode.ACCOUNT_INCORRECT);
            }


            if (isDeleted) {
                throw new BasicException(MessageCode.ACCOUNT_DISABLED);
            }
        }
    }


    private PrincipalAdminDetail loadPrincipal(String clientId, String input) {
        if (OAuthConstants.ADMIN_CLIENT.equals(clientId)) {
            Admin admin = (input.contains("@"))
                    ? adminRepository.findByEmail(input).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT))
                    : adminRepository.findByLoginId(input).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
            return new PrincipalAdminDetail(admin, null, admin.getLoginId(), "", "00000000", "99999999");
        } else {
            User user = (input.contains("@"))
                    ? userRepository.findAllByEmailAndIsDeletedFalse(input).stream()
                    .filter(u -> u.getRegVerifyStatus() != VerifyStatusUser.DELETED.getValue())
                    .findFirst()
                    .orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT))
                    : userRepository.findByLoginId(input).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));


            String dateNow = DateUtil.dateToStringTimeZone(new Date(), DateConst.YYYYMMDD);


            boolean isTooEarly = Objects.nonNull(user.getAccessStartDate()) && dateNow.compareTo(user.getAccessStartDate()) < 0;
            boolean isTooLate = Objects.nonNull(user.getAccessEndDate()) && dateNow.compareTo(user.getAccessEndDate()) > 0;
            if (isTooEarly) {
                throw new BasicException(MessageCode.ACCESS_NOT_STARTED);
            }
            if (isTooLate) {
                throw new BasicException(MessageCode.FORBIDDEN);
            }
            return new PrincipalAdminDetail(null, user, user.getLoginId(), user.getFullName(),
                    user.getAccessStartDate(), user.getAccessEndDate());
        }
    }


    private void createDeviceToken(String accessToken, String jti, PrincipalAdminDetail principal, String deviceTokenStr) {
        if (principal == null || StringUtils.isBlank(jti) || StringUtils.isBlank(deviceTokenStr)) {
            return;
        }


        Long adminId = principal.getAdmin() != null ? principal.getAdmin().getId() : null;
        Long userId = principal.getUser() != null ? principal.getUser().getId() : null;
        Long principalId = adminId != null ? adminId : userId;


        if (principalId == null) {
            return;
        }


        DeviceToken deviceToken = deviceTokenRepository.findFirstByDeviceToken(deviceTokenStr);
        if (deviceToken == null) {
            deviceToken = new DeviceToken();
        }


        deviceToken.setJti(jti);
        deviceToken.setUserId(principalId);
        deviceToken.setAccessToken(accessToken);
        deviceToken.setDeviceToken(deviceTokenStr);
        deviceToken.setIsAdmin(adminId != null);
        deviceTokenRepository.save(deviceToken);
    }


    private OauthClientDetails getOauthClient(String clientId, String clientSecret) {
        Optional<OauthClientDetails> opClient = oauthClientDetailsRepository.findByClientId(clientId);
        if (opClient.isEmpty()
                || !passwordEncoder.matches(clientSecret, opClient.get().getClientSecret())) {
            throw new BadCredentialsException("You do not have permission to access!");
        }


        return opClient.get();
    }


    private OauthTokenResponse passwordToken(OAuthLoginRequest request) {
        String basicCredentials = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String[] credentials = TokenCredentialHelper.parseCredentials(basicCredentials);
        String clientId = credentials[0];
        String clientSecret = credentials[1];


        OauthClientDetails oauthClient = this.getOauthClient(clientId, clientSecret);


        this.validateAccount(clientId, request);


        PrincipalAdminDetail principal = this.loadPrincipal(clientId, request.getUsername());


        Long expiresIn = oauthClient.getAccessTokenValiditySeconds() > 0
                ? Long.valueOf(oauthClient.getAccessTokenValiditySeconds())
                : null;


        JwtClaimsSet accessJwt = TokenUtils.createAccessTokenClaims(clientId, oauthClient, principal);
        String tokenValue = jwtEncoder.encode(JwtEncoderParameters.from(accessJwt)).getTokenValue();


        JwtClaimsSet refreshJwt = TokenUtils.createRefreshTokenClaims(
                clientId, oauthClient, principal, accessJwt.getId()
        );
        String refreshTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(refreshJwt)).getTokenValue();


        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );


        legacyTokenStoreService.saveAccessToken(clientId, tokenValue, authentication, refreshTokenValue);
        createDeviceToken(tokenValue, accessJwt.getId(), principal, request.getDeviceToken());


        return new OauthTokenResponse()
                .setAccessToken(tokenValue)
                .setRefreshToken(refreshTokenValue)
                .setExpiresIn(expiresIn)
                .setScope(oauthClient.getScope())
                .setJti(accessJwt.getId());
    }


    private OauthTokenResponse refreshToken(OAuthLoginRequest request) throws IOException {
        String basicCredentials = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String[] credentials = TokenCredentialHelper.parseCredentials(basicCredentials);
        String clientId = credentials[0];
        String clientSecret = credentials[1];


        OauthClientDetails oauthClient = this.getOauthClient(clientId, clientSecret);


        String refreshTokenValue = request.getRefresh_token();
        if (StringUtils.isBlank(refreshTokenValue)) {
            throw new InvalidBearerTokenException("Missing refresh_token");
        }


        Jwt refreshJwt = jwtRefreshDecoder.decode(refreshTokenValue);


        // validate refresh token claims
        validateRefreshToken(refreshJwt, clientId);


        String accessTokenId = refreshJwt.getClaimAsString("ati");
        if (StringUtils.isBlank(accessTokenId)) {
            throw new InvalidBearerTokenException("Missing ati claim");
        }


        PrincipalAdminDetail principal = this.loadPrincipal(clientId, refreshJwt.getSubject());


        Long expiresIn = oauthClient.getAccessTokenValiditySeconds() > 0
                ? Long.valueOf(oauthClient.getAccessTokenValiditySeconds())
                : null;


        // create new access token
        JwtClaimsSet newAccessJwt = TokenUtils.createAccessTokenClaims(clientId, oauthClient, principal);
        String newAccessTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(newAccessJwt)).getTokenValue();


        // legacy-like: create new refresh token too
        JwtClaimsSet newRefreshJwt = TokenUtils.createRefreshTokenClaims(
                clientId, oauthClient, principal, newAccessJwt.getId()
        );
        String newRefreshTokenValue = jwtEncoder.encode(JwtEncoderParameters.from(newRefreshJwt)).getTokenValue();


        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal, null, principal.getAuthorities()
        );


        // revoke old refresh token if needed
        legacyTokenStoreService.revokeByRefreshToken(refreshTokenValue);


        // save new tokens
        legacyTokenStoreService.saveAccessToken(clientId, newAccessTokenValue, authentication, newRefreshTokenValue);


        return new OauthTokenResponse()
                .setAccessToken(newAccessTokenValue)
                .setRefreshToken(newRefreshTokenValue)
                .setExpiresIn(expiresIn)
                .setScope(oauthClient.getScope())
                .setJti(newAccessJwt.getId());
    }


    private void validateRefreshToken(Jwt refreshJwt, String clientId) {
        if (!"refresh".equals(refreshJwt.getClaimAsString("token_type"))) {
            throw new InvalidBearerTokenException("Not a refresh token");
        }


        String tokenClientId = refreshJwt.getClaimAsString("client_id");
        if (!clientId.equals(tokenClientId)) {
            throw new InvalidBearerTokenException("Refresh token client mismatch");
        }


        if (Objects.isNull(legacyTokenStoreService.readRefreshToken(refreshJwt.getTokenValue()))) {
            throw new InvalidBearerTokenException("Refresh token revoked");
        }
    }
}

