package ru.is1.config;

import jakarta.websocket.Endpoint;
import jakarta.websocket.server.ServerApplicationConfig;
import jakarta.websocket.server.ServerEndpointConfig;
import jakarta.ws.rs.ApplicationPath;
import ru.is1.controller.*;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class ApplicationConfig extends jakarta.ws.rs.core.Application implements ServerApplicationConfig {
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        singletons.add(new CorsFilter());
//        singletons.add(new JwtAuthenticationFilter());
        singletons.add(new JacksonConfig());
        return singletons;
    }

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        classes.add(LocationRestController.class);
        classes.add(PersonRestController.class);
        classes.add(CoordinatesRestController.class);
        classes.add(TestResource.class);
//        classes.add(AuthController.class);
        return classes;
    }

    // Методы для WebSocket конфигурации
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