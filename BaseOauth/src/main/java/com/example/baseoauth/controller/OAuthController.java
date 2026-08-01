package com.example.baseoauth.controller;

import com.example.basecommon.constants.ApiPath;
import com.example.basecommon.dto.LogoutUsersRequest;
import com.example.basecommon.dto.OAuthLoginRequest;
import com.example.basecommon.factory.ResponseFactory;
import com.example.basecommon.service.LogoutService;
import com.example.baseoauth.security.AdminOauthService;
import io.swagger.v3.oas.annotations.Operation;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


import java.io.IOException;




@RestController
public class OAuthController {


    private final AdminOauthService adminOauthService;


    private final LogoutService logoutService;




    public OAuthController(AdminOauthService adminOauthService, LogoutService logoutService) {
        this.adminOauthService = adminOauthService;
        this.logoutService = logoutService;
    }


    @Operation(summary = "Login for admin client")
    @PostMapping(value = ApiPath.OAUTH_TOKEN)
    public ResponseEntity<?> getToken(@RequestBody @Valid OAuthLoginRequest request) throws IOException {
        return ResponseFactory.success(adminOauthService.getToken(request));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping(value = ApiPath.OAUTH_LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request) {
        logoutService.logout(request);
        return ResponseFactory.success(HttpStatus.NO_CONTENT);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_USER')")
    @PostMapping(value = ApiPath.OAUTH_LOGOUT_BY_LIST_USER)
    public ResponseEntity<?> logoutByUserId(@RequestBody @Valid LogoutUsersRequest request) {
        logoutService.logoutByUserList(request);
        return ResponseFactory.success(HttpStatus.NO_CONTENT);
    }


}

