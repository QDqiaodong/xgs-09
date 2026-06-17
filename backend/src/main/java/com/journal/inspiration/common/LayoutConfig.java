package com.journal.inspiration.common;

import java.util.List;

public class LayoutConfig {
    private Integer columns;
    private String columnGap;
    private String padding;
    private List<LayoutArea> areas;

    public LayoutConfig() {
    }

    public Integer getColumns() {
        return columns;
    }

    public void setColumns(Integer columns) {
        this.columns = columns;
    }

    public String getColumnGap() {
        return columnGap;
    }

    public void setColumnGap(String columnGap) {
        this.columnGap = columnGap;
    }

    public String getPadding() {
        return padding;
    }

    public void setPadding(String padding) {
        this.padding = padding;
    }

    public List<LayoutArea> getAreas() {
        return areas;
    }

    public void setAreas(List<LayoutArea> areas) {
        this.areas = areas;
    }
}
