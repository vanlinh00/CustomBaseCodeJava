package com.example.basecommon.entity;

import com.example.basecommon.enums.ERole;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Entity(name = "Admins")
@Table(name = "admins")
@Builder
@SuppressWarnings("unused")
public class Admin extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "login_id", length = 20, unique = true)
    private String loginId;


    @Column(name = "group_id")
    private Long groupId;


    @Column(name = "full_name", length = 128)
    private String fullName;


    @Column(name = "email", length = 255)
    private String email;


    @Column(name = "password_hash", length = 128)
    private String passwordHash;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20)
    private ERole role;


    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;


    @Column(name = "can_send_notification", nullable = false)
    private Boolean canSendNotification;


}



