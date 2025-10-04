package ru.is1.dal.entity;


public enum Color {
    GREEN,
    RED,
    YELLOW,
    ORANGE,
    WHITE;

    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
