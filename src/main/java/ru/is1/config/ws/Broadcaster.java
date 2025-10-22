package ru.is1.config.ws;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.websocket.Session;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class Broadcaster {
    private final Set<Session> sessions = ConcurrentHashMap.newKeySet();

    public void register(Session s)    { sessions.add(s); }
    public void unregister(Session s)  { sessions.remove(s); }

    public void broadcast(String json) {
        for (Session s : sessions) {
            if (s.isOpen()) {
                s.getAsyncRemote().sendText(json);
            }
        }
    }
}

