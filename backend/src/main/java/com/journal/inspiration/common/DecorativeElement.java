package com.journal.inspiration.common;

public class DecorativeElement {
    private String elementType;
    private String elementName;
    private Integer quantity;
    private String position;
    private String color;
    private String size;
    private String style;

    public DecorativeElement() {
    }

    public DecorativeElement(String elementType, String elementName, Integer quantity, String position) {
        this.elementType = elementType;
        this.elementName = elementName;
        this.quantity = quantity;
        this.position = position;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getElementName() {
        return elementName;
    }

    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
}
