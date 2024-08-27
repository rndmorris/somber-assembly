package dev.rndmorris.somberassembly.items;

import net.minecraft.item.Item;

import dev.rndmorris.somberassembly.SomberAssembly;
import net.minecraft.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public abstract class SomberItem extends Item {

    public static ItemResource itemResource;

    public static void initItems() {
        itemResource = new ItemResource();
    }

    public static void initItemAspects() {
        ThaumcraftApi.registerObjectTag(decayingFlesh(), new AspectList().add(Aspect.MAN, 1).add(Aspect.FLESH, 2).add(Aspect.SENSES, 2).add(Aspect.ENTROPY, 1));
    }

    public static ItemStack decayingFlesh() {
        return new ItemStack(itemResource, 0, ItemResource.DECAYING_FLESH);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(SomberAssembly.prefixModid(name));
    }
}
