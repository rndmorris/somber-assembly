package dev.rndmorris.somberassembly.common.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.items.SomberItem;

public class LootGeneration {

    public final static List<WeightedRandomChestContent> graveContents = new ArrayList<>();

    public static void init() {
        Collections.addAll(
            graveContents,
            new WeightedRandomChestContent(Items.rotten_flesh, 0, 1, 1, 10),
            new WeightedRandomChestContent(Items.bone, 0, 1, 1, 5),
            new WeightedRandomChestContent(SomberItem.decayingFlesh(1), 1, 1, 1));
    }

    public static ChestGenHooks graveChestGenHooks(int min, int max) {
        return new ChestGenHooks(
            SomberAssembly.prefixModid("graveChestContents"),
            graveContents.toArray(new WeightedRandomChestContent[0]),
            min,
            max);
    }
}
