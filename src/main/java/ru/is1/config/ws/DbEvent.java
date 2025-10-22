package ru.is1.config.ws;

public record DbEvent(String type, Long id, String object) {}
