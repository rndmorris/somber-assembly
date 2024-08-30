package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class BlockHelper
{
    public static ItemStack coarseDirt() {
        return new ItemStack(Blocks.dirt, 0, 1);
    }
}
