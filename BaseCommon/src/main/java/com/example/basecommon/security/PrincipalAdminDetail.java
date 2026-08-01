package com.example.basecommon.security;

import com.example.basecommon.entity.Admin;
import com.example.basecommon.entity.User;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.util.Collection;
import java.util.Collections;
import java.util.Objects;



public class PrincipalAdminDetail implements UserDetails {
    private static final Logger LOG = LoggerFactory.getLogger(PrincipalAdminDetail.class);


    @Getter
    private final Admin admin;


    @Getter
    private final User user;


    private String username;


    @Getter
    private String name = "";


    private String accessStartDate;


    private String accessEndDate;


    PrincipalAdminDetail(Admin admin, User user) {
        this.admin = admin;
        this.user = user;
    }


    public PrincipalAdminDetail(Admin admin, User user, String username, String name, String accessStartDate, String accessEndDate) {
        this.admin = admin;
        this.user = user;
        this.username = username;
        this.name = StringUtils.defaultString(name);
        this.accessStartDate = accessStartDate;
        this.accessEndDate = accessEndDate;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (Objects.nonNull(this.admin)) {
            return Collections.singleton(new SimpleGrantedAuthority(this.admin.getRole().name()));
        }


        return Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() {
        if (Objects.nonNull(this.admin)) {
            return this.admin.getPasswordHash();
        }


        return this.user.getPasswordHash();
    }


    @Override
    public String getUsername() {
        return this.username;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }
}

