package com.example.basecommon.repository;

import com.example.basecommon.entity.OauthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;



@Repository
public interface OauthClientDetailsRepository extends JpaRepository<OauthClientDetails, String> {


    Optional<OauthClientDetails> findByClientId(String clientId);


}

