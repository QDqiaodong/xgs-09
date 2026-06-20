package com.journal.inspiration.common;

import java.util.List;

public class LayoutConfig {
    private Integer columns;
    private String columnGap;
    private String rowGap;
    private String padding;
    private List<LayoutArea> areas;

    private String whiteSpacePosition;
    private String whiteSpaceSize;
    private String partitionMode;
    private List<Double> partitionRatios;
    private String imageTextLayout;
    private String layoutDirection;
    private String pageBackground;
    private String gridStyle;

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

    public String getRowGap() {
        return rowGap;
    }

    public void setRowGap(String rowGap) {
        this.rowGap = rowGap;
    }

    public String getWhiteSpacePosition() {
        return whiteSpacePosition;
    }

    public void setWhiteSpacePosition(String whiteSpacePosition) {
        this.whiteSpacePosition = whiteSpacePosition;
    }

    public String getWhiteSpaceSize() {
        return whiteSpaceSize;
    }

    public void setWhiteSpaceSize(String whiteSpaceSize) {
        this.whiteSpaceSize = whiteSpaceSize;
    }

    public String getPartitionMode() {
        return partitionMode;
    }

    public void setPartitionMode(String partitionMode) {
        this.partitionMode = partitionMode;
    }

    public List<Double> getPartitionRatios() {
        return partitionRatios;
    }

    public void setPartitionRatios(List<Double> partitionRatios) {
        this.partitionRatios = partitionRatios;
    }

    public String getImageTextLayout() {
        return imageTextLayout;
    }

    public void setImageTextLayout(String imageTextLayout) {
        this.imageTextLayout = imageTextLayout;
    }

    public String getLayoutDirection() {
        return layoutDirection;
    }

    public void setLayoutDirection(String layoutDirection) {
        this.layoutDirection = layoutDirection;
    }

    public String getPageBackground() {
        return pageBackground;
    }

    public void setPageBackground(String pageBackground) {
        this.pageBackground = pageBackground;
    }

    public String getGridStyle() {
        return gridStyle;
    }

    public void setGridStyle(String gridStyle) {
        this.gridStyle = gridStyle;
    }
}
