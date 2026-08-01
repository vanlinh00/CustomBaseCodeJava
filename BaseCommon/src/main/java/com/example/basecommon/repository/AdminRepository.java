package com.example.basecommon.repository;

import com.example.basecommon.entity.Admin;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;


import java.util.List;
import java.util.Optional;




@Repository
public interface AdminRepository extends JpaRepository<Admin, Long>, CustomAdminRepository {
    @Modifying
    @Transactional
    @Query("UPDATE Admins a SET a.isDeleted = true " +
            "WHERE a.groupId IN (SELECT g.id FROM Group g WHERE g.effectiveEndAt < :currentDate) " +
            "AND a.isDeleted = false")
    void softDeleteAdminsInExpiredGroups(@Param("currentDate") String currentDate);


    Admin findFirstByLoginId(String username);


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Admin> findByEmail(String email);


    Optional<Admin> findByLoginId(String loginId);


    Optional<Admin> findByLoginIdAndIsDeletedFalse(String loginId);




    @Query(value = "SELECT a.login_id FROM admins a " +
            "WHERE a.login_id LIKE CONCAT(:prefix, '%') " +
            "ORDER BY a.login_id DESC LIMIT 1",
            nativeQuery = true)
    Optional<String> findLastLoginIdByPrefix(@Param("prefix") String prefix);
//
//    @Query("SELECT" +
//            " a.id AS id, " +
//            " a.loginId AS loginId, " +
//            "a.fullName AS fullName, " +
//            "a.role AS role, " +
//            "g.name AS groupName " +
//            "FROM Admins a LEFT JOIN Group g ON a.groupId = g.id " +
//            "WHERE a.isDeleted = false " +
//            "AND (:excludeRoot = false OR a.role != 'ROLE_ADMIN') " +
//            "AND (:loginId IS NULL OR a.loginId LIKE CONCAT('%', :loginId, '%')) " +
//            "AND (:userName IS NULL OR a.fullName LIKE CONCAT('%', :userName, '%')) " +
//            "AND (:#{#userTypes == null || #userTypes.isEmpty()} = true OR a.role IN :userTypes) " +
//            "AND (:groupId IS NULL OR g.id = :groupId)"
//    )
//    Page<IAdminUserFilterProjection> findFilteredAdmins(
//            @Param("loginId") String loginId,
//            @Param("userName") String userName,
//            @Param("userTypes") List<ERole> userTypes,
//            @Param("groupId") Long groupId,
//            @Param("excludeRoot") boolean excludeRoot,
//            Pageable pageable);
//
//    List<Admin> findAllByLoginIdIn(List<String> loginIds);
//
//    @Query("SELECT a.loginId AS loginId, " +
//            "a.fullName AS fullName, " +
//            "a.email AS email, " +
//            "a.role AS roleName, " +
//            "g.id AS groupId, " +
//            "g.name AS groupName, " +
//            "p.id AS prefectureId, " +
//            "p.jisCd AS prefectureJisCode, " +
//            "p.name AS prefectureName, " +
//            "m.id AS municipalityId, " +
//            "m.name AS municipalityName, " +
//            "a.canSendNotification AS canSendNotification, " +
//            "a.isDeleted AS isDeleted " +
//            "FROM Admins a " +
//            "LEFT JOIN Group g ON a.groupId = g.id " +
//            "LEFT JOIN MPrefectures p ON g.prefectureId = p.id " +
//            "LEFT JOIN Municipalities m ON g.municipalityId = m.id " +
//            "WHERE a.id = :id ")
//    Optional<IAdminProfileProjection> findAdminProfileById(@Param("id") Long id);


    boolean existsByEmail(String email);


}

