package dev.rndmorris.somberassembly.data.events;

import javax.annotation.Nonnull;

public interface IEventManager<E> {

    /**
     * Register a handler that will be called when an event is announced.
     *
     * @param handler The closure to be called.
     */
    void registerHandler(@Nonnull EventHandler<E> handler);

    /**
     * Announce an event to all registered handlers.
     *
     * @param event The event to be announced.
     */
    void announceEvent(@Nonnull E event);

    interface EventHandler<E> {

        void handle(E event);
    }
}
