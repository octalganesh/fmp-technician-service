package com.octal.fsm.dto;

import lombok.Data;

import java.io.Serializable;


@Data
public class PagedItemInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public final int items;
    public final int page;
    public final String sortingField;
    public final boolean isSortingAsc;
    public final String filterQuery;

    public PagedItemInfo(int page, int items, String sortingField, boolean sortingAsc, String filterQuery) {
        this.page = page;
        this.items = items;
        this.sortingField = sortingField;
        isSortingAsc = sortingAsc;
        this.filterQuery = filterQuery;
    }

    @Override
    public String toString() {
        return "PagedItemInfo [items=" + items + ", page=" + page + ", sortingField=" + sortingField + ", isSortingAsc="
                + isSortingAsc + ", filterQuery=" + filterQuery + "]";
    }

}