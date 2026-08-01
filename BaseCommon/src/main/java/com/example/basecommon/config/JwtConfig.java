package com.example.basecommon.config;

import com.example.basecommon.security.LegacyTokenStoreService;
import com.example.basecommon.util.KeyUtils;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;


import org.apache.commons.io.IOUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;


import java.io.IOException;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.UUID;






@Configuration
public class JwtConfig {


    @Value("${spring.config.jwt.passkey}")
    private String jwtPassKey;


    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = rsaKey();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }


    @Primary
    @Bean("jwtAccessDecoder")
    public JwtDecoder jwtAccessDecoder(LegacyTokenStoreService legacyTokenStoreService) throws IOException {
        String publicKey = IOUtils.toString(
                new ClassPathResource("certificate/pubkey.txt").getInputStream(),
                Charset.defaultCharset()
        );


        RSAPublicKey rsaPublicKey = KeyUtils.parseRsaPublicKey(publicKey);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
        decoder.setJwtValidator(jwtAccessValidator(legacyTokenStoreService));
        return decoder;
    }


    @Bean
    public OAuth2TokenValidator<Jwt> jwtAccessValidator(LegacyTokenStoreService legacyTokenStoreService) {
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefault();


        OAuth2TokenValidator<Jwt> customValidator = jwt -> {
            if (Objects.isNull(legacyTokenStoreService.readAccessToken(jwt.getTokenValue()))) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Token expired or invalid", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };


        return new DelegatingOAuth2TokenValidator<>(defaultValidator, customValidator);
    }


    @Bean("jwtRefreshDecoder")
    public JwtDecoder jwtRefreshDecoder(LegacyTokenStoreService legacyTokenStoreService) throws IOException {
        String publicKey = IOUtils.toString(
                new ClassPathResource("certificate/pubkey.txt").getInputStream(),
                Charset.defaultCharset()
        );


        RSAPublicKey rsaPublicKey = KeyUtils.parseRsaPublicKey(publicKey);
        NimbusJwtDecoder decoder = NimbusJwtDecoder.withPublicKey(rsaPublicKey).build();
        decoder.setJwtValidator(jwtRefreshValidator(legacyTokenStoreService));
        return decoder;
    }


    @Bean
    public OAuth2TokenValidator<Jwt> jwtRefreshValidator(LegacyTokenStoreService legacyTokenStoreService) {
        OAuth2TokenValidator<Jwt> defaultValidator = JwtValidators.createDefault();


        OAuth2TokenValidator<Jwt> customValidator = jwt -> {
            if (Objects.isNull(legacyTokenStoreService.readRefreshToken(jwt.getTokenValue()))) {
                return OAuth2TokenValidatorResult.failure(
                        new OAuth2Error("invalid_token", "Token expired or invalid", null)
                );
            }
            return OAuth2TokenValidatorResult.success();
        };


        return new DelegatingOAuth2TokenValidator<>(defaultValidator, customValidator);
    }


    @Bean
    public JwtEncoder jwtEncoder() {
        try {
            Resource resource = new ClassPathResource("certificate/jwt.p12");
            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, jwtPassKey.toCharArray(), "PKCS12");
            KeyPair keyPair = keyStoreKeyFactory.getKeyPair("jwt");


            RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();


            JWKSet jwkSet = new JWKSet(rsaKey);
            JWKSource<SecurityContext> jwkSource = (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
            return new NimbusJwtEncoder(jwkSource);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create JwtEncoder", e);
        }
    }


    private RSAKey rsaKey() {
        try {
            KeyPair keyPair = keyPair();
            return new RSAKey.Builder((RSAPublicKey) keyPair.getPublic())
                    .privateKey((RSAPrivateKey) keyPair.getPrivate())
                    .keyID(UUID.randomUUID().toString())
                    .build();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }


    private KeyPair keyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }
}

