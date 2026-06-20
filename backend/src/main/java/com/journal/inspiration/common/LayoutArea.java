package com.journal.inspiration.common;

public class LayoutArea {
    private String type;
    private String label;
    private Integer columnSpan;
    private Integer rowSpan;
    private Integer stickerCount;
    private String alignSelf;
    private String justifySelf;

    private Double areaRatio;
    private String imageTextRelation;
    private String textAlign;
    private String textVerticalAlign;
    private java.util.List<DecorativeElement> decorativeElements;
    private String backgroundStyle;
    private String borderStyle;

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

    public Double getAreaRatio() {
        return areaRatio;
    }

    public void setAreaRatio(Double areaRatio) {
        this.areaRatio = areaRatio;
    }

    public String getImageTextRelation() {
        return imageTextRelation;
    }

    public void setImageTextRelation(String imageTextRelation) {
        this.imageTextRelation = imageTextRelation;
    }

    public String getTextAlign() {
        return textAlign;
    }

    public void setTextAlign(String textAlign) {
        this.textAlign = textAlign;
    }

    public String getTextVerticalAlign() {
        return textVerticalAlign;
    }

    public void setTextVerticalAlign(String textVerticalAlign) {
        this.textVerticalAlign = textVerticalAlign;
    }

    public java.util.List<DecorativeElement> getDecorativeElements() {
        return decorativeElements;
    }

    public void setDecorativeElements(java.util.List<DecorativeElement> decorativeElements) {
        this.decorativeElements = decorativeElements;
    }

    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    public String getBorderStyle() {
        return borderStyle;
    }

    public void setBorderStyle(String borderStyle) {
        this.borderStyle = borderStyle;
    }
}
