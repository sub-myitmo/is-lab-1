package ru.is1.config.ws;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.websocket.Session;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/websocket")
public class DataWebSocket {

    private Broadcaster broadcaster() {
        return CDI.current().select(Broadcaster.class).get();
    }

    @OnOpen
    public void onOpen(Session session) {
        broadcaster().register(session);
        System.out.println("WebSocket connected: " + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        broadcaster().unregister(session);
        System.out.println("WebSocket disconnected: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if ("ping".equalsIgnoreCase(message)) System.out.println("Websocket ping from client: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        broadcaster().unregister(session);
        System.err.println("WebSocket error: " + throwable.getMessage());
        throwable.printStackTrace();
    }
}