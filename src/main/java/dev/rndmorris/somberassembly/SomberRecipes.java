package dev.rndmorris.somberassembly;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import cpw.mods.fml.common.registry.GameRegistry;
import dev.rndmorris.somberassembly.util.CompoundRecipe;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;

public class SomberRecipes {

    // Compound recipes
    public static CompoundRecipe assembleCreeper;
    public static CompoundRecipe assembleSkeleton;
    public static CompoundRecipe assembleZombie;

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
        GameRegistry.addRecipe(new ItemStack(SomberBlocks.boneBlock), "XXX", "XXX", "XXX", 'X', Items.bone);
        GameRegistry.addShapelessRecipe(new ItemStack(Items.bone, 9), new ItemStack(SomberBlocks.boneBlock));
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
            .aspectCost(Aspect.ENTROPY, 10)
            .aspectCost(Aspect.FIRE, 30)
            .aspectCost(Aspect.ORDER, 10);
        assembleSkeleton = CompoundRecipe.start()
            .blueprint(() -> {
                final var bone = new ItemStack(SomberBlocks.boneBlock);
                return new ItemStack[][][] { { { bone, }, { null, }, }, { { bone, }, { basicWand, } },
                    { { bone, }, { null, } }, };
            })
            .aspectCost(Aspect.AIR, 15)
            .aspectCost(Aspect.FIRE, 10)
            .aspectCost(Aspect.ENTROPY, 15);
        assembleZombie = CompoundRecipe.start()
            .blueprint(() -> {
                final var fleshBlock = new ItemStack(
                    SomberBlocks.Thaumcraft.fleshBlock(), 0,
                    SomberBlocks.Thaumcraft.fleshBlockDamage());
                return new ItemStack[][][] { { { fleshBlock, }, { null, }, }, { { fleshBlock, }, { basicWand, } },
                    { { fleshBlock, }, { null, } }, };
            })
            .aspectCost(Aspect.AIR, 15)
            .aspectCost(Aspect.ENTROPY, 15);
    }
}
