package dev.rndmorris.somberassembly;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;

import cpw.mods.fml.common.registry.GameRegistry;
import dev.rndmorris.somberassembly.util.CompoundRecipe;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;

public class SomberRecipes {

    // Compound recipes
    public static CompoundRecipe assembleCreeper;
    public static CompoundRecipe assembleSkeleton;
    public static CompoundRecipe assembleZombie;

    // Vanilla crafting recipes
    public static IRecipe boneBlockRecipe;
    public static IRecipe boneBlockUncraftRecipe;

    // Thaumcraft block references
    private static ItemStack basicWand;

    public static void init() {
        assignBlockReferences();
        craftingRecipes();
        compoundRecipes();
    }

    private static void assignBlockReferences() {
        basicWand = ItemApi.getItem("itemWandCasting", 0);
    }

    private static void craftingRecipes() {
        boneBlockRecipe = new ShapedRecipes(
            3,
            3,
            Utils.fillArray(new ItemStack[9], (index) -> new ItemStack(Items.bone)),
            new ItemStack(SomberBlocks.boneBlock));
        boneBlockUncraftRecipe = new ShapelessRecipes(
            new ItemStack(Items.bone, 9),
            Utils.list(new ItemStack(SomberBlocks.boneBlock)));

        GameRegistry.addRecipe(boneBlockRecipe);
        GameRegistry.addRecipe(boneBlockUncraftRecipe);
    }

    private static void compoundRecipes() {
        mobAssembly();
    }

    private static void mobAssembly() {
        assembleCreeper = CompoundRecipe.start()
            .blueprint(() -> {
                final var tnt = new ItemStack(Blocks.tnt);
                return new ItemStack[][][] { { { tnt, }, { null, }, }, { { tnt, }, { basicWand, } },
                    { { tnt, }, { null, } }, };
            })
            .aspectCost(Aspect.AIR, 15)
            .aspectCost(Aspect.ENTROPY, 25)
            .aspectCost(Aspect.FIRE, 30)
            .aspectCost(Aspect.ORDER, 10);
        assembleSkeleton = CompoundRecipe.start()
            .blueprint(() -> {
                final var bone = new ItemStack(SomberBlocks.boneBlock);
                return new ItemStack[][][] { { { bone, }, { null, }, }, { { bone, }, { basicWand, } },
                    { { bone, }, { null, } }, };
            })
            .aspectCost(Aspect.AIR, 20)
            .aspectCost(Aspect.FIRE, 10)
            .aspectCost(Aspect.ENTROPY, 10)
            .aspectCost(Aspect.ORDER, 5);
        assembleZombie = CompoundRecipe.start()
            .blueprint(() -> {
                final var fleshBlock = SomberBlocks.Thaumcraft.fleshBlockItemStack();
                return new ItemStack[][][] { { { fleshBlock, }, { null, }, }, { { fleshBlock, }, { basicWand, } },
                    { { fleshBlock, }, { null, } }, };
            })
            .aspectCost(Aspect.AIR, 15)
            .aspectCost(Aspect.ENTROPY, 10);
    }
}
