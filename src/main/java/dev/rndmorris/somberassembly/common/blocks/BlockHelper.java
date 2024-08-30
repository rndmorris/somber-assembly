package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import java.util.Random;

public class BlockHelper
{
    public static ItemStack coarseDirt() {
        return new ItemStack(Blocks.dirt, 0, 1);
    }

    public static ItemStack carpet(int metadata) {
        return new ItemStack(Blocks.carpet, 0, MathHelper.clamp_int(metadata, 0, 15));
    }

    public static ItemStack carpet(Random random) {
        return new ItemStack(Blocks.carpet, 0, MathHelper.getRandomIntegerInRange(random, 0, 15));
    }
}
