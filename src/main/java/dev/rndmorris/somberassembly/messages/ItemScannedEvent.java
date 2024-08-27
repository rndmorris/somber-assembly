package dev.rndmorris.somberassembly.messages;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemScannedEvent implements IEvent<ItemStack> {

    private final EntityPlayer byPlayer;
    private final ItemStack scannedItem;

    public ItemScannedEvent(EntityPlayer byPlayer, ItemStack scannedItem) {
        this.byPlayer = byPlayer;
        this.scannedItem = scannedItem;
    }

    public EntityPlayer byPlayer() {
        return this.byPlayer;
    }

    public ItemStack scannedObject() {
        return this.scannedItem;
    }
}
