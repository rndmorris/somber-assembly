package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.material.Material;

import cpw.mods.fml.common.registry.GameRegistry;

public class BoneBlock extends SomberBlock {

    public static final String NAME = "block_bone";

    public BoneBlock() {
        super(Material.rock);

        this.setBlockNamePrefixed(NAME);
        this.setBlockTextureNamePrefixed(NAME);
    }
}
