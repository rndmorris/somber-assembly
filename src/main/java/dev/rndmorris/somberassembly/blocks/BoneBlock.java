package dev.rndmorris.somberassembly.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.registry.GameRegistry;
import dev.rndmorris.somberassembly.SomberAssembly;

public class BoneBlock extends Block {

    private static final String name = "bone_block";

    public BoneBlock() {
        super(Material.rock);

        // to-do: set block sounds. Borrow TC4's jar sounds?

        this.setBlockName(name);
        this.setBlockTextureName(SomberAssembly.prefixModid(name));
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, name);
    }
}
