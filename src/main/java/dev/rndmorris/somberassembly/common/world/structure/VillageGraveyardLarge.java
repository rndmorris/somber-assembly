package dev.rndmorris.somberassembly.common.world.structure;

import static net.minecraft.init.Blocks.air;
import static net.minecraft.init.Blocks.cobblestone_wall;
import static net.minecraft.init.Blocks.fence;
import static net.minecraft.init.Blocks.fence_gate;
import static net.minecraft.init.Blocks.glass;
import static net.minecraft.init.Blocks.glass_pane;
import static net.minecraft.init.Blocks.glowstone;
import static net.minecraft.init.Blocks.gravel;
import static net.minecraft.init.Blocks.iron_bars;
import static net.minecraft.init.Blocks.log;
import static net.minecraft.init.Blocks.oak_stairs;
import static net.minecraft.init.Blocks.planks;
import static net.minecraft.init.Blocks.stone_brick_stairs;
import static net.minecraft.init.Blocks.stone_slab;
import static net.minecraft.init.Blocks.stonebrick;
import static net.minecraft.init.Blocks.torch;
import static net.minecraft.init.Blocks.wooden_slab;

import java.util.List;
import java.util.Random;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.common.blocks.BlockHelper;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.world.LootGeneration;
import dev.rndmorris.somberassembly.utils.ArrayUtil;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileBanner;

public class VillageGraveyardLarge extends SomberVillage {

    public static final int xLength = 15;
    public static final int height = 18;
    public static final int zLength = 15;

    public static final int yShift = -8;
    private static final int groundLevel = 7;

    private final ChestGenHooks graveChestHooks;

    private final boolean hasBasement;
    private final ItemStack carpet;
    private final Boolean[] graveIsFlowery = new Boolean[8];
    private final Boolean[] coffinGenerated = new Boolean[16];

    public VillageGraveyardLarge(Start start, int componentType, Random random, StructureBoundingBox boundingBox,
        int coodBaseMode) {
        super(start, componentType, random, boundingBox, coodBaseMode);
        graveChestHooks = LootGeneration.graveChestGenHooks(10, 10);
        hasBasement = Config.graveyardLargeBasementFrequency != -1
            && MathHelper.getRandomIntegerInRange(random, 0, Config.graveyardLargeBasementFrequency) == 0;
        carpet = BlockHelper.carpet(random);
        ArrayUtil.fillFromInitializer(graveIsFlowery, i -> random.nextBoolean());
        ArrayUtil.fillFromInitializer(coffinGenerated, i -> false);
    }

    @Override
    protected int structureHeight() {
        return height;
    }

    @Override
    protected int groundLevel() {
        return yShift;
    }

    @Override
    protected int yOffset() {
        return -8;
    }

    @Override
    protected void readFromNBT(NBTTagCompound tagCompound) {

    }

    @Override
    protected void writeToNBT(NBTTagCompound tagCompound) {

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
    public void buildStructure() {
        buildWalls();
        buildWallPosts();
        buildLeftGraves();
        buildRightGraves();

        buildPath();

        buildTower();
        buildTowerRoof();
        buildTowerContents();

        addTorches();

        if (hasBasement) {
            buildBasement();
        }
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
            buildLeftGrave(3 + (zz * 2), graveIsFlowery[zz], zz);
        }
    }

    private void buildLeftGrave(int z, boolean isFlowerGrave, int coffinIndex) {
        painter.set(3, groundLevel + 1, z, stonebrick);
        painter.set(3, groundLevel + 2, z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 0));

        if (isFlowerGrave) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(4, groundLevel, z, coarseDirt);
            painter.set(5, groundLevel, z, coarseDirt);
            if (!coffinGenerated[coffinIndex] && painter.generateChest(4, groundLevel - 1, z, graveChestHooks)) {
                coffinGenerated[coffinIndex] = true;
            }
            if (!coffinGenerated[coffinIndex + 1] && painter.generateChest(5, groundLevel - 1, z, graveChestHooks)) {
                coffinGenerated[coffinIndex + 1] = true;
            }
            painter.createFlowerPot(4, groundLevel + 1, z);
        } else {
            painter.set(4, groundLevel + 1, z, stone_slab);
            painter.set(5, groundLevel + 1, z, stone_slab);
            painter.generateChest(4, groundLevel, z, graveChestHooks);
            painter.generateChest(5, groundLevel, z, graveChestHooks);
        }
    }

    private void buildRightGraves() {
        for (int zz = 0; zz < 3; ++zz) {
            buildRightGrave(3 + (zz * 2), graveIsFlowery[zz + 5]);
        }
    }

    private void buildRightGrave(int z, boolean isFlowerGrave) {
        painter.set(11, groundLevel + 1, z, stonebrick);
        painter.set(11, groundLevel + 2, z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 1));

        if (isFlowerGrave) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(9, groundLevel, z, coarseDirt);
            painter.set(10, groundLevel, z, coarseDirt);
            painter.generateChest(9, groundLevel - 1, z, graveChestHooks);
            painter.generateChest(10, groundLevel - 1, z, graveChestHooks);
            painter.createFlowerPot(10, groundLevel + 1, z);
        } else {
            painter.set(9, groundLevel + 1, z, stone_slab);
            painter.set(10, groundLevel + 1, z, stone_slab);
            painter.generateChest(9, groundLevel, z, graveChestHooks);
            painter.generateChest(10, groundLevel, z, graveChestHooks);
        }
    }

    private void buildPath() {
        painter.fill(6, groundLevel, 0, 2, 0, 1, gravel);
        painter.fill(6, groundLevel, 2, 1, 0, 4, gravel);
        painter.fill(7, groundLevel, 7, 1, 0, 2, gravel);
        painter.set(6, groundLevel, 10, gravel);
        painter.fill(7, groundLevel, 10, 0, 0, 1, gravel);
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
        painter.fill(9, groundLevel + 1, 9, 0, 3, 0, cobblestone_wall);
        painter.fill(13, groundLevel + 1, 13, 0, 3, 0, cobblestone_wall);

        // Tower door
        final var doorX = 9;
        final var doorZ = 11;
        painter.set(doorX, groundLevel + 2, doorZ, air);
        painter.set(doorX, groundLevel + 3, doorZ, air);
        painter.placeWoodenDoor(9, groundLevel + 2, 11, 2);

        // steps
        final var stepsX = 8;
        painter.set(stepsX, groundLevel + 1, 10, oak_stairs, getMetadataWithOffset(oak_stairs, 3));
        painter.set(stepsX, groundLevel + 1, 11, oak_stairs, getMetadataWithOffset(oak_stairs, 0));
        painter.set(stepsX, groundLevel + 1, 12, oak_stairs, getMetadataWithOffset(oak_stairs, 0));

        // windows
        painter.set(11, groundLevel + 3, 9, glass_pane);
        final var windowY = groundLevel + 6;
        painter.fill(9, windowY, 11, 0, 1, 0, glass_pane);
        painter.fill(13, windowY, 11, 0, 1, 0, glass_pane);
        painter.fill(11, windowY, 9, 0, 1, 0, glass_pane);
        painter.fill(11, windowY, 13, 0, 1, 0, glass_pane);
    }

    private void buildTowerRoof() {
        final var stairE = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 0));
        final var stairW = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 1));
        final var stairS = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 2));
        final var stairN = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 3));

        final var stairUE = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 4));
        final var stairUW = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 5));
        final var stairUS = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 6));
        final var stairUN = new ItemStack(oak_stairs, 0, getMetadataWithOffset(oak_stairs, 7));

        var roofLevel = groundLevel + 9;
        painter.fill(9, roofLevel, 8, 4, 0, 0, stairN);
        painter.fill(9, roofLevel, 14, 4, 0, 0, stairS);
        painter.fill(8, roofLevel, 9, 0, 0, 4, stairE);
        painter.fill(14, roofLevel, 9, 0, 0, 4, stairW);
        painter.set(9, roofLevel, 9, planks);
        painter.set(9, roofLevel, 13, planks);
        painter.set(13, roofLevel, 9, planks);
        painter.set(13, roofLevel, 13, planks);

        painter.fill(10, roofLevel, 10, 2, 0, 0, stairUS);
        painter.fill(10, roofLevel, 12, 2, 0, 0, stairUN);
        painter.set(10, roofLevel, 11, stairUW);
        painter.set(12, roofLevel, 11, stairUE);

        roofLevel += 1;
        painter.fill(9, roofLevel, 9, 4, 0, 0, stairN);
        painter.fill(9, roofLevel, 13, 4, 0, 0, stairS);
        painter.fill(9, roofLevel, 10, 0, 0, 2, stairE);
        painter.fill(13, roofLevel, 10, 0, 0, 2, stairW);

        painter.set(10, roofLevel, 10, planks);
        painter.set(12, roofLevel, 10, planks);
        painter.set(10, roofLevel, 12, planks);
        painter.set(12, roofLevel, 12, planks);

        roofLevel += 1;
        painter.fill(10, roofLevel, 10, 2, 0, 0, stairN);
        painter.fill(10, roofLevel, 12, 2, 0, 0, stairS);
        painter.set(10, roofLevel, 11, stairE);
        painter.set(12, roofLevel, 11, stairW);
        painter.set(11, roofLevel, 11, glass);
    }

    private void buildTowerContents() {
        final var altarX = 12;
        painter.set(altarX, groundLevel + 2, 11, BlockHelper.chiseledStoneBrick());
        painter.fill(altarX, groundLevel + 3, 11, 0, 3, 0, fence);
        final var gateMd = getMetadataWithOffset(fence_gate, 3);
        painter.set(altarX, groundLevel + 5, 10, fence_gate, gateMd);
        painter.set(altarX, groundLevel + 5, 12, fence_gate, gateMd);

        final var banner = BlockHelper.Thaumcraft.banner();
        final var setBannerData = new Painter.TileEntityCallback() {

            @Override
            public void execute(TileEntity tileEntity) {
                if (!(tileEntity instanceof TileBanner banner)) {
                    return;
                }
                banner.setColor((byte) 4);
                banner.setAspect(Aspect.LIGHT);
                banner.setWall(true);
                if (coordBaseMode == 0 || coordBaseMode == 2) {
                    banner.setFacing((byte) 4);
                } else {
                    banner.setFacing((byte) 8);
                }
                banner.markDirty();
            }
        };
        painter.setTileEntity(altarX, groundLevel + 4, 10, banner, setBannerData);
        painter.setTileEntity(altarX, groundLevel + 4, 12, banner, setBannerData);
    }

    private void addTorches() {
        final var y = groundLevel + 3;
        painter.set(5, y, 2, torch);
        painter.set(9, y, 2, torch);

        painter.set(5, y, 12, torch);

        painter.set(2, y, 5, torch);
        painter.set(2, y, 9, torch);

        painter.set(12, y, 5, torch);
    }

    private void buildBasement() {

    }
}
