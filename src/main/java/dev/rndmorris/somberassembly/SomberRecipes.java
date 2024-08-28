package dev.rndmorris.somberassembly;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;

public class SomberRecipes {

    public static void preInit() {
        GameRegistry.addRecipe(new ItemStack(SomberBlocks.boneBlock), "XXX", "XXX", "XXX", 'X', Items.bone);
        GameRegistry.addShapelessRecipe(new ItemStack(Items.bone, 9), new ItemStack(SomberBlocks.boneBlock));
    }
}
