package com.example.basecommon.repository;

import com.example.basecommon.entity.OauthAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


import java.util.List;




public interface OauthAccessTokenRepository extends JpaRepository<OauthAccessToken, String> {


    OauthAccessToken findFirstByTokenId(String tokenId);


    List<OauthAccessToken> findAllByRefreshToken(String refreshToken);


    @Modifying
    void deleteByRefreshToken(String refreshToken);
}

