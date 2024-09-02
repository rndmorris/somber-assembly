package dev.rndmorris.somberassembly.common.blocks;

import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

import thaumcraft.api.ItemApi;

public class BlockHelper {

    public static ItemStack carpet(int metadata) {
        return new ItemStack(Blocks.carpet, 0, MathHelper.clamp_int(metadata, 0, 15));
    }

    public static ItemStack carpet(Random random) {
        return carpet(MathHelper.getRandomIntegerInRange(random, 0, 15));
    }

    public static ItemStack chiseledStoneBrick() {
        return new ItemStack(Blocks.stonebrick, 0, 3);
    }

    public static ItemStack coarseDirt() {
        return new ItemStack(Blocks.dirt, 0, 1);
    }

    public static ItemStack flowerAllium() {
        return new ItemStack(Blocks.red_flower, 0, 2);
    }

    public static ItemStack flowerAzureBluet() {
        return new ItemStack(Blocks.red_flower, 0, 3);
    }

    public static ItemStack flowerBlueOrchid() {
        return new ItemStack(Blocks.red_flower, 0, 1);
    }

    public static ItemStack flowerDandelion() {
        return new ItemStack(Blocks.yellow_flower);
    }

    public static ItemStack flowerOxeyeDaisy() {
        return new ItemStack(Blocks.red_flower, 0, 8);
    }

    public static ItemStack flowerPoppy() {
        return new ItemStack(Blocks.red_flower, 0);
    }

    public static ItemStack flowerTulipOrange() {
        return new ItemStack(Blocks.red_flower, 0, 5);
    }

    public static ItemStack flowerTulipRed() {
        return new ItemStack(Blocks.red_flower, 0, 4);
    }

    public static ItemStack flowerTulipPink() {
        return new ItemStack(Blocks.red_flower, 0, 7);
    }

    public static ItemStack flowerTulipWhite() {
        return new ItemStack(Blocks.red_flower, 0, 6);
    }

    public static class Thaumcraft {

        public static ItemStack arcaneStoneBricks() {
            return ItemApi.getBlock("blockCosmeticSolid", 7);
        }

        public static ItemStack arcaneWorktable() {
            return ItemApi.getBlock("blockTable", 15);
        }

        public static ItemStack banner() {
            return ItemApi.getBlock("blockWoodenDevice", 8);
        }

        public static ItemStack crucible() {
            return ItemApi.getBlock("blockMetalDevice", 0);
        }

        public static ItemStack table(boolean northSouth) {
            return ItemApi.getBlock("blockTable", northSouth ? 1 : 0);
        }
    }
}
