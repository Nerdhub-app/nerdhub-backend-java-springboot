package com.nerdhub.webservices.global.application.input.mapper;

import com.nerdhub.webservices.global.application.input.CommonPagePaginationInput;
import com.nerdhub.webservices.global.domain.datastore.params.QueryPagePaginationParams;

public class QueryPagePaginationParamsMapper {
    QueryPagePaginationParams toQueryParams(CommonPagePaginationInput input) {
        return new QueryPagePaginationParams(
                input.getPage(),
                input.getLimit(),
                input.getSortBy(),
                input.getOrder()
        );
    }
}
