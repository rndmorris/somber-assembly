package dev.rndmorris.somberassembly.messages;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class EntityScannedEvent implements IEvent<Entity> {

    private final EntityPlayer byPlayer;
    private final Entity scannedEntity;

    public EntityScannedEvent(EntityPlayer byPlayer, Entity scannedEntity) {
        this.byPlayer = byPlayer;
        this.scannedEntity = scannedEntity;
    }

    public EntityPlayer byPlayer() {
        return this.byPlayer;
    }

    public Entity scannedObject() {
        return this.scannedEntity;
    }
}
