package com.example.basecommon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;


@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "DeviceToken")
@Table(name = "device_tokens")
@SuppressWarnings("unused")
public class DeviceToken extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "user_id")
    private Long userId;


    @Column(name = "jti", length = 100, nullable = false)
    private String jti;


    @Column(name = "device_token", nullable = false, unique = true, length = 1000)
    private String deviceToken;


    @Column(name = "is_admin", nullable = false)
    private Boolean isAdmin = Boolean.FALSE;


    @Column(name = "access_token", length = 1000)
    private String accessToken;
}

