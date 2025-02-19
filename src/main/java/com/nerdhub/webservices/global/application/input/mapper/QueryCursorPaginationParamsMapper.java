package com.nerdhub.webservices.global.application.input.mapper;

import com.nerdhub.webservices.global.application.input.CommonCursorPaginationInput;
import com.nerdhub.webservices.global.domain.datastore.params.QueryCursorPaginationParams;

public class QueryCursorPaginationParamsMapper {
    QueryCursorPaginationParams toQueryParams(CommonCursorPaginationInput input) {
        return new QueryCursorPaginationParams(
                input.getCursor(),
                input.getLimit(),
                input.getSortBy(),
                input.getOrder()
        );
    }
}
