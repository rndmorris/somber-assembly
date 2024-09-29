package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import dev.rndmorris.somberassembly.SomberAssembly;

public abstract class SomberBlock extends Block {

    public static Block boneBlock;

    public static void preInit() {
        boneBlock = new BoneBlock();
    }

    protected SomberBlock(Material materialIn) {
        super(materialIn);

        this.setCreativeTab(SomberAssembly.creativeTab);
    }

    @Override
    public Block setBlockName(String name) {
        return super.setBlockName(SomberAssembly.prefixModid(name));
    }

    @Override
    public Block setBlockTextureName(String name) {
        return super.setBlockTextureName(SomberAssembly.prefixModid(name));
    }
}
