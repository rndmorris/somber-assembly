package dev.rndmorris.somberassembly.data.events;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import joptsimple.internal.Objects;

public class SimpleEventManager<E> implements IEventManager<E> {

    private final List<EventHandler<E>> handlers = new ArrayList<>();

    @Override
    public void registerHandler(@Nonnull EventHandler<E> handler) {
        Objects.ensureNotNull(handler);
        handlers.add(handler);
    }

    @Override
    public void announceEvent(@Nonnull E event) {
        Objects.ensureNotNull(event);
        for (var handler : handlers) {
            handler.handle(event);
        }
    }
}
