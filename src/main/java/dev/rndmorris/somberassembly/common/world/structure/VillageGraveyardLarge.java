package dev.rndmorris.somberassembly.common.world.structure;

import dev.rndmorris.somberassembly.common.blocks.BlockHelper;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.world.LootGeneration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import java.util.List;
import java.util.Random;

import static net.minecraft.init.Blocks.air;
import static net.minecraft.init.Blocks.glowstone;
import static net.minecraft.init.Blocks.iron_bars;
import static net.minecraft.init.Blocks.log;
import static net.minecraft.init.Blocks.planks;
import static net.minecraft.init.Blocks.stone_brick_stairs;
import static net.minecraft.init.Blocks.stone_slab;
import static net.minecraft.init.Blocks.stonebrick;
import static net.minecraft.init.Blocks.wooden_slab;

public class VillageGraveyardLarge extends SomberVillage
{
    public static final int xLength = 15;
    public static final int height = 18;
    public static final int zLength = 15;

    public static final int yShift = -8;
    private static final int groundLevel = 7;

    private final ChestGenHooks graveChestHooks;
    private final boolean hasBasement;

    private final ItemStack carpet;

    public VillageGraveyardLarge(Start start, int componentType, Random random, StructureBoundingBox boundingBox, int coodBaseMode)
    {
        super(start, componentType, random, boundingBox, coodBaseMode);
        graveChestHooks = LootGeneration.graveChestGenHooks(10, 10);
        hasBasement = Config.graveyardLargeBasementFrequency != -1
            && MathHelper.getRandomIntegerInRange(random, 0, Config.graveyardLargeBasementFrequency) == 0;
        carpet = BlockHelper.carpet(random);
    }

    @Override
    protected int structureHeight()
    {
        return height;
    }

    @Override
    protected int yAdjustment()
    {
        return yShift;
    }

    public static VillageGraveyardLarge build(StructureVillagePieces.Start start, List<StructureComponent> pieces,
                                              Random rand, int x, int y, int z, int coordBaseMode, int componentType) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox
            .getComponentToAddBoundingBox(x, y, z, 0, 0, 0, xLength, height, zLength, coordBaseMode);
        return canVillageGoDeeper(structureboundingbox)
            && StructureComponent.findIntersecting(pieces, structureboundingbox) == null
            ? new VillageGraveyardLarge(start, componentType, rand, structureboundingbox, coordBaseMode)
            : null;
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox boundingBox)
    {
        if (!canWorkWithAverageGroundLevel(world, boundingBox)) {
            return true;
        }

        painter = new Painter(world, boundingBox, rand);

        buildWalls();
        buildWallPosts();
        buildLeftGraves();
        buildRightGraves();

        buildTower();

        if (hasBasement) {
            buildBasement();
        }

        return true;
    }

    private void buildWalls() {
        buildWall(1, 1, 0, 11);
        buildWall(13, 1, 0, 7);
        buildWall(1, 13, 7, 0);
        buildWall(2, 1, 2, 0);
        buildWall(10, 1, 2, 0);

        painter.fill(6, groundLevel + 4, 1, 2, 0, 0, iron_bars);
        painter.fill(6, groundLevel + 5, 1, 2, 0, 0, planks);
    }

    private void buildWall(int x, int z, int deltaX, int deltaZ) {
        painter.fill(x, groundLevel + 1, z, deltaX, 0, deltaZ, planks);
        painter.fill(x, groundLevel + 2, z, deltaX, 1, deltaZ, iron_bars);
        painter.fill(x, groundLevel + 4, z, deltaX, 0, deltaZ, wooden_slab);
    }

    private void buildWallPosts() {
        for (var zz = 0; zz < 4; ++zz) {
            buildWallPost(1, 1 + (zz * 4));
        }
        for (var zz = 0; zz < 3; ++zz) {
            buildWallPost(13, 1 + (zz * 4));
        }
        buildWallPost(5, 1);
        buildWallPost(9, 1);
        buildWallPost(5, 13);
        buildWallPost(9, 13);
    }

    private void buildWallPost(int x, int z) {
        painter.fill(x, groundLevel + 1, z, 0, 3, 0, log);
        painter.set(x, groundLevel + 5, z, wooden_slab);
    }

    private void buildLeftGraves() {
        for (var zz = 0; zz < 5; ++zz) {
            buildLeftGrave(3 + (zz * 2));
        }
    }

    private void buildLeftGrave(int z) {
        final var isFlowerGrave = painter.random.nextBoolean();

        painter.set(3, groundLevel + 1, z, stonebrick);
        painter.set(3, groundLevel + 2, z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 0));

        if (isFlowerGrave) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(4, groundLevel, z, coarseDirt);
            painter.set(5, groundLevel, z, coarseDirt);
            painter.generateChest(4, groundLevel - 1, z, graveChestHooks);
            painter.generateChest(5, groundLevel - 1, z, graveChestHooks);
            painter.createFlowerPot(4, groundLevel + 1, z);
        }
        else {
            painter.set(4, groundLevel + 1, z, stone_slab);
            painter.set(5, groundLevel + 1, z, stone_slab);
            painter.generateChest(4, groundLevel, z, graveChestHooks);
            painter.generateChest(5, groundLevel, z, graveChestHooks);
        }
    }

    private void buildRightGraves() {
        for (int zz = 0; zz < 3; ++zz) {
            buildRightGrave(3 + (zz * 2));
        }
    }

    private void buildRightGrave(int z) {
        final var isFlowerGrave = painter.random.nextBoolean();

        painter.set(11, groundLevel + 1, z, stonebrick);
        painter.set(11, groundLevel + 2, z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 1));

        if (isFlowerGrave) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(9, groundLevel, z, coarseDirt);
            painter.set(10, groundLevel, z, coarseDirt);
            painter.generateChest(9, groundLevel - 1, z, graveChestHooks);
            painter.generateChest(10, groundLevel - 1, z, graveChestHooks);
            painter.createFlowerPot(10, groundLevel + 1, z);
        }
        else {
            painter.set(9, groundLevel + 1, z, stone_slab);
            painter.set(10, groundLevel + 1, z, stone_slab);
            painter.generateChest(9, groundLevel, z, graveChestHooks);
            painter.generateChest(10, groundLevel, z, graveChestHooks);
        }
    }

    private void buildTower() {
        // floor
        painter.fill(10, groundLevel + 1, 10, 2, 0, 2, stonebrick);
        painter.set(10, groundLevel + 1, 11, glowstone);
        painter.fill(10, groundLevel + 2, 10, 2, 0, 2, carpet);

        // walls
        painter.fill(9, groundLevel + 1, 10, 0, 8, 2, stonebrick);
        painter.fill(13, groundLevel + 1, 10, 0, 8, 2, stonebrick);
        painter.fill(10, groundLevel + 1, 9, 2, 8, 0, stonebrick);
        painter.fill(10, groundLevel + 1, 13, 2, 8, 0, stonebrick);

        // Tower door
        final var doorX = 9;
        final var doorZ = 11;
        painter.set(doorX, groundLevel + 2, doorZ, air);
        painter.set(doorX, groundLevel + 3, doorZ, air);
        painter.placeWoodenDoor(9, groundLevel + 2, 11, 2);
    }

    private void buildBasement() {

    }
}
