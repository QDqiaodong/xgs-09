package com.journal.inspiration.common;

public class LayoutArea {
    private String type;
    private String label;
    private Integer columnSpan;
    private Integer rowSpan;
    private Integer stickerCount;
    private String alignSelf;
    private String justifySelf;

    public LayoutArea() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(Integer columnSpan) {
        this.columnSpan = columnSpan;
    }

    public Integer getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(Integer rowSpan) {
        this.rowSpan = rowSpan;
    }

    public Integer getStickerCount() {
        return stickerCount;
    }

    public void setStickerCount(Integer stickerCount) {
        this.stickerCount = stickerCount;
    }

    public String getAlignSelf() {
        return alignSelf;
    }

    public void setAlignSelf(String alignSelf) {
        this.alignSelf = alignSelf;
    }

    public String getJustifySelf() {
        return justifySelf;
    }

    public void setJustifySelf(String justifySelf) {
        this.justifySelf = justifySelf;
    }
}
