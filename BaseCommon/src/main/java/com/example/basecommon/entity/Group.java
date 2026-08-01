package com.example.basecommon.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.*;


import java.io.Serializable;


@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "Group")
@Table(name = "`groups`")
@SuppressWarnings("unused")
public class Group extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "name", nullable = false, length = 255)
    private String name;


    @Column(name = "kana", nullable = false, length = 255)
    private String kana;


    @Column(name = "prefecture_id")
    private Long prefectureId;


    @Column(name = "municipality_id")
    private Long municipalityId;


    @Column(name = "code", nullable = false,  length = 50)
    private String code;


    @Column(name = "effective_start_at", nullable = false, length = 8)
    private String effectiveStartAt;


    @Column(name = "effective_end_at", nullable = false, length = 8)
    private String effectiveEndAt;


    @Column(name = "enable_health_guidance", nullable = false)
    private boolean enableHealthGuidance = false;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;


    @Column(name = "logo_image_id", nullable = false)
    private Long logoImageId;


    @Column(name = "is_registered_prefecture", nullable = false)
    private Boolean isRegisteredPrefecture = true;


    @Column(name = "full_name_state")
    private int fullNameState;


    @Column(name = "email_state")
    private int emailState;


    @Column(name = "birthday_state")
    private int birthdayState;


    @Column(name = "is_displayed", nullable = false)
    private boolean isDisplayed = true;


}

