package dev.rndmorris.somberassembly.messages;

import net.minecraft.entity.player.EntityPlayer;

public interface IEvent<T> {

    EntityPlayer byPlayer();

    T scannedObject();
}
