package com.example.basecommon.security;


import com.example.basecommon.constants.DateConst;
import com.example.basecommon.entity.Admin;
import com.example.basecommon.entity.User;
import com.example.basecommon.repository.AdminRepository;
import com.example.basecommon.repository.UserRepository;

import com.example.basecommon.util.DateUtil;
import com.example.basecommon.util.NumberUtil;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;



@Component
public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {


    private static final String USER_ID = "user_id";
    private static final String ADMIN_ID = "admin_id";
    private static final String USER_NAME = "user_name";
    private static final String NAME = "name";


    private final AdminRepository adminRepository;
    private final UserRepository userRepository;


    public CustomJwtAuthenticationConverter(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        Map<String, Object> claims = jwt.getClaims();


        Long adminId = NumberUtil.toLong(claims.get(ADMIN_ID));
        Long userId = NumberUtil.toLong(claims.get(USER_ID));


        PrincipalAdminDetail principalAdminDetail = null;


        if (Objects.nonNull(adminId)) {
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new BadCredentialsException("You do not have permission to access!"));
            principalAdminDetail = new PrincipalAdminDetail(admin, null, admin.getLoginId(), "", "00000000", "99999999");
        } else if (Objects.nonNull(userId)) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadCredentialsException("You do not have permission to access!"));


            String dateNow = DateUtil.dateToStringTimeZone(new Date(), DateConst.YYYYMMDD);


            boolean isTooEarly = Objects.nonNull(user.getAccessStartDate()) && dateNow.compareTo(user.getAccessStartDate()) < 0;
            boolean isTooLate = Objects.nonNull(user.getAccessEndDate()) && dateNow.compareTo(user.getAccessEndDate()) > 0;
            if (isTooEarly || isTooLate) {
                throw new BadCredentialsException("You do not have permission to access!");
            }


            principalAdminDetail = new PrincipalAdminDetail(null, user, user.getLoginId(), user.getFullName(),
                    user.getAccessStartDate(), user.getAccessEndDate());
        }


        Collection<GrantedAuthority> authorities = new ArrayList<>(jwt.getClaimAsStringList("authorities")
                .stream()
                .map(SimpleGrantedAuthority::new)
                .toList());


        return new UsernamePasswordAuthenticationToken(principalAdminDetail, jwt, authorities);
    }
}

