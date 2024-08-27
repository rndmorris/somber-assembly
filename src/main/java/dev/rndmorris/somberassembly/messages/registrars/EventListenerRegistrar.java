package dev.rndmorris.somberassembly.messages.registrars;

import java.util.HashMap;

import dev.rndmorris.somberassembly.messages.IEvent;
import dev.rndmorris.somberassembly.messages.IEventListenerRegistrar;

public class EventListenerRegistrar<T extends IEvent> implements IEventListenerRegistrar<T> {

    private final HashMap<Integer, EventListener<T>> listeners = new HashMap<>();
    private int id = 0;

    @Override
    public int registerEventListener(EventListener<T> listener) {
        var id = this.id++;
        listeners.put(id, listener);
        return id;
    }

    @Override
    public void announceEvent(T event) {
        for (var kvPair : this.listeners.entrySet()) {
            kvPair.getValue()
                .handle(event);
        }
    }
}
