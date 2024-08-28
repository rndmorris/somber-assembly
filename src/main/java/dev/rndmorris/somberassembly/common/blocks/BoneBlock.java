package dev.rndmorris.somberassembly.common.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;

public class BoneBlock extends SomberBlock {

    public static final String NAME = "block_bone";

    public BoneBlock() {
        super(Material.rock);

        this.setBlockName(NAME);
        this.setBlockTextureName(NAME);

        GameRegistry.registerBlock(this, NAME);
    }
}
