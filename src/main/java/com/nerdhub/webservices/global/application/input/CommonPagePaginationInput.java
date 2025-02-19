package com.nerdhub.webservices.global.application.input;

import lombok.Getter;

@Getter
public class CommonPagePaginationInput {
    private int page;
    private int limit;
    private String sortBy;
    private String order;

    public CommonPagePaginationInput setPage(int page) {
        this.page = page;
        return this;
    }

    public CommonPagePaginationInput setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public CommonPagePaginationInput setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public CommonPagePaginationInput setOrder(String order) {
        this.order = order;
        return this;
    }
}
