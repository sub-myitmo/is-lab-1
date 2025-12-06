package ru.is1.dal.entity;

public enum ImportStatus {
    SUCCESS,
    ERROR;

    public String getDisplayName() {
        return this.name().charAt(0) + this.name().substring(1).toLowerCase();
    }
}
