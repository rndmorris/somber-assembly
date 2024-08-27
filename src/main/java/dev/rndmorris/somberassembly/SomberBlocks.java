package dev.rndmorris.somberassembly;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import dev.rndmorris.somberassembly.blocks.BoneBlock;
import thaumcraft.api.ItemApi;

public class SomberBlocks {

    public static Block boneBlock;

    public static void init() {
        boneBlock = new BoneBlock();
    }

    /**
     * Helper methods to get Thaumcraft blocks.
     */
    public static class Thaumcraft {

        /**
         * Placeholder block for compound recipes. Displays as "Empty Space".
         */
        public static ItemStack emptyBlock() {
            return ItemApi.getBlock("blockHole", 0);
        }

        public static Block fleshBlock() {
            return Block.getBlockFromItem(fleshBlockItemStack().getItem());
        }

        public static int fleshBlockDamage() {
            return 2;
        }

        public static ItemStack fleshBlockItemStack() {
            return ItemApi.getBlock("blockTaint", fleshBlockDamage());
        }
    }
}
