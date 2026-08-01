package com.example.basecommon.repository.impl;

import com.example.basecommon.enums.TypeCompare;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringTemplate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.stereotype.Repository;


import jakarta.validation.constraints.NotNull;
import java.util.Date;


@Repository
public class BaseRepository<MODELS> {


    @SuppressWarnings({"rawtypes", "unchecked"})
    protected OrderSpecifier<?> toOrderSpecifier(Class<? extends MODELS> type,
                                                 PathMetadata pathMetadata,
                                                 Sort.Order order) {


        PathBuilder<MODELS> builder = new PathBuilder<>(type, pathMetadata);
        return new OrderSpecifier(order.isAscending() ? Order.ASC
                : Order.DESC, this.buildOrderPropertyPathFrom(builder, order),
                this.toQueryDslNullHandling(order.getNullHandling()));
    }


    private OrderSpecifier.NullHandling toQueryDslNullHandling(Sort.NullHandling nullHandling) {


        switch (nullHandling) {
            case NULLS_FIRST:
                return OrderSpecifier.NullHandling.NullsFirst;
            case NULLS_LAST:
                return OrderSpecifier.NullHandling.NullsLast;
            case NATIVE:
            default:
                return OrderSpecifier.NullHandling.Default;
        }
    }


    private Expression<?> buildOrderPropertyPathFrom(PathBuilder<MODELS> pathBuilder, Sort.Order order) {


        PropertyPath path = PropertyPath.from(order.getProperty(), pathBuilder.getType());
        Expression<?> sortPropertyExpression = pathBuilder;
        while (path != null) {
            if (!path.hasNext() && order.isIgnoreCase()) {
                sortPropertyExpression = Expressions.stringPath((Path<?>) sortPropertyExpression,
                        path.getSegment()).lower();
            } else {
                sortPropertyExpression = Expressions.path(path.getType(), (Path<?>) sortPropertyExpression,
                        path.getSegment());
            }


            path = path.next();
        }
        return sortPropertyExpression;
    }


    protected OrderSpecifier[] getOrderSpecifiers(@NotNull Pageable pageable, @NotNull Class className) {
        String tableName = className.getSimpleName();
        final String orderVariable = String.valueOf(Character.toLowerCase(tableName.charAt(0))).concat(tableName.substring(1));


        return pageable.getSort().stream()
                .map(order -> new OrderSpecifier(
                        Order.valueOf(order.getDirection().toString()),
                        new PathBuilder(className, orderVariable).get(order.getProperty())).nullsLast()
                )
                .toArray(OrderSpecifier[]::new);
    }


    protected BooleanExpression dateCompare(DateTimePath<Date> dateTimePath, Date date, TypeCompare typeCompare, String timezoneId) {
        StringTemplate dbDate = Expressions.stringTemplate(String.format("DATE(date_convert_with_timezone({0}, '%s'))", timezoneId), dateTimePath);
        StringTemplate compareDate = Expressions.stringTemplate("DATE({0})", date);


        switch (typeCompare) {
            case GT:
                return dbDate.gt(compareDate);
            case GOE:
                return dbDate.goe(compareDate);
            case LT:
                return dbDate.lt(compareDate);
            case LOE:
                return dbDate.loe(compareDate);
            default:
                return dbDate.eq(compareDate);
        }
    }


    protected BooleanExpression dateBetween(DateTimePath<Date> dateTimePath, Date from, Date to, String timezoneId) {
        StringTemplate dbDate = Expressions.stringTemplate(String.format("DATE(date_convert_with_timezone({0}, '%s'))", timezoneId), dateTimePath);
        StringTemplate fromDate = Expressions.stringTemplate("DATE({0})", from);
        StringTemplate toDate = Expressions.stringTemplate("DATE({0})", to);
        return dbDate.between(fromDate, toDate);
    }
}

