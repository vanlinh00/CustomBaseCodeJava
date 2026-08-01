package com.example.basecommon.util;

import com.example.basecommon.enums.MessageCode;
import com.example.basecommon.exception.UTF8Control;
import com.example.basecommon.heplers.TokenCredentialHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;


import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


@Slf4j
public class BaseUtils {


    private static final String SYSTEM = "system";
    private static final String LOCALHOST_IPV4 = "127.0.0.1";
    private static final String LOCALHOST_IPV6 = "0:0:0:0:0:0:0:1";


    // private static List<Locale> LOCALES = Arrays.asList(new Locale("en"), new Locale("ja"));




    public static String getErrorMessageDefault(MessageCode messageCode) {
        try {
            Locale locale = Locale.getDefault();
            ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages", locale, new UTF8Control());


            String message = resourceBundle.getString(messageCode.getCode());
            return message;
        } catch (Exception ex) {
            return null;
        }
    }


    public static String getErrorMessageLanguage(HttpServletRequest req, MessageCode messageCode) {
        return getErrorMessageLanguage(req, messageCode.getCode());
    }


    public static String getErrorMessageLanguage(HttpServletRequest req, String messageCode) {
        try {
            Locale locale = Locale.JAPAN;
            ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n/messages", locale, new UTF8Control());
            return resourceBundle.getString(messageCode);
        } catch (Exception e) {
            log.error("Can not found message with code {}", messageCode);
            return null;
        }
    }


    public static String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }


        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }


        if (StringUtils.isEmpty(ipAddress) || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (LOCALHOST_IPV4.equals(ipAddress) || LOCALHOST_IPV6.equals(ipAddress)) {
                try {
                    InetAddress inetAddress = InetAddress.getLocalHost();
                    ipAddress = inetAddress.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }


        if (!StringUtils.isEmpty(ipAddress)
                && ipAddress.length() > 15
                && ipAddress.indexOf(",") > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }


        return ipAddress;
    }


    public static String getClientId(HttpServletRequest httpServletRequest) {
        String credentials = StringUtils.defaultString(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));


        String[] clientIdAndSecret = TokenCredentialHelper.parseCredentials(credentials);
        String clientId = clientIdAndSecret[0];
        return clientId;
    }


}

