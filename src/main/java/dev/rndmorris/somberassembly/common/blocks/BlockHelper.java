package dev.rndmorris.somberassembly.common.blocks;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import thaumcraft.api.ItemApi;

public class BlockHelper {

    public static ItemStack carpet(int metadata) {
        return new ItemStack(Blocks.carpet, 0, MathHelper.clamp_int(metadata, 0, 15));
    }

    public static ItemStack carpet(Random random) {
        return carpet(MathHelper.getRandomIntegerInRange(random, 0, 15));
    }

    public static ItemStack chiseledStoneBrick() {
        return new ItemStack(Blocks.stonebrick, 0, 3);
    }

    public static ItemStack coarseDirt() {
        return new ItemStack(Blocks.dirt, 0, 1);
    }

    public static class Thaumcraft {

        public static ItemStack banner() {
            return ItemApi.getBlock("blockWoodenDevice", 8);
        }
    }
}
