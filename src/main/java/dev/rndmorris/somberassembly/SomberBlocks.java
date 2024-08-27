package dev.rndmorris.somberassembly;

import net.minecraft.block.Block;

import dev.rndmorris.somberassembly.blocks.BoneBlock;

public class SomberBlocks {

    public static Block bone_block;

    public static void init() {
        bone_block = new BoneBlock();
    }
}
