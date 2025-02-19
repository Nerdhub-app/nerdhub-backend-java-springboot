package com.nerdhub.webservices.global.application.input;

import lombok.Getter;

@Getter
public class CommonCursorPaginationInput {
    private String cursor;
    private int limit;
    private String sortBy;
    private String order;

    public CommonCursorPaginationInput setCursor(String cursor) {
        this.cursor = cursor;
        return this;
    }

    public CommonCursorPaginationInput setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public CommonCursorPaginationInput setSortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public CommonCursorPaginationInput setOrder(String order) {
        this.order = order;
        return this;
    }
}
