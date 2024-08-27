package dev.rndmorris.somberassembly.messages;

public interface IEventRegistrarManager {

    public IEventListenerRegistrar<EntityScannedEvent> entityScannedEventRegistrar();

    public IEventListenerRegistrar<ItemScannedEvent> itemScannedEventRegistrar();
}
