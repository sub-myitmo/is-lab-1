package ru.is1.config;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import ru.is1.config.ws.DataWebSocket;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends Application implements ServerApplicationConfig {
    // WebSocket конфигурация
    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(Set<Class<? extends Endpoint>> endpointClasses) {
        return new HashSet<>(); // Возвращаем пустой Set, используем аннотированные классы
    }

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses(Set<Class<?>> scanned) {
        Set<Class<?>> endpoints = new HashSet<>();
        endpoints.add(DataWebSocket.class);
        return endpoints;
    }
}