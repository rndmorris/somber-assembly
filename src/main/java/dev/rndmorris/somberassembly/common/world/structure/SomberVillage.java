package dev.rndmorris.somberassembly.common.world.structure;

import java.util.Random;

import dev.rndmorris.somberassembly.common.world.LootGeneration;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.common.ChestGenHooks;

import static net.minecraft.init.Blocks.flower_pot;
import static net.minecraft.init.Blocks.oak_stairs;

public abstract class SomberVillage extends Village {

    protected int averageGroundLevel = -1;
    protected Painter painter;

    public SomberVillage(StructureVillagePieces.Start start, int componentType, Random random,
        StructureBoundingBox boundingBox, int coordBaseMode) {
        super(start, componentType);
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = boundingBox;
    }

    protected abstract int structureHeight();

    protected abstract int yAdjustment();

    /**
     * Check that the structure can be with the given average ground level
     *
     * @param world               The world in which the structure will be built
     * @param functionBoundingBox The bounding box passed into the `addComponentParts` method, *not* `this.boundingBox`
     * @return True if the average level is workable, false otherwise.
     */
    protected boolean canWorkWithAverageGroundLevel(World world, StructureBoundingBox functionBoundingBox) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, functionBoundingBox);
            if (this.averageGroundLevel < 0) {
                return false;
            }
        }

        this.boundingBox
            .offset(0, this.averageGroundLevel - this.boundingBox.maxY + structureHeight() - 1 + yAdjustment(), 0);
        return true;
    }

    protected TileEntity getTileEntityAtCurrentPosition(World world, int x, int y, int z,
        StructureBoundingBox boundingBox) {
        int l = this.getXWithOffset(x, z);
        int i1 = this.getYWithOffset(y);
        int j1 = this.getZWithOffset(x, z);
        return !boundingBox.isVecInside(l, i1, j1) ? null : world.getTileEntity(l, i1, j1);
    }

    protected int getMetadataWithOffset(Block block, int metadata) {
        if (block == Blocks.oak_stairs) {
            if (metadata >= 4) {
                // upside-down stairs are just +4
                return super.getMetadataWithOffset(block, metadata - 4) + 4;
            }
        }
        return super.getMetadataWithOffset(block, metadata);
    }

    /**
     * Helper methods to make building less painful
     */
    protected class Painter {

        public final World world;
        public final StructureBoundingBox boundingBox;
        public final Random random;

        public Painter(World world, StructureBoundingBox boundingBox, Random random) {
            this.world = world;
            this.boundingBox = boundingBox;
            this.random = random;
        }

        public void set(int x, int y, int z, Block block) {
            set(x, y, z, block, 0);
        }

        public void set(int x, int y, int z, Block block, int md) {
            placeBlockAtCurrentPosition(world, block, md, x, y, z, boundingBox);
        }

        public void set(int x, int y, int z, ItemStack blockItemStack) {
            final var item = blockItemStack.getItem();
            if (item == null) {
                throw new IllegalArgumentException("blockItemStack.getItem() returned null");
            }
            final var block = Block.getBlockFromItem(item);
            final var metadata = blockItemStack.getItemDamage();
            set(x, y, z, block, metadata);
        }

        public void generateChest(int x, int y, int z, ChestGenHooks chestGenHooks) {
            generateStructureChestContents(
                world,
                boundingBox,
                random,
                x,
                y,
                z,
                chestGenHooks.getItems(random),
                chestGenHooks.getCount(random));
        }

        public void setTileEntity(int x, int y, int z, Block block, TileEntityCallback callback) {
            setTileEntity(x, y, z, block, 0, callback);
        }

        public void setTileEntity(int x, int y, int z, Block block, int md, TileEntityCallback callback) {
            set(x, y, z, block, md);
            if (callback != null) {
                final var tileEntity = getTileEntityAtCurrentPosition(world, x, y, z, boundingBox);
                callback.execute(tileEntity);
            }
        }

        public interface TileEntityCallback {

            void execute(TileEntity tileEntity);
        }

        public void fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, Block block) {
            fill(x, y, z, deltaX, deltaY, deltaZ, block, 0);
        }

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
         * Create a flower pot with a random flower at the given coordinates
         */
        public void createFlowerPot(int x, int y, int z) {
            setTileEntity(x, y, z, flower_pot, (te) -> {
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
    }
}
