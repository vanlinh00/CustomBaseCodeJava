package com.example.basecommon.security;

import com.example.basecommon.constants.BaseConst;
import com.example.basecommon.constants.DateConst;
import com.example.basecommon.entity.Admin;
import com.example.basecommon.entity.User;
import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.enums.VerifyStatusUser;
import com.example.basecommon.exception.BasicException;
import com.example.basecommon.heplers.TokenCredentialHelper;
import com.example.basecommon.repository.AdminRepository;
import com.example.basecommon.repository.UserRepository;
import com.example.basecommon.util.DateUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * Class for handling Customer. authorized_grant_types != client_credentials
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService, AuthenticationUserDetailsService<PreAuthenticatedAuthenticationToken> {
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);


    private final HttpServletRequest httpServletRequest;
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    // private final OtpRepository otpRepository;
//    private final LogoutService logoutService;
    private final PasswordEncoder passwordEncoder;
    private RestTemplate restTemplate = new RestTemplate();
    // private final UserLifestyleDailyRepository lifestyleDailyRepository;

//
//    @Value("${tomo.oauth.logout}")
//    private String logoutUrl;
//
//
//    @Value("${tomo.oauth.logoutByListUser}")
//    private String logoutByListUserUrl;
//
//    private final SesEmailSenderService sesEmailSenderService;
//    private final ForgotLoginIdEmailProperties forgotLoginIdEmailProperties;
//    private final OtpEmailProperties otpEmailProperties;
//    private final OtpCommonService otpCommonService;
//    private final HealthGuidanceRepository healthGuidanceRepository;
//    private final InsuranceProfileRepository insuranceProfileRepository;
//    private final GroupRepository groupRepository;
//    private final RegisterSuccessEmailProperties registerSuccessEmailProperties;
//    private final InsuredCardEmailProperties insuredCardEmailProperties;
//    private final S3Service s3Service;


//   private final UserDraftRepository userDraftRepository;


    @Transactional(readOnly = true)
    @Override
    public PrincipalAdminDetail loadUserByUsername(String input) {
        String credentials = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        String[] clientIdAndSecret = TokenCredentialHelper.parseCredentials(credentials);
        String clientId = clientIdAndSecret[0];


        logger.debug("*** loadUserByUsername {} with client_id = {}", input, clientId);


        // Check user loginId
        if (StringUtils.isAllBlank(input)) {
            throw new BasicException(MessageCode.ACCOUNT_INCORRECT);
        }


        if (BaseConst.ADMIN_CLIENT.equals(clientId)) {
            Admin admin = (input.contains("@"))
                    ? adminRepository.findByEmail(input).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT))
                    : adminRepository.findByLoginId(input).orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));


            return new PrincipalAdminDetail(admin, null, admin.getLoginId(), "", "00000000", "99999999");
        } else if (BaseConst.MOBILE_CLIENT.equals(clientId)) {
            User user;
            if (input.contains("@")) {
                List<User> userList = userRepository.findAllByEmailAndIsDeletedFalse(input);
                user = userList.stream()
                        .filter(u -> u.getRegVerifyStatus() != VerifyStatusUser.DELETED.getValue())
                        .findFirst()
                        .orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
            } else {
                user = userRepository.findByLoginIdAndIsDeletedFalse(input)
                        .orElseThrow(() -> new BasicException(MessageCode.ACCOUNT_INCORRECT));
            }
            if (Objects.isNull(user)) {
                throw new BasicException(MessageCode.ACCOUNT_INCORRECT);
            }
            if (user.getRegVerifyStatus() != null && user.getRegVerifyStatus() == VerifyStatusUser.DELETED.getValue()) {
                throw new BasicException(MessageCode.ACCOUNT_DISABLED);
            }


            String dateNow = DateUtil.dateToStringTimeZone(new Date(), DateConst.YYYYMMDD);
            if (dateNow.compareTo(user.getAccessStartDate()) < 0) {
                throw new BasicException(MessageCode.ACCESS_NOT_STARTED);
            }
            if (user.getAccessEndDate() != null && dateNow.compareTo(user.getAccessEndDate()) > 0) {
                throw new BasicException(MessageCode.FORBIDDEN);
            }
            return new PrincipalAdminDetail(null, user, user.getLoginId(), user.getFullName(), user.getAccessStartDate(), user.getAccessEndDate());
        }


        throw new BasicException(MessageCode.ACCOUNT_INCORRECT);
    }




    @Override
    public UserDetails loadUserDetails(PreAuthenticatedAuthenticationToken preAuthenticatedAuthenticationToken) throws UsernameNotFoundException {
        if (preAuthenticatedAuthenticationToken.getPrincipal() instanceof UsernamePasswordAuthenticationToken) {
            UsernamePasswordAuthenticationToken authenticationToken = (UsernamePasswordAuthenticationToken) preAuthenticatedAuthenticationToken.getPrincipal();
            return (PrincipalAdminDetail) authenticationToken.getPrincipal();
        } else if (preAuthenticatedAuthenticationToken.getPrincipal() instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken authenticationToken = (PreAuthenticatedAuthenticationToken) preAuthenticatedAuthenticationToken.getPrincipal();
            return (PrincipalAdminDetail) authenticationToken.getPrincipal();
        } else if (preAuthenticatedAuthenticationToken.getPrincipal() instanceof PrincipalAdminDetail) {
            return (PrincipalAdminDetail) preAuthenticatedAuthenticationToken.getPrincipal();
        }


        return null;
    }



}

