package dev.rndmorris.somberassembly.messages;

public interface IEventListenerRegistrar<T extends IEvent> {

    int registerEventListener(EventListener<T> listener);

    void announceEvent(T event);

    interface EventListener<T> {

        void handle(T event);
    }
}
