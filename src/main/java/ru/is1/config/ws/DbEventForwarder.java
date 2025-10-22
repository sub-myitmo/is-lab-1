package ru.is1.config.ws;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.json.Json;

@ApplicationScoped
public class DbEventForwarder {

    @Inject
    Broadcaster broadcaster;

    public void on(@Observes(during = TransactionPhase.AFTER_SUCCESS) DbEvent e) {
        var json = Json.createObjectBuilder()
                .add("type", e.type())
                .add("id", e.id())
                .add("entity", e.object())
                .build()
                .toString();

        broadcaster.broadcast(json);
    }
}