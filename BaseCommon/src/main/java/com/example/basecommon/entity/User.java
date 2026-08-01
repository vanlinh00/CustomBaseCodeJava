package com.example.basecommon.entity;

import com.example.basecommon.enums.EGender;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


import jakarta.persistence.*;


import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@JsonSerialize
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(name = "Users")
@Table(name = "users")
@SuppressWarnings("unused")
public class User extends BaseEntity implements Serializable {


    private static final long serialVersionUID = 1L;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column(name = "group_id")
    private Long groupId;


    @Column(name = "login_id", length = 20, nullable = false)
    private String loginId;


    @Column(name = "password_hash", length = 128, nullable = false)
    private String passwordHash;


    @Column(name = "full_name", length = 128, nullable = true)
    private String fullName;


    @Column(name = "nick_name", length = 128, nullable = true)
    private String nickName;


    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = true)
    private EGender gender;


    @Temporal(TemporalType.DATE)
    @Column(name = "date_of_birth", nullable = true)
    private Date dateOfBirth;


    @Column(name = "height_cm", nullable = true)
    private Float heightCm;


    @Column(name = "email", length = 255, nullable = true)
    private String email;


    @Column(name = "point", nullable = true)
    private Integer point;


    @Column(name = "access_start_date", nullable = true, length = 8)
    private String accessStartDate;


    @Column(name = "access_end_date", nullable = true, length = 8)
    private String accessEndDate;


    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;


    @Column(name = "is_confirmed", nullable = false)
    private Boolean isConfirmed = false;


    @Column(name = "initial_password", length = 128, nullable = true)
    private String initialPassword;


    @Temporal(TemporalType.DATE)
    @Column(name = "point_received_date", nullable = true)
    private Date pointReceivedDate;


    @Column(name = "reg_verify_status", nullable = false)
    private Integer regVerifyStatus = 0;


    @Column(name = "insured_card_symbol", length = 20, nullable = true)
    private String insuredCardSymbol;


    @Column(name = "insured_card_number", length = 20, nullable = true)
    private String insuredCardNumber;


    @Column(name = "insured_card_branch_no", length = 10, nullable = true)
    private String insuredCardBranchNo;


    @Column(name = "previous_state", nullable = true)
    private Integer previousState;
}



