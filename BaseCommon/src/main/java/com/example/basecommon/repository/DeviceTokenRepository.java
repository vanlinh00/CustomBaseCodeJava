package com.example.basecommon.repository;
//
import com.example.basecommon.entity.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;


import java.util.List;



public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Long> {


    DeviceToken findFirstByDeviceToken(String deviceToken);


    @Modifying
    void deleteByJtiAndUserId(String jti, Long userId);


    List<DeviceToken> findAllByUserId(Long userId);
    List<DeviceToken> findAllByUserIdAndIsAdmin(Long userId, Boolean isAdmin);


}

