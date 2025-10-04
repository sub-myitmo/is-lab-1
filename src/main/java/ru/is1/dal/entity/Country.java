package ru.is1.dal.entity;


public enum Country {
    RUSSIA,
    SPAIN,
    INDIA;

    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
