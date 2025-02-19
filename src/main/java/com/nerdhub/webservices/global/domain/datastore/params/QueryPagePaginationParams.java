package com.nerdhub.webservices.global.domain.datastore.params;

public record QueryPagePaginationParams(int page, int pageSize, String sortBy, String order) {
}
