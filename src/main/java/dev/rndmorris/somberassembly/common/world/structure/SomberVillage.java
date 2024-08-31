package dev.rndmorris.somberassembly.common.world.structure;

import static net.minecraft.init.Blocks.flower_pot;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.common.world.LootGeneration;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public abstract class SomberVillage extends Village {

    protected int averageGroundLevel = -1;
    protected Painter painter;

    public SomberVillage(StructureVillagePieces.Start start, int componentType, Random random,
        StructureBoundingBox boundingBox, int coordBaseMode) {
        super(start, componentType);
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = boundingBox;
    }

    protected void func_143012_a(NBTTagCompound p_143012_1_) {
        super.func_143012_a(p_143012_1_);
        writeToNBT(p_143012_1_);
    }

    protected void func_143011_b(NBTTagCompound p_143011_1_) {
        super.func_143011_b(p_143011_1_);
        readFromNBT(p_143011_1_);
    }

    @Override
    public boolean addComponentParts(World world, Random rand, StructureBoundingBox boundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, boundingBox);
            if (this.averageGroundLevel < 0) {
                return true;
            }
        }

        this.boundingBox.minY = this.averageGroundLevel + this.yOffset();
        this.boundingBox.maxY = this.averageGroundLevel + this.yOffset() + this.structureHeight() - 1;

        painter = new Painter(world, rand);
        buildStructure();

        return true;
    }

    protected TileEntity getTileEntityAtCurrentPosition(World world, int x, int y, int z) {
        if (isOutsideBoundingBox(x, y, z)) {
            return null;
        }
        int l = this.getXWithOffset(x, z);
        int i = this.getYWithOffset(y);
        int j = this.getZWithOffset(x, z);
        return world.getTileEntity(l, i, j);
    }

    protected int getMetadataWithOffset(Block block, int metadata) {
        if (block == Blocks.oak_stairs || block == Blocks.nether_brick_stairs) {
            if (metadata >= 4) {
                // upside-down stairs are just +4
                return super.getMetadataWithOffset(block, metadata - 4) + 4;
            }
        }
        return super.getMetadataWithOffset(block, metadata);
    }

    protected boolean isOutsideBoundingBox(int x, int y, int z) {
        final var i = getXWithOffset(x, z);
        final var j = getYWithOffset(y);
        final var k = getZWithOffset(x, z);

        return !boundingBox.isVecInside(i, j, k);
    }

    /**
     * The height of the structure
     *
     * @return An integer
     */
    protected abstract int structureHeight();

    /**
     * The 0-indexed relative y-coordinate the structure treats as solid ground.
     */
    protected abstract int groundLevel();

    /**
     * Get a 0-indexed y-coordinate adjusted for the structure's ground level.
     *
     * @param delta The amount above or below ground level to shift.
     * @return The adjusted coordinate.
     */
    protected int groundLevel(int delta) {
        return groundLevel() + delta;
    }

    /**
     * How far above or below the averageGroundLevel + 1 the structure should be built.
     */
    protected abstract int yOffset();

    protected abstract void buildStructure();

    protected abstract void writeToNBT(NBTTagCompound tagCompound);

    protected abstract void readFromNBT(NBTTagCompound tagCompound);

    /**
     * Helper methods to make building less painful
     */
    protected class Painter {

        public final World world;
        public final Random random;

        public Painter(World world, Random random) {
            this.world = world;
            this.random = random;
        }

        /**
         *
         * @param x     The relative x coordinate
         * @param y     The relative y coordinate.
         * @param z     The relative z coordinate.
         * @param block The block to place.
         * @return True if the block was placed, false otherwise.
         */
        public boolean set(int x, int y, int z, Block block) {
            return set(x, y, z, block, 0);
        }

        /**
         *
         * @param x     The relative x coordinate.
         * @param y     The relative y coordinate.
         * @param z     The relative z coordinate.
         * @param block The block to place.
         * @param md    The metadata of the block to place.
         * @return True if the block was placed, false otherwise.
         */
        public boolean set(int x, int y, int z, Block block, int md) {
            if (isOutsideBoundingBox(x, y, z)) {
                return false;
            }
            placeBlockAtCurrentPosition(world, block, md, x, y, z, boundingBox);
            return true;
        }

        /**
         * Place a block in the world.
         *
         * @param x              The relative x coordinate.
         * @param y              The relative y coordinate.
         * @param z              The relative z coordinate.
         * @param blockItemStack The block and damage value to place.
         * @return True if the block was placed, false otherwise.
         */
        public boolean set(int x, int y, int z, ItemStack blockItemStack) {
            final var item = blockItemStack.getItem();
            if (item == null) {
                throw new IllegalArgumentException("blockItemStack.getItem() returned null");
            }
            final var block = Block.getBlockFromItem(item);
            final var metadata = blockItemStack.getItemDamage();
            return set(x, y, z, block, metadata);
        }

        /**
         * Create a chest at the given location, populated by the given chest generation hooks.
         *
         * @param x             The relative x coordinate.
         * @param y             The relative y coordinate.
         * @param z             The relative z coordinate.
         * @param chestGenHooks The hooks used to populate the chest's contents
         * @return True if the block was placed, false otherwise.
         */
        public boolean generateChest(int x, int y, int z, ChestGenHooks chestGenHooks) {
            if (isOutsideBoundingBox(x, y, z)) {
                return false;
            }
            generateStructureChestContents(
                world,
                boundingBox,
                random,
                x,
                y,
                z,
                chestGenHooks.getItems(random),
                chestGenHooks.getCount(random));
            return true;
        }

        /**
         * Place a block in the world, attempt to get its tile entity, then configure it with a callback.
         *
         * @param x        The relative x coordinate.
         * @param y        The relative y coordinate.
         * @param z        The relative z coordinate.
         * @param block    The block to place.
         * @param callback A callback used to configure the created tile entity.
         * @return True if the block was placed, false otherwise.
         */
        public boolean setTileEntity(int x, int y, int z, Block block, TileEntityCallback callback) {
            return setTileEntity(x, y, z, block, 0, callback);
        }

        /**
         * Place a block in the world, attempt to get its tile entity, then configure it with a callback.
         *
         * @param x        The relative x coordinate.
         * @param y        The relative y coordinate.
         * @param z        The relative z coordinate.
         * @param block    The block to place.
         * @param md       The metadata of the block to place.
         * @param callback A callback used to configure the created tile entity.
         * @return True if the block was placed, false otherwise.
         */
        public boolean setTileEntity(int x, int y, int z, Block block, int md, TileEntityCallback callback) {
            if (!set(x, y, z, block, md)) {
                return false;
            }
            if (callback != null) {
                final var tileEntity = getTileEntityAtCurrentPosition(world, x, y, z);
                callback.execute(tileEntity);
            }
            return true;
        }

        /**
         * Place a block in the world, attempt to get its tile entity, then configure it with a callback.
         *
         * @param x              The relative x coordinate.
         * @param y              The relative y coordinate.
         * @param z              The relative z coordinate.
         * @param blockItemStack The block and damage value to place.
         * @param callback       A callback used to configure the created tile entity.
         * @return True if the block was placed, false otherwise.
         */
        public boolean setTileEntity(int x, int y, int z, ItemStack blockItemStack, TileEntityCallback callback) {
            final var item = blockItemStack.getItem();
            if (item == null) {
                throw new IllegalArgumentException("blockItemStack.getItem() returned null");
            }
            final var block = Block.getBlockFromItem(item);
            final var metadata = blockItemStack.getItemDamage();
            return setTileEntity(x, y, z, block, metadata, callback);
        }

        public interface TileEntityCallback {

            void execute(TileEntity tileEntity);
        }

        /**
         * Place one or more blocks in an area.
         *
         * @param x      The starting relative x coordinate.
         * @param y      The starting relative y coordinate.
         * @param z      The starting relative z coordinate.
         * @param deltaX The number of additional blocks to place along the x-axis. Can be positive, negative, or zero.
         * @param deltaY The number of additional blocks to place along the y-axis. Can be positive, negative, or zero.
         * @param deltaZ The number of additional blocks to place along the z-axis. Can be positive, negative, or zero.
         * @param block  The block to place.
         */
        public void fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, Block block) {
            fill(x, y, z, deltaX, deltaY, deltaZ, block, 0);
        }

        /**
         * Place one or more blocks in an area.
         *
         * @param x      The starting relative x coordinate.
         * @param y      The starting relative y coordinate.
         * @param z      The starting relative z coordinate.
         * @param deltaX The number of additional blocks to place along the x-axis. Can be positive, negative, or zero.
         * @param deltaY The number of additional blocks to place along the y-axis. Can be positive, negative, or zero.
         * @param deltaZ The number of additional blocks to place along the z-axis. Can be positive, negative, or zero.
         * @param block  The block to place.
         * @param md     The metadata of the block to place.
         */
        public void fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, Block block, int md) {
            final var x2 = x + deltaX;
            final var y2 = y + deltaY;
            final var z2 = z + deltaZ;

            final var xStart = Integer.min(x, x2);
            final var xEnd = Integer.max(x, x2);
            final var yStart = Integer.min(y, y2);
            final var yEnd = Integer.max(y, y2);
            final var zStart = Integer.min(z, z2);
            final var zEnd = Integer.max(z, z2);

            for (var xx = xStart; xx <= xEnd; ++xx) {
                for (var yy = yStart; yy <= yEnd; ++yy) {
                    for (var zz = zStart; zz <= zEnd; ++zz) {
                        set(xx, yy, zz, block, md);
                    }
                }
            }
        }

        /**
         * Place one or more blocks in an area.
         *
         * @param x              The starting relative x coordinate.
         * @param y              The starting relative y coordinate.
         * @param z              The starting relative z coordinate.
         * @param deltaX         The number of additional blocks to place along the x-axis. Can be positive, negative,
         *                       or zero.
         * @param deltaY         The number of additional blocks to place along the y-axis. Can be positive, negative,
         *                       or zero.
         * @param deltaZ         The number of additional blocks to place along the z-axis. Can be positive, negative,
         *                       or zero.
         * @param blockItemStack The block and damage value to place.
         */
        public void fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, ItemStack blockItemStack) {
            final var item = blockItemStack.getItem();
            if (item == null) {
                throw new IllegalArgumentException("blockItemStack.getItem() returned null");
            }
            final var block = Block.getBlockFromItem(item);
            final var metadata = blockItemStack.getItemDamage();
            fill(x, y, z, deltaX, deltaY, deltaZ, block, metadata);
        }

        /**
         * Create a flower pot with a random flower at the given coordinates.
         *
         * @param x The relative x coordinate.
         * @param y The relative y coordinate.
         * @param z The relative z coordinate.
         * @return True if the block was placed, false otherwise.
         */
        public boolean createFlowerPot(int x, int y, int z) {
            return setTileEntity(x, y, z, flower_pot, (te) -> {
                if (te instanceof TileEntityFlowerPot flowerPot) {
                    final var flower = LootGeneration.randomFlower(painter.random);
                    flowerPot.func_145964_a(flower.getItem(), flower.getItemDamage());
                    flowerPot.markDirty();
                }
            });
        }

        public void placeWoodenDoor(int x, int y, int z, int metadata) {
            placeDoorAtCurrentPosition(world, boundingBox, random, x, y, z, metadata);
        }

        public boolean createAuraNode(int x, int y, int z, boolean silverwood, boolean eerie, boolean small) {
            if (isOutsideBoundingBox(x, y, z)) {
                return false;
            }
            final var i = getXWithOffset(x, z);
            final var j = getYWithOffset(y);
            final var k = getZWithOffset(x, z);
            ThaumcraftWorldGenerator.createRandomNodeAt(world, i, j, k, random, silverwood, eerie, small);
            return true;
        }
    }
}
