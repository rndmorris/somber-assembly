package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.material.Material;

public class BoneBlock extends SomberBlock {

    public static final String NAME = "block_bone";

    public BoneBlock() {
        super(Material.rock);

        this.setBlockTextureName(NAME);
    }

    @Override
    public String getPlainName() {
        return NAME;
    }
}
