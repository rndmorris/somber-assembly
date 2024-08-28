package dev.rndmorris.somberassembly.common.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import dev.rndmorris.somberassembly.SomberAssembly;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public abstract class SomberItem extends Item {

    public static ItemResource itemResource;

    public SomberItem() {
        this.setCreativeTab(SomberAssembly.creativeTab);
    }

    public static void preInit() {
        itemResource = new ItemResource();
    }

    public static void postInit() {
        ThaumcraftApi.registerObjectTag(
            decayingFlesh(0),
            new AspectList().add(Aspect.MAN, 1)
                .add(Aspect.FLESH, 2)
                .add(Aspect.SENSES, 2)
                .add(Aspect.ENTROPY, 1));
    }

    public static ItemStack decayingFlesh(int stackSize) {
        return new ItemStack(itemResource, stackSize, ItemResource.DECAYING_FLESH);
    }

    @Override
    public Item setUnlocalizedName(String name) {
        return super.setUnlocalizedName(SomberAssembly.prefixModid(name));
    }
}
