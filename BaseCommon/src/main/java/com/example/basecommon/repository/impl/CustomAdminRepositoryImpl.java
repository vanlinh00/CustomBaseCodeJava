//package com.example.basecommon.repository.impl;
//
//import com.example.basecommon.entity.Admin;
//import com.example.basecommon.repository.CustomAdminRepository;
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.jpa.impl.JPAQuery;
//
//
//
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Repository;
//
//
//import java.util.List;
//
//
//
//@Repository
//public class CustomAdminRepositoryImpl extends BaseRepository<Admin> implements CustomAdminRepository {
////    private final QAdmin qAdmin = QAdmin.admin;
////
////
////    public CustomAdminRepositoryImpl() {
////    }
////
////
////    @Override
////    public Page<Admin> getListAdmin() {
////        Pageable pageable = PageRequest.of(1, 20, Sort.unsorted());
////        JPAQuery<Admin> query = new JPAQuery<>();
////        BooleanBuilder where = new BooleanBuilder();
////
////
////        List<Admin> list = query.from(qAdmin)
////                .where(where)
////                .limit(pageable.getPageSize())
////                .offset(pageable.getOffset())
////                .orderBy(qAdmin.id.desc().nullsLast())
////                .fetch();
////
////
////        return new PageImpl<>(list, pageable, query.fetchCount());
////    }
//}
//
