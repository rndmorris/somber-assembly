package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import cpw.mods.fml.common.registry.GameRegistry;
import dev.rndmorris.somberassembly.SomberAssembly;

public abstract class SomberBlock extends Block {

    public static Block boneBlock;
    public static Block blockMachine;

    public static void preInit() {
        boneBlock = new BoneBlock().register();
        blockMachine = new BlockMachine().register();
    }

    protected SomberBlock(Material materialIn) {
        super(materialIn);
        setDefaultName();
        setCreativeTab(SomberAssembly.creativeTab);
    }

    protected abstract String getPlainName();

    @Override
    public Block setBlockName(String name) {
        return super.setBlockName(SomberAssembly.prefixModid(name));
    }

    public void setDefaultName() {
        setBlockName(getPlainName());
    }

    @Override
    public Block setBlockTextureName(String name) {
        return super.setBlockTextureName(SomberAssembly.prefixModid(name));
    }

    public SomberBlock register() {
        GameRegistry.registerBlock(this, getPlainName());
        return this;
    }
}
