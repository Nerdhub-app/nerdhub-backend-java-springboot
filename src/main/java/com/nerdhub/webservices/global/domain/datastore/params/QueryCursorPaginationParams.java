package com.nerdhub.webservices.global.domain.datastore.params;

public record QueryCursorPaginationParams(String cursor, int pageSize, String sortBy, String order) {
}
