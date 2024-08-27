package dev.rndmorris.somberassembly.items;

import net.minecraft.item.Item;

import dev.rndmorris.somberassembly.SomberAssembly;

public abstract class SomberItem extends Item {

    public static ItemResource itemResource;

    public static void init() {
        itemResource = new ItemResource();
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(SomberAssembly.prefixModid(name));
    }
}
