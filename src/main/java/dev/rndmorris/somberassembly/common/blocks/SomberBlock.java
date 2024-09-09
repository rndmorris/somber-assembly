package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import dev.rndmorris.somberassembly.SomberAssembly;
import thaumcraft.api.ItemApi;

public abstract class SomberBlock extends Block {

    public static Block boneBlock;

    public static void preInit() {
        boneBlock = GameRegistry.registerBlock(new BoneBlock(), BoneBlock.NAME);
    }

    protected SomberBlock(Material materialIn) {
        super(materialIn);

        this.setCreativeTab(SomberAssembly.creativeTab);
    }

    public Block setBlockNamePrefixed(String name) {
        return super.setBlockName(SomberAssembly.prefixModid(name));
    }

    public Block setBlockTextureNamePrefixed(String name) {
        return super.setBlockTextureName(SomberAssembly.prefixModid(name));
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
