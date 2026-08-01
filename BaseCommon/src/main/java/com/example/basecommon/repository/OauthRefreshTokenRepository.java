package com.example.basecommon.repository;

import com.example.basecommon.entity.OauthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


import java.util.Map;
import java.util.Optional;



public interface OauthRefreshTokenRepository extends JpaRepository<OauthRefreshToken, String> {


    OauthRefreshToken findFirstByTokenId(String tokenId);


    @Modifying
    void deleteByTokenId(String tokenId);


    Optional<OauthRefreshToken> findByTokenId(String tokenId);
}

