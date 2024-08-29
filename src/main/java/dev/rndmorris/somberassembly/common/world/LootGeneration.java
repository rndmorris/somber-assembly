package dev.rndmorris.somberassembly.common.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.items.SomberItem;

public class LootGeneration {

    public final static List<WeightedRandomChestContent> graveContents = new ArrayList<>();

    public final static List<ItemStack> graveyardFlowers = new ArrayList<>();

    public static void init() {
        Collections.addAll(
            graveContents,
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.bone, 0, 1, 1, 5),
            new WeightedRandomChestContent(SomberItem.decayingFlesh(1), 1, 1, 1));

        final var flowers = new ArrayList<ItemStack>();
        Blocks.red_flower.getSubBlocks(Item.getItemFromBlock(Blocks.red_flower), null, flowers);
        Collections.addAll(graveyardFlowers, flowers.toArray(new ItemStack[0]));
    }

    public static ChestGenHooks graveChestGenHooks(int min, int max) {
        return new ChestGenHooks(
            SomberAssembly.prefixModid("graveChestContents"),
            graveContents.toArray(new WeightedRandomChestContent[0]),
            min,
            max);
    }

    public static ItemStack randomFlower(Random random) {
        return graveyardFlowers.get(random.nextInt(graveyardFlowers.size()))
            .copy();
    }
}
