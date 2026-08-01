package com.example.basecommon.repository;

import com.example.basecommon.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.Optional;




import org.springframework.data.domain.Pageable;


import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;


import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, CustomUserRepository {


//    @Query(value =
//            "SELECT " +
//                    "    u.id, u.group_id AS groupId, u.login_id AS loginId, u.full_name AS fullName, " +
//                    "    u.gender, u.date_of_birth AS dateOfBirth, u.height_cm AS heightCm, u.email, " +
//                    "    u.point, u.is_deleted AS isDeleted, u.is_confirmed AS isConfirmed, " +
//                    "    u.access_start_date AS accessStartDate, u.access_end_date AS accessEndDate, g.name AS groupName, " +
//                    "    u.reg_verify_status AS regVerifyStatus, " +
//                    "    (SELECT ld.weight_kg FROM user_lifestyle_daily ld " +
//                    "     WHERE ld.user_id = u.id AND ld.weight_kg IS NOT NULL " +
//                    "     ORDER BY ld.date DESC LIMIT 1) AS weightKg, " +
//                    "    CONCAT(COALESCE(p.name, ''), COALESCE(m.name, '')) AS address, " +
//                    "    hg.state AS hgState, " +
//                    "    me.image_url AS logoImageUrl, " +
//                    "    (SELECT IF(COUNT(*) > 0, 1, 0) FROM user_goal ug WHERE ug.user_id = u.id AND ug.is_user_app = 'APP') AS hasCreatedAppGoal, " +
//                    "    (SELECT IF(COUNT(*) > 0, 1, 0) FROM user_goal ug " +
//                    "     WHERE ug.user_id = u.id AND ug.is_user_app = 'WEB' AND ug.display = 1) AS hasCreatedWebGoal " +
//                    "FROM users u " +
//                    "LEFT JOIN `groups` g ON u.group_id = g.id " +
//                    "LEFT JOIN m_prefectures p ON g.prefecture_id = p.id " +
//                    "LEFT JOIN municipalities m ON g.municipality_id = m.id " +
//                    "LEFT JOIN health_guidance hg ON u.id = hg.user_id " +
//                    "LEFT JOIN media me ON me.id = g.logo_image_id " +
//                    "WHERE u.id = :userId",
//            nativeQuery = true)
//    Optional<IUserCurrentDtoProjection> findCurrentUserDetail(@Param("userId") Long userId);


    @Modifying
    @Transactional
    @Query("UPDATE Users u SET u.isDeleted = true " +
            "WHERE u.groupId IN (SELECT g.id FROM Group g WHERE g.effectiveEndAt < :currentDate) " +
            "AND u.isDeleted = false")
    void softDeleteUsersInExpiredGroups(@Param("currentDate") String currentDate);
    //
    Optional<User> findByLoginId(String loginId);
    //
//    List<User> findAllByLoginIdIn(List<String> loginIds);
//
//    Optional<User> findTopByLoginIdStartingWithOrderByLoginIdDesc(String prefix);
//
    Optional<User> findByLoginIdAndIsDeletedFalse(String loginId);
    //
//    User findFirstByLoginId(String username);
//
//    @Lock(LockModeType.PESSIMISTIC_WRITE)
//    List<User> findAllByEmail(String email);
//
    List<User> findAllByEmailAndIsDeletedFalse(String email);
//    List<User> findAllByEmailAndFullNameAndIsDeletedFalse(String email, String fullName);
//
//    @Query("SELECT u AS user, hg.state AS healthGuidanceState, g AS group, i.name AS userNameInsurance " +
//            "FROM Users u " +
//            "LEFT JOIN Group g ON u.groupId = g.id " +
//            "LEFT JOIN HealthGuidance hg ON u.id = hg.userId " +
//            "LEFT JOIN InsuranceProfile i ON u.id = i.userId " +
//            "WHERE u.loginId = :loginId AND u.isDeleted = false")
//    Optional<IUserDetailProjection> findUserDetailWithState(@Param("loginId") String loginId);
//
//    @Query(
//            value = "SELECT u.id AS id, " +
//                    "u.loginId AS loginId, " +
//                    "u.fullName AS fullName, " +
//                    "ip.name AS userNameInsurance, " +
//                    "u.insuredCardSymbol AS insuredCardSymbol, " +
//                    "u.insuredCardNumber AS insuredCardNumber, " +
//                    "u.insuredCardBranchNo AS insuredCardBranchNo, " +
//                    "g.name AS groupName, " +
//                    "u.accessStartDate AS effectiveStartDate, " +
//                    "u.accessEndDate AS effectiveEndDate, " +
//                    "u.regVerifyStatus AS regVerifyStatus, " +
//                    "u.isDeleted AS isDeleted, " +
//                    "hg.state AS healthGuidanceState, " +
//                    "(SELECT MAX(lh.createdAt) FROM LoginHistory lh WHERE lh.userId = u.id AND lh.success = true) AS lastLoginDate, " +
//                    "(SELECT COUNT(lh2.id) FROM LoginHistory lh2 WHERE lh2.userId = u.id AND lh2.success = true AND lh2.createdAt >= :sevenDaysAgo) AS loginCountLast7Days " +
//                    "FROM Users u " +
//                    "LEFT JOIN Group g ON u.groupId = g.id " +
//                    "LEFT JOIN HealthGuidance hg ON u.id = hg.userId " +
//                    "LEFT JOIN InsuranceProfile ip ON ip.userId = u.id " +
//                    "WHERE u.isDeleted = false " +
//                    "AND u.accessStartDate IS NOT NULL " +
//                    "AND (:regVerifyStatuses IS NULL OR u.regVerifyStatus IN :regVerifyStatuses) " +
//                    "AND (:listIdUser IS NULL OR u.id IN :listIdUser) " +
//                    "AND (:loginId IS NULL OR u.loginId LIKE CONCAT('%', :loginId, '%')) " +
//                    "AND (:userName IS NULL OR ip.name LIKE CONCAT('%', :userName, '%')) " +
//                    "AND (:groupIds IS NULL OR u.groupId IN :groupIds) " +
//                    "AND (:effectiveStartDateInf IS NULL OR u.accessEndDate >= :effectiveStartDateInf) " +
//                    "AND (:effectiveEndDateInf IS NULL OR u.accessStartDate <= :effectiveEndDateInf) " +
//                    "AND (:states IS NULL OR hg.state IN :states) " +
//                    "AND (:instructorId IS NULL OR hg.instructorId = :instructorId)" +
//                    "ORDER BY u.fullName ASC, u.id ASC",
//            countQuery = "SELECT COUNT(u) FROM Users u " +
//                    "LEFT JOIN HealthGuidance hg ON u.id = hg.userId " +
//                    "LEFT JOIN InsuranceProfile ip ON ip.userId = u.id " +
//                    "WHERE u.isDeleted = false " +
//                    "AND u.accessStartDate IS NOT NULL " +
//                    "AND (:regVerifyStatuses IS NULL OR u.regVerifyStatus IN :regVerifyStatuses) " +
//                    "AND (:listIdUser IS NULL OR u.id IN :listIdUser) " +
//                    "AND (:loginId IS NULL OR u.loginId LIKE CONCAT('%', :loginId, '%')) " +
//                    "AND (:userName IS NULL OR ip.name LIKE CONCAT('%', :userName, '%')) " +
//                    "AND (:groupIds IS NULL OR u.groupId IN :groupIds) " +
//                    "AND (:effectiveStartDateInf IS NULL OR u.accessEndDate >= :effectiveStartDateInf) " +
//                    "AND (:effectiveEndDateInf IS NULL OR u.accessStartDate <= :effectiveEndDateInf) " +
//                    "AND (:states IS NULL OR hg.state IN :states) " +
//                    "AND (:instructorId IS NULL OR hg.instructorId = :instructorId)" +
//                    "AND u.isDeleted = false "
//    )
//    Page<IAppUserFilterProjection> findFilteredUsers(
//            @Param("listIdUser") List<Long> listIdUser,
//            @Param("loginId") String loginId,
//            @Param("userName") String userName,
//            @Param("groupIds") List<Long> groupIds,
//            @Param("effectiveStartDateInf") String effectiveStartDateInf,
//            @Param("effectiveEndDateInf") String effectiveEndDateInf,
//            @Param("states") List<Integer> states,
//            @Param("instructorId") Long instructorId,
//            @Param("sevenDaysAgo") java.util.Date sevenDaysAgo,
//            @Param("regVerifyStatuses") List<Integer> regVerifyStatuses,
//            Pageable pageable);
//
//
//    @Query("SELECT g.name AS groupName, " +
//            "u.loginId AS loginId, " +
//            "u.fullName AS fullName, " +
//            "u.passwordHash AS passwordHash, " +
//            "u.gender AS gender, " +
//            "u.dateOfBirth AS dateOfBirth, " +
//            "u.accessStartDate AS accessStartDate, " +
//            "u.accessEndDate AS accessEndDate, " +
//            "u.isConfirmed  AS isConfirmed, " +
//            "u.initialPassword AS initialPassword " +
//            "FROM Users u LEFT JOIN Group g ON u.groupId = g.id " +
//            "WHERE u.loginId IN :loginIds")
//    List<IUserGroupProjection> findUserGroupInfoByLoginIds(@Param("loginIds") List<String> loginIds);
//
//
//    @Query(value =
//            "SELECT u.id AS userId, u.login_id AS loginId, u.date_of_birth AS dateOfBirth, " +
//                    "u.gender AS gender, g.name AS groupName, u.access_start_date AS accessStartDate, " +
//                    "u.access_end_date AS accessEndDate, hg.instructor_id AS instructorId, hg.id AS healthGuidanceId, " +
//                    "(SELECT COALESCE(SUM(pt.point_delta), 0) FROM point_transactions pt WHERE pt.user_id = u.id) AS totalPoints, " +
//                    "ip.name_kanna AS nameKanna, " +
//                    "g.full_name_state AS fullNameState, " +
//                    "g.email_state AS emailState, " +
//                    "g.birthday_state AS birthdayState " +
//                    "FROM users u " +
//                    "LEFT JOIN `groups` g ON u.group_id = g.id " +
//                    "LEFT JOIN health_guidance hg ON hg.user_id = u.id " +
//                    "LEFT JOIN insurance_profiles ip ON ip.user_id = u.id " +
//                    "WHERE u.login_id = :loginId AND u.is_deleted = false",
//            nativeQuery = true)
//    Optional<IUserInfoProjection> findUserInfoWithPointsByLoginId(@Param("loginId") String loginId);
//
//
//    @Query("SELECT u.loginId AS loginId, u.fullName AS fullName, " +
//            "g.id AS groupId, g.name AS groupName, " +
//            "p.id AS prefectureId, p.jisCd AS prefectureJisCode, p.name AS prefectureName, " +
//            "m.id AS municipalityId, m.name AS municipalityName " +
//            "FROM Users u " +
//            "LEFT JOIN Group g ON u.groupId = g.id " +
//            "LEFT JOIN MPrefectures p ON g.prefectureId = p.id " +
//            "LEFT JOIN Municipalities m ON g.municipalityId = m.id " +
//            "WHERE u.id = :userId AND u.isDeleted = false")
//    Optional<IUserProfileProjection> findUserProfileByLoginId(@Param("userId") Long userId);
//


    @Modifying
    @Transactional
    @Query(value = "  UPDATE health_guidance hg\n" +
            "    JOIN users u ON hg.user_id = u.id\n" +
            "    SET hg.state = 0\n" +
            "    WHERE u.group_id = :groupId\n" +
            "      AND u.is_deleted = false", nativeQuery = true)
    int bulkUpdateState(@Param("groupId") Long groupId);


}

