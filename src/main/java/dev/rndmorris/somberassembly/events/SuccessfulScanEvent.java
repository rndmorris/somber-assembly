package dev.rndmorris.somberassembly.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import com.github.bsideup.jabel.Desugar;

import thaumcraft.api.research.ScanResult;

@Desugar
public record SuccessfulScanEvent(EntityPlayer scanningPlayer, ScanResult scanResult) {

    public boolean isObjectScan() {
        return scanResult.type == 1 || scanResult.entity instanceof EntityItem;
    }

    public boolean isEntityScan() {
        return scanResult.type == 2 && !(scanResult.entity instanceof EntityItem);
    }

    public ItemStack getScannedItem() {
        if (scanResult.type == 1) {
            return new ItemStack(Item.getItemById(scanResult.id), 1, scanResult.meta);
        }
        if (scanResult.entity instanceof EntityItem item) {
            return item.getEntityItem()
                .copy();
        }
        return null;
    }

    public Entity getScannedEntity() {
        if (scanResult.entity != null && !(scanResult.entity instanceof EntityItem)) {
            return scanResult.entity;
        }
        return null;
    }
}
