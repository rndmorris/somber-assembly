package dev.rndmorris.somberassembly.common.world.structure;

import static net.minecraft.init.Blocks.air;
import static net.minecraft.init.Blocks.bookshelf;
import static net.minecraft.init.Blocks.cobblestone;
import static net.minecraft.init.Blocks.cobblestone_wall;
import static net.minecraft.init.Blocks.fence;
import static net.minecraft.init.Blocks.fence_gate;
import static net.minecraft.init.Blocks.glass;
import static net.minecraft.init.Blocks.glass_pane;
import static net.minecraft.init.Blocks.glowstone;
import static net.minecraft.init.Blocks.gravel;
import static net.minecraft.init.Blocks.iron_bars;
import static net.minecraft.init.Blocks.ladder;
import static net.minecraft.init.Blocks.log;
import static net.minecraft.init.Blocks.nether_brick;
import static net.minecraft.init.Blocks.nether_brick_fence;
import static net.minecraft.init.Blocks.nether_brick_stairs;
import static net.minecraft.init.Blocks.oak_stairs;
import static net.minecraft.init.Blocks.planks;
import static net.minecraft.init.Blocks.redstone_torch;
import static net.minecraft.init.Blocks.stone_brick_stairs;
import static net.minecraft.init.Blocks.stone_slab;
import static net.minecraft.init.Blocks.stonebrick;
import static net.minecraft.init.Blocks.torch;
import static net.minecraft.init.Blocks.trapdoor;
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
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.tiles.TileBanner;

public class VillageGraveyardLarge extends SomberVillage {

    public static final int structureWidth = 15;
    public static final int structureHeight = 19;
    public static final int structureLength = 15;

    private final static int NUMBER_OF_GRAVES = 8;
    private final static String NBT_FLOWERY_GRAVES = "floweryGraves";
    private final static String NBT_FLOWERPOT_CREATED = "flowerpotCreated";
    private final static String NBT_COFFIN_CREATED = "coffinCreated";
    private final static String NBT_HAS_BASEMENT = "hasBasement";
    private final static String NBT_CARPET_COLOR = "carpetColor";

    private final ChestGenHooks graveChestHooks;
    private final boolean[] floweryGraves = new boolean[NUMBER_OF_GRAVES];
    private final boolean[] flowerpotCreated = new boolean[NUMBER_OF_GRAVES];
    private final boolean[] coffinCreated = new boolean[NUMBER_OF_GRAVES * 2];
    private boolean hasBasement;
    private ItemStack carpet;

    public VillageGraveyardLarge(Start start, int componentType, Random random, StructureBoundingBox boundingBox,
        int coodBaseMode) {
        super(start, componentType, random, boundingBox, coodBaseMode);
        graveChestHooks = LootGeneration.graveChestGenHooks(10, 10);
        hasBasement = Config.graveyardLargeBasementFrequency != -1
            && MathHelper.getRandomIntegerInRange(random, 0, Config.graveyardLargeBasementFrequency) == 0;
        carpet = BlockHelper.carpet(random);
        ArrayUtil.fillFromInitializer(floweryGraves, i -> random.nextBoolean());
    }

    @Override
    protected int structureHeight() {
        return structureHeight;
    }

    @Override
    protected int groundLevel() {
        return 7;
    }

    @Override
    protected int yOffset() {
        return -8;
    }

    @Override
    protected void readFromNBT(NBTTagCompound tagCompound) {
        if (tagCompound.hasKey(NBT_CARPET_COLOR)) {
            carpet = BlockHelper.carpet(tagCompound.getByte(NBT_CARPET_COLOR));
        }
        if (tagCompound.hasKey(NBT_HAS_BASEMENT)) {
            hasBasement = tagCompound.getBoolean(NBT_HAS_BASEMENT);
        }

        final var hasFloweryGraves = tagCompound.hasKey(NBT_FLOWERY_GRAVES);
        final var hasFlowerpotsCreated = tagCompound.hasKey(NBT_FLOWERPOT_CREATED);
        final var hasCoffinCreated = tagCompound.hasKey(NBT_COFFIN_CREATED);

        final var dataFloweryGraves = hasFloweryGraves ? tagCompound.getByte(NBT_FLOWERY_GRAVES) : 0;
        final var dataFlowerpotsCreated = hasFlowerpotsCreated ? tagCompound.getByte(NBT_FLOWERPOT_CREATED) : 0;
        final var dataCoffinCreated = hasCoffinCreated ? tagCompound.getShort(NBT_COFFIN_CREATED) : 0;

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

        tagCompound.setByte(NBT_CARPET_COLOR, (byte) carpet.getItemDamage());
        tagCompound.setBoolean(NBT_HAS_BASEMENT, hasBasement);
        tagCompound.setByte(NBT_FLOWERY_GRAVES, (byte) dataFloweryGraves);
        tagCompound.setByte(NBT_FLOWERPOT_CREATED, (byte) dataFlowerpotCreated);
        tagCompound.setShort(NBT_COFFIN_CREATED, (short) dataCoffinCreated);
    }

    public static VillageGraveyardLarge build(StructureVillagePieces.Start start, List<StructureComponent> pieces,
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

        painter.fill(6, groundLevel(4), 1, 2, 0, 0, iron_bars);
        painter.fill(6, groundLevel(5), 1, 2, 0, 0, planks);
    }

    private void buildWall(int x, int z, int deltaX, int deltaZ) {
        painter.fill(x, groundLevel(1), z, deltaX, 0, deltaZ, planks);
        painter.fill(x, groundLevel(2), z, deltaX, 1, deltaZ, iron_bars);
        painter.fill(x, groundLevel(4), z, deltaX, 0, deltaZ, wooden_slab);
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
        painter.fill(x, groundLevel(1), z, 0, 3, 0, log);
        painter.set(x, groundLevel(5), z, wooden_slab);
    }

    private void buildLeftGraves() {
        for (var zz = 0; zz < 5; ++zz) {
            buildLeftGrave(3 + (zz * 2), zz, zz * 2);
        }
    }

    private void buildLeftGrave(int z, int graveIndex, int coffinIndex) {
        painter.set(3, groundLevel(1), z, stonebrick);
        painter.set(3, groundLevel(2), z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 0));

        final var isFlowerVariant = floweryGraves[graveIndex];
        final var coffinY = isFlowerVariant ? groundLevel(-1) : groundLevel();

        if (!coffinCreated[coffinIndex] && painter.generateChest(4, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex] = true;
        }
        if (!coffinCreated[coffinIndex + 1] && painter.generateChest(5, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex + 1] = true;
        }

        if (isFlowerVariant) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(4, groundLevel(), z, coarseDirt);
            painter.set(5, groundLevel(), z, coarseDirt);
            if (!flowerpotCreated[graveIndex] && painter.createFlowerPot(4, groundLevel(1), z)) {
                flowerpotCreated[graveIndex] = true;
            }
        } else {
            painter.set(4, groundLevel(1), z, stone_slab);
            painter.set(5, groundLevel(1), z, stone_slab);
        }
    }

    private void buildRightGraves() {
        for (int zz = 0; zz < 3; ++zz) {
            buildRightGrave(3 + (zz * 2), zz + 5, (zz * 2) + 10);
        }
    }

    private void buildRightGrave(int z, int graveIndex, int coffinIndex) {
        painter.set(11, groundLevel(1), z, stonebrick);
        painter.set(11, groundLevel(2), z, stone_brick_stairs, getMetadataWithOffset(stone_brick_stairs, 1));

        final var isFlowerVariant = floweryGraves[graveIndex];
        final var coffinY = isFlowerVariant ? groundLevel(-1) : groundLevel();

        if (!coffinCreated[coffinIndex] && painter.generateChest(9, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex] = true;
        }
        if (!coffinCreated[coffinIndex + 1] && painter.generateChest(10, coffinY, z, graveChestHooks)) {
            coffinCreated[coffinIndex + 1] = true;
        }

        if (isFlowerVariant) {
            final var coarseDirt = BlockHelper.coarseDirt();
            painter.set(9, groundLevel(), z, coarseDirt);
            painter.set(10, groundLevel(), z, coarseDirt);
            if (!flowerpotCreated[graveIndex] && painter.createFlowerPot(10, groundLevel(1), z)) {
                flowerpotCreated[graveIndex] = true;
            }
        } else {
            painter.set(9, groundLevel(1), z, stone_slab);
            painter.set(10, groundLevel(1), z, stone_slab);
        }
    }

    private void buildPath() {
        painter.fill(6, groundLevel(), 0, 2, 0, 1, gravel);
        painter.fill(6, groundLevel(), 2, 1, 0, 4, gravel);
        painter.fill(7, groundLevel(), 7, 1, 0, 2, gravel);
        painter.set(6, groundLevel(), 10, gravel);
        painter.fill(7, groundLevel(), 10, 0, 0, 1, gravel);
    }

    private void buildTower() {
        // floor
        painter.fill(10, groundLevel(1), 10, 2, 0, 2, stonebrick);
        painter.set(10, groundLevel(1), 11, glowstone);
        painter.fill(10, groundLevel(2), 10, 2, 0, 2, carpet);

        // walls
        painter.fill(9, groundLevel(1), 10, 0, 8, 2, stonebrick);
        painter.fill(13, groundLevel(1), 10, 0, 8, 2, stonebrick);
        painter.fill(10, groundLevel(1), 9, 2, 8, 0, stonebrick);
        painter.fill(10, groundLevel(1), 13, 2, 8, 0, stonebrick);
        painter.fill(9, groundLevel(1), 9, 0, 3, 0, cobblestone_wall);
        painter.fill(13, groundLevel(1), 13, 0, 3, 0, cobblestone_wall);

        // Tower door
        final var doorX = 9;
        final var doorZ = 11;
        painter.set(doorX, groundLevel(2), doorZ, air);
        painter.set(doorX, groundLevel(3), doorZ, air);
        painter.placeWoodenDoor(9, groundLevel(2), 11, 2);

        // steps
        final var stepsX = 8;
        painter.set(stepsX, groundLevel(1), 10, oak_stairs, getMetadataWithOffset(oak_stairs, 3));
        painter.set(stepsX, groundLevel(1), 11, oak_stairs, getMetadataWithOffset(oak_stairs, 0));
        painter.set(stepsX, groundLevel(1), 12, oak_stairs, getMetadataWithOffset(oak_stairs, 0));

        // windows
        painter.set(11, groundLevel(3), 9, glass_pane);
        final var windowY = groundLevel(6);
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

        var roofLevel = groundLevel(9);
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

        final var altarBase = Config.debugTweaks ? new ItemStack(planks, 0, coordBaseMode)
            : BlockHelper.chiseledStoneBrick();

        painter.set(altarX, groundLevel(2), 11, altarBase);
        painter.fill(altarX, groundLevel(3), 11, 0, 3, 0, fence);
        final var gateMd = getMetadataWithOffset(fence_gate, 3);
        painter.set(altarX, groundLevel(5), 10, fence_gate, gateMd);
        painter.set(altarX, groundLevel(5), 12, fence_gate, gateMd);

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
        painter.setTileEntity(altarX, groundLevel(4), 10, banner, setBannerData);
        painter.setTileEntity(altarX, groundLevel(4), 12, banner, setBannerData);
    }

    private void addTorches() {
        final var y = groundLevel(3);
        painter.set(5, y, 2, torch);
        painter.set(9, y, 2, torch);

        painter.set(5, y, 12, torch);

        painter.set(2, y, 5, torch);
        painter.set(2, y, 9, torch);

        painter.set(12, y, 5, torch);
    }

    private void buildBasement() {
        final int xLength = 8, yLength = 4, zLength = 8;
        final int originX = 5, originY = 0, originZ = 5;
        final int endX = originX + xLength, endY = originY + yLength, endZ = originZ + zLength;

        painter.fill(originX, originY, originZ, xLength, yLength, zLength, air);

        // floor
        final var arcaneStoneBricks = BlockHelper.Thaumcraft.arcaneStoneBricks();
        painter.fill(originX, originY, originZ, 8, 0, 8, nether_brick);
        painter.fill(originX + 2, originY, originZ + 2, 0, 0, 4, arcaneStoneBricks);
        painter.fill(originX + 3, originY, originZ + 2 + 4, 3, 0, 0, arcaneStoneBricks);
        painter.set(endX - 2, originY, endZ - 1, arcaneStoneBricks);

        // walls
        final var wallHeight = 3;
        painter.fill(originX, originY + 1, originZ, 0, wallHeight, zLength, nether_brick);
        painter.fill(endX, originY + 1, originZ, 0, wallHeight, zLength, nether_brick);
        painter.fill(originX + 1, originY + 1, originZ, xLength - 2, wallHeight, 0, nether_brick);
        painter.fill(originX + 1, originY + 1, endZ, xLength - 2, wallHeight, 0, nether_brick);

        // wall grating
        final var gratingHeight = 2;
        painter.fill(endX - 1, gratingHeight, originZ, -2, 1, 0, nether_brick_fence);
        painter.fill(endX, gratingHeight, originZ + 1, 0, 1, 2, nether_brick_fence);
        painter.fill(endX, gratingHeight, endZ - 2, 0, 1, 0, nether_brick_fence);
        painter.fill(originX + 3, gratingHeight, endZ, 1, 1, 0, nether_brick_fence);
        painter.fill(originX, gratingHeight, originZ + 2, 0, 1, 0, nether_brick_fence);

        // small room
        painter.fill(endX - 4, originY + 1, originZ + 1, 0, wallHeight, 2, nether_brick);
        painter.fill(endX - 3, originY + 1, originZ + 4, 2, wallHeight, 0, nether_brick);
        painter.fill(endX - 2, originY + 2, originZ + 4, 0, 1, 0, air);

        // ceiling
        painter.fill(originX + 4, originY + 4, originZ, 3, 0, 3, nether_brick);
        final var cobbleY = originY + 5;
        painter.fill(originX + 1, cobbleY, originZ + 1, 2, 0, 6, cobblestone);
        painter.fill(originX + 4, cobbleY, originZ + 4, 0, 0, 3, cobblestone);
        painter.fill(originX + 5, cobbleY, originZ + 5, 2, 0, 2, cobblestone);
        painter.set(endX - 2, cobbleY, endZ, cobblestone);

        // ladder
        final int shaftX = endX - 2, shaftZ = endZ - 1;
        painter.fill(shaftX - 1, cobbleY + 1, shaftZ, 2, 2, 0, cobblestone);
        painter.fill(shaftX, cobbleY + 1, shaftZ - 1, 0, 2, 2, cobblestone);
        painter.fill(shaftX, endY, shaftZ, 0, 4, 0, air);
        painter.fill(shaftX, originY + 1, shaftZ, 0, 6, 0, ladder, getMetadataWithOffset(ladder, 3));
        painter.set(shaftX, groundLevel(1), shaftZ, trapdoor, getMetadataWithOffset(trapdoor, 9));

        // decorations
        painter.set(originX + 4, originY + 1, originZ + 4, BlockHelper.Thaumcraft.arcaneWorktable());
        painter.set(originX + 2, originY + 1, originZ + 1, BlockHelper.Thaumcraft.crucible());
        painter.set(originX + 1, originY + 1, originZ + 5, ItemApi.getBlock("blockTable", 2));
        painter.set(originX + 1, originY + 1, originZ + 6, ItemApi.getBlock("blockTable", 7));
        painter.setTileEntity(endX - 2, originY + 2, endZ - 3, BlockHelper.Thaumcraft.banner(), te -> {
            if (!(te instanceof TileBanner banner)) {
                return;
            }
            banner.setWall(true);
            banner.setColor((byte) 15);
            banner.setAspect(Aspect.UNDEAD);
            final byte facing = switch (coordBaseMode) {
                case 0 -> 0; //
                case 1 -> 4;
                case 2 -> 8;
                case 3 -> 12; //
                default -> -1;
            };
            banner.setFacing(facing);
            banner.markDirty();
        });

        painter.fill(originX + 1, originY + 1, originZ + 4, 0, 3, 0, bookshelf);
        painter.fill(originX + 1, originY + 1, originZ + 7, 0, 3, 0, bookshelf);
        painter.fill(originX + 1, originY + 4, originZ + 5, 0, 0, 1, bookshelf);
        painter.fill(originX + 1, originY + 3, originZ + 5, 0, 0, 1, oak_stairs, getMetadataWithOffset(oak_stairs, 5));
        painter.set(originX + 1, originY + 2, originZ + 6, redstone_torch, 5);

        final var brickStairE = new ItemStack(nether_brick_stairs, 0, getMetadataWithOffset(nether_brick_stairs, 4));
        final var brickStairW = new ItemStack(nether_brick_stairs, 0, getMetadataWithOffset(nether_brick_stairs, 5));
        final var brickStairS = new ItemStack(nether_brick_stairs, 0, getMetadataWithOffset(nether_brick_stairs, 6));
        final var brickStairN = new ItemStack(nether_brick_stairs, 0, getMetadataWithOffset(nether_brick_stairs, 7));
        final var stairY = originY + 4;

        painter.set(originX + 1, stairY, originZ + 1, brickStairW);
        painter.set(originX + 1, stairY, originZ + 3, brickStairW);
        painter.set(originX + 3, stairY, originZ + 1, brickStairE);
        painter.set(originX + 3, stairY, originZ + 3, brickStairE);

        painter.set(endX - 1, stairY, endZ - 1, brickStairN);
        painter.set(endX - 3, stairY, endZ - 1, brickStairN);
        painter.set(endX - 5, stairY, endZ - 1, brickStairN);

        painter.set(endX - 1, stairY, endZ - 3, brickStairS);
        painter.set(endX - 3, stairY, endZ - 3, brickStairS);

        final int nodeX = originX + 2, nodeY = originY + 3, nodeZ = originZ + 2;
        painter.createAuraNode(nodeX, nodeY, nodeZ, false, true, true);
    }
}
