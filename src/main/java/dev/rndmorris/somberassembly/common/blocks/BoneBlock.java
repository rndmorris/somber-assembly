package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

public class BoneBlock extends SomberBlock {

    public static final String NAME = "block_bone";

    public BoneBlock() {
        super(Material.rock);

        this.setBlockName(NAME);
        this.setBlockTextureName(NAME);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
}
