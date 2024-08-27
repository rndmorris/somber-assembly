package dev.rndmorris.somberassembly.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;

import com.github.bsideup.jabel.Desugar;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;

public class CompoundRecipe {

    final AspectList aspects = new AspectList();
    ItemStack[][][] blueprint;

    private CompoundRecipe() {}

    public static CompoundRecipe start() {
        return new CompoundRecipe();
    }

    public CompoundRecipe aspectCost(Aspect aspect, int amount) {
        aspects.add(aspect, amount);
        return this;
    }

    public AspectList aspectCost() {
        return this.aspects;
    }

    public CompoundRecipe blueprint(SetShapeClosure setShape) {
        this.blueprint = setShape.buildBlueprint();
        return this;
    }

    public ItemStack[][][] blueprint() {
        return this.blueprint;
    }

    public interface SetShapeClosure {

        ItemStack[][][] buildBlueprint();
    }

    public List<Object> toRecipeList() {
        var translated = translateBlueprint();
        // Order: String tag, AspectList aspects, int xDimension, int yDimension, int zDimension, Object[] blocks
        return Arrays
            .asList(new Object[] { aspects, translated.dimX, translated.dimY, translated.dimZ, translated.blockList });
    }

    private TranslatedBlueprint translateBlueprint() {
        final var dimY = this.blueprint.length;
        var dimX = Integer.MIN_VALUE;
        var dimZ = Integer.MIN_VALUE;

        final var translated = new ArrayList<ItemStack>(dimY);

        for (ItemStack[][] itemStacks : this.blueprint) {
            dimX = Integer.max(dimX, itemStacks.length);
            for (ItemStack[] itemStack : itemStacks) {
                dimZ = Integer.max(dimZ, itemStack.length);
                Collections.addAll(translated, itemStack);
            }
        }

        return new TranslatedBlueprint(dimX, dimY, dimZ, translated);
    }

    @Desugar
    private record TranslatedBlueprint(int dimX, int dimY, int dimZ, List<ItemStack> blockList) {};
}
