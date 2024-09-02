package dev.rndmorris.somberassembly.common.world.structure;

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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.common.blocks.BlockHelper;
import dev.rndmorris.somberassembly.common.world.LootGeneration;
import dev.rndmorris.somberassembly.utils.ArrayUtil;

public class VillageGraveyardSmall extends SomberVillage {

    public static final int structureWidth = 9;
    public static final int structureHeight = 6;
    public static final int structureLength = 9;
    public static final int groundLevel = 1;

    private final static int NUMBER_OF_GRAVES = 3;
    private final static String NBT_FLOWERY_GRAVES = "floweryGraves";
    private final static String NBT_FLOWERPOT_CREATED = "flowerpotCreated";
    private final static String NBT_COFFIN_CREATED = "coffinCreated";

    private final ChestGenHooks graveChestHooks;
    private final boolean[] floweryGraves = new boolean[NUMBER_OF_GRAVES];
    private final boolean[] flowerpotCreated = new boolean[NUMBER_OF_GRAVES];
    private final boolean[] coffinCreated = new boolean[NUMBER_OF_GRAVES * 2];

    public VillageGraveyardSmall(Start start, int componentType, Random random, StructureBoundingBox boundingBox,
        int coordBaseMode) {
        super(start, componentType, random, boundingBox, coordBaseMode);
        graveChestHooks = LootGeneration.graveChestGenHooks(10, 10);
        ArrayUtil.fillFromInitializer(floweryGraves, i -> random.nextBoolean());
    }

    @Override
    public int structureHeight() {
        return structureHeight;
    }

    @Override
    public int groundLevel() {
        return groundLevel;
    }

    @Override
    protected int yOffset() {
        return -2;
    }

    @Override
    protected void readFromNBT(NBTTagCompound tagCompound) {
        final var hasFloweryGraves = tagCompound.hasKey(NBT_FLOWERY_GRAVES);
        final var hasFlowerpotsCreated = tagCompound.hasKey(NBT_FLOWERPOT_CREATED);
        final var hasCoffinCreated = tagCompound.hasKey(NBT_COFFIN_CREATED);

        final var dataFloweryGraves = hasFloweryGraves ? tagCompound.getByte(NBT_FLOWERY_GRAVES) : 0;
        final var dataFlowerpotsCreated = hasFlowerpotsCreated ? tagCompound.getByte(NBT_FLOWERPOT_CREATED) : 0;
        final var dataCoffinCreated = hasCoffinCreated ? tagCompound.getByte(NBT_COFFIN_CREATED) : 0;

        if (!(hasFloweryGraves || hasFlowerpotsCreated || hasCoffinCreated)) {
            return;
        }

        for (var index = 0; index < NUMBER_OF_GRAVES; ++index) {
            final var bitIndex = 1 << index;
            if (hasFloweryGraves) {
                floweryGraves[index] = (dataFloweryGraves & bitIndex) == bitIndex;
            }
            if (hasFlowerpotsCreated) {
                flowerpotCreated[index] = (dataFlowerpotsCreated & bitIndex) == bitIndex;
            }
            if (hasCoffinCreated) {
                final var cIndex = index * 2;
                var coffinBitIndex = 1 << (cIndex);
                coffinCreated[cIndex] = (dataCoffinCreated & coffinBitIndex) == coffinBitIndex;

                coffinBitIndex = 1 << (cIndex + 1);
                coffinCreated[cIndex + 1] = (dataCoffinCreated & coffinBitIndex) == coffinBitIndex;
            }
        }
    }

    @Override
    protected void writeToNBT(NBTTagCompound tagCompound) {
        var dataFloweryGraves = 0;
        var dataFlowerpotCreated = 0;
        var dataCoffinCreated = 0;

        for (var index = 0; index < NUMBER_OF_GRAVES; ++index) {
            dataFloweryGraves = dataFloweryGraves | ((floweryGraves[index] ? 1 : 0) << index);
            dataFlowerpotCreated = dataFlowerpotCreated | ((flowerpotCreated[index] ? 1 : 0) << index);

            var cIndex = index * 2;
            dataCoffinCreated = dataCoffinCreated | ((coffinCreated[cIndex] ? 1 : 0) << cIndex);
            cIndex += 1;
            dataCoffinCreated = dataCoffinCreated | ((coffinCreated[cIndex] ? 1 : 0) << cIndex);
        }

        tagCompound.setByte(NBT_FLOWERY_GRAVES, (byte) dataFloweryGraves);
        tagCompound.setByte(NBT_FLOWERPOT_CREATED, (byte) dataFlowerpotCreated);
        tagCompound.setByte(NBT_COFFIN_CREATED, (byte) dataCoffinCreated);
    }

    public static VillageGraveyardSmall build(StructureVillagePieces.Start start, List<StructureComponent> pieces,
        Random rand, int x, int y, int z, int coordBaseMode, int componentType) {
        StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(
            x,
            y,
            z,
            0,
            0,
            0,
            structureWidth,
            structureHeight,
            structureLength,
            coordBaseMode);
        return canVillageGoDeeper(structureboundingbox)
            && StructureComponent.findIntersecting(pieces, structureboundingbox) == null
                ? new VillageGraveyardSmall(start, componentType, rand, structureboundingbox, coordBaseMode)
                : null;
    }

    protected void buildStructure() {
        buildWalls();
        buildPosts();
        buildGraves();
        buildPath();

        painter.set(6, 3, 4, torch, 0);
    }

    private void buildWalls() {
        buildWall(1, 1, 3, 0);
        buildWall(1, 7, 5, 0);
        buildWall(1, 1, 0, 5);
        buildWall(7, 1, 0, 5);
    }

    private void buildWall(int x, int z, int deltaX, int deltaZ) {
        painter.fill(x, groundLevel(1), z, deltaX, 0, deltaZ, planks);
        painter.fill(x, groundLevel(2), z, deltaX, 0, deltaZ, iron_bars);
        painter.fill(x, groundLevel(3), z, deltaX, 0, deltaZ, wooden_slab);
    }

    private void buildPosts() {
        for (var xx = 0; xx < 3; ++xx) {
            for (var zz = 0; zz < 3; ++zz) {
                if (xx == 1 && zz == 1) {
                    continue;
                }
                final var x = 1 + (xx * 3);
                final var z = 1 + (zz * 3);
                buildPost(x, z);
            }
        }
    }

    private void buildPost(int x, int z) {
        painter.fill(x, groundLevel(1), z, 0, 2, 0, log);
        painter.set(x, groundLevel(4), z, wooden_slab);
    }

    private void buildGraves() {
        for (var zz = 0; zz < 3; ++zz) {
            buildGrave(2 + (zz * 2), zz, zz * 2);
        }
    }

    private void buildGrave(int z, int graveIndex, int coffinIndex) {
        painter.set(2, groundLevel(1), z, stonebrick);
        painter.set(2, groundLevel(2), z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 0));

        final var isFlowerVariant = floweryGraves[graveIndex];

        final var coffinY = isFlowerVariant ? groundLevel(-1) : groundLevel(0);
        if (!coffinCreated[coffinIndex] && painter.generateChest(3, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex] = true;
        }
        if (!coffinCreated[coffinIndex + 1] && painter.generateChest(4, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex + 1] = true;
        }

        if (isFlowerVariant) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(3, groundLevel(), z, coarseDirt);
            painter.set(4, groundLevel(), z, coarseDirt);
            if (!flowerpotCreated[graveIndex] && painter.createFlowerPot(3, groundLevel(1), z)) {
                flowerpotCreated[graveIndex] = true;
            }
        } else {
            painter.set(3, groundLevel(1), z, stone_slab);
            painter.set(4, groundLevel(1), z, stone_slab);
        }
    }

    private void buildPath() {
        painter.fill(5, groundLevel(), 0, 1, 0, 5, gravel);
        painter.set(5, groundLevel(), 6, gravel);
    }
}
