package dev.rndmorris.somberassembly.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.registry.GameRegistry;

public class BoneBlock extends SomberBlock {

    private static final String name = "block_bone";

    public BoneBlock() {
        super(Material.rock);

        this.setBlockName(name);
        this.setBlockTextureName(name);
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, name);
    }
}
