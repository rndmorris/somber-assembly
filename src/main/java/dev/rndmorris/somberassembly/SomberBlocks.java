package dev.rndmorris.somberassembly;

import net.minecraft.block.Block;

import dev.rndmorris.somberassembly.blocks.BoneBlock;

public class SomberBlocks {

    public static Block boneBlock;

    public static void init() {
        boneBlock = new BoneBlock();
    }
}
