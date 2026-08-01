package com.example.basecommon.constants;


public class ApiPath {


    public static final String OAUTH_TOKEN = "/oauth/get_token";


    public static final String OAUTH_LOGOUT = "/oauth/logout";
    public static final String OAUTH_LOGOUT_BY_LIST_USER = "/oauth/logout-by-list-user";


    public interface ADMIN {
        String OAUTH_CHECK_TOKEN = "/oauth/check_token";
        String OAUTH_LOGOUT = "/oauth/logout";
        String OAUTH_ME = "/oauth/me";

    }

}

