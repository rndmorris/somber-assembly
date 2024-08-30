package dev.rndmorris.somberassembly.common.world.structure;

import static net.minecraft.init.Blocks.dirt;
import static net.minecraft.init.Blocks.flower_pot;
import static net.minecraft.init.Blocks.gravel;
import static net.minecraft.init.Blocks.iron_bars;
import static net.minecraft.init.Blocks.log;
import static net.minecraft.init.Blocks.planks;
import static net.minecraft.init.Blocks.stone_brick_stairs;
import static net.minecraft.init.Blocks.stone_slab;
import static net.minecraft.init.Blocks.stonebrick;
import static net.minecraft.init.Blocks.torch;
import static net.minecraft.init.Blocks.wooden_slab;

import java.util.List;
import java.util.Random;

import dev.rndmorris.somberassembly.common.blocks.BlockHelper;
import dev.rndmorris.somberassembly.utils.ArrayUtil;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.common.world.LootGeneration;

public class VillageGraveyardSmall extends SomberVillage {

    public static final int xLength = 9;
    public static final int height = 5;
    public static final int zLength = 9;

    public static final int yShift = -2;

    private final ChestGenHooks graveChestHooks;
    private final Boolean[] graveIsFlowery;

    public VillageGraveyardSmall(Start start, int componentType, Random random, StructureBoundingBox boundingBox,
        int coordBaseMode) {
        super(start, componentType, random, boundingBox, coordBaseMode);
        graveChestHooks = LootGeneration.graveChestGenHooks(10, 10);
        graveIsFlowery = new Boolean[3];
        ArrayUtil.fillFromInitializer(graveIsFlowery, i -> random.nextBoolean());
    }

    public int structureHeight() {
        return height;
    }

    public int yAdjustment() {
        return yShift;
    };

    public static VillageGraveyardSmall build(StructureVillagePieces.Start start, List<StructureComponent> pieces,
        Random rand, int x, int y, int z, int coordBaseMode, int componentType) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox
            .getComponentToAddBoundingBox(x, y, z, 0, 0, 0, xLength, height, zLength, coordBaseMode);
        return canVillageGoDeeper(structureboundingbox)
            && StructureComponent.findIntersecting(pieces, structureboundingbox) == null
                ? new VillageGraveyardSmall(start, componentType, rand, structureboundingbox, coordBaseMode)
                : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox boundingBox) {
        if (!canWorkWithAverageGroundLevel(world, boundingBox)) {
            return true;
        }

        painter = new Painter(world, boundingBox, rand);

        buildWalls();
        buildPosts();
        buildGraves();
        buildPath();

        painter.set(6, 3, 4, torch, 0);

        return true;
    }

    private void buildWalls() {
        paintWall(1, 1, 3, 0);
        paintWall(1, 7, 5, 0);
        paintWall(1, 1, 0, 5);
        paintWall(7, 1, 0, 5);
    }

    private void paintWall(int x, int z, int deltaX, int deltaZ) {
        painter.fill(x, 2, z, deltaX, 0, deltaZ, planks);
        painter.fill(x, 3, z, deltaX, 0, deltaZ, iron_bars);
        painter.fill(x, 4, z, deltaX, 0, deltaZ, wooden_slab);
    }

    private void buildPosts() {
        for (var xx = 0; xx < 3; ++xx) {
            for (var zz = 0; zz < 3; ++zz) {
                if (xx == 1 && zz == 1) {
                    continue;
                }
                final var x = 1 + (xx * 3);
                final var z = 1 + (zz * 3);
                paintPost(x, z);
            }
        }
    }

    private void paintPost(int x, int z) {
        final var y = 2;
        painter.fill(x, y, z, 0, 2, 0, log);
        painter.set(x, y + 3, z, wooden_slab);
    }

    private void buildGraves() {
        for (var zz = 0; zz < 3; ++zz) {
            paintGrave(2 + (zz * 2), graveIsFlowery[zz]);
        }
    }

    private void paintGrave(int z, boolean isFlowerVariant) {
        painter.set(2, 2, z, stonebrick);
        painter.set(2, 3, z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 0));

        if (isFlowerVariant) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(3, 1, z, coarseDirt);
            painter.generateChest(3, 0, z, graveChestHooks);
            painter.set(4, 1, z, coarseDirt);
            painter.generateChest(4, 0, z, graveChestHooks);

            painter.createFlowerPot(3, 2, z);
        } else {
            painter.set(3, 2, z, stone_slab);
            painter.generateChest(3, 1, z, graveChestHooks);
            painter.set(4, 2, z, stone_slab);
            painter.generateChest(4, 1, z, graveChestHooks);
        }
    }

    private void buildPath() {
        painter.fill(5, 1, 0, 1, 0, 5, gravel);
        painter.set(5, 1, 6, gravel);
    }
}
