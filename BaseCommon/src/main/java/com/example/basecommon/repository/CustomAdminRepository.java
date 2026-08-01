package com.example.basecommon.repository;

import com.example.basecommon.entity.Admin;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;


/**
 * CustomAccountRepository
 */
public interface CustomAdminRepository {
    @Query("SELECT a FROM Admins a")
    Page<Admin> getListAdmin(Pageable pageable);
}



