package dev.rndmorris.somberassembly.common.world.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.common.ChestGenHooks;

import dev.rndmorris.somberassembly.SomberAssembly;

public abstract class SomberVillage extends Village {

    protected int averageGroundLevel = -1;

    public SomberVillage(StructureVillagePieces.Start start, int componentType, Random random,
        StructureBoundingBox boundingBox, int coordBaseMode) {
        super(start, componentType);
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = boundingBox;
    }

    /**
     * Check that the structure can be with the given average ground level
     *
     * @param world                The world in which the structure will be built
     * @param functionBoundingBox  The bounding box passed into the `addComponentParts` method, *not* `this.boundingBox`
     * @param structureHeight      The height of the structure
     * @param structureHeightShift How far up or down the structure should be shifted.
     * @return True if the average level is workable, false otherwise.
     */
    protected boolean canWorkWithAverageGroundLevel(World world, StructureBoundingBox functionBoundingBox,
        int structureHeight, int structureHeightShift) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, functionBoundingBox);
            if (this.averageGroundLevel < 0) {
                return false;
            }
        }

        this.boundingBox
            .offset(0, this.averageGroundLevel - this.boundingBox.maxY + structureHeight - 1 + structureHeightShift, 0);
        return true;
    }

    @Override
    protected int getMetadataWithOffset(Block block, int offset) {
        if (block == Blocks.rail || block == Blocks.wooden_door
            || block == Blocks.iron_door
            || block == Blocks.stone_stairs
            || block == Blocks.oak_stairs
            || block == Blocks.nether_brick_stairs
            || block == Blocks.stone_brick_stairs
            || block == Blocks.sandstone_stairs
            || block == Blocks.ladder
            || block == Blocks.stone_button
            || block == Blocks.tripwire_hook
            || block == Blocks.piston
            || block == Blocks.sticky_piston
            || block == Blocks.lever
            || block == Blocks.dispenser) {
            return super.getMetadataWithOffset(block, offset);
        }

        if (block == Blocks.torch || block == Blocks.redstone_torch || block == Blocks.unlit_redstone_torch) {
            final var result = switch (this.coordBaseMode) {
                case 0 -> switch (offset) {
                        default -> offset;
                    };
                case 1 -> switch (offset) {
                        case 4 -> 4;
                        default -> offset;
                    };
                case 2 -> switch (offset) {
                        case 4 -> 2;
                        default -> offset;
                    };
                case 3 -> switch (offset) {
                        case 4 -> 4;
                        default -> offset;
                    };
                default -> offset;
            };
            SomberAssembly.LOG
                .info("Placing torch. coordBaseMode {}, offset {}, result {}", this.coordBaseMode, offset, result);
            return result;
        }
        return offset;
    }

    protected class Painter {

        private final World world;
        private final StructureBoundingBox boundingBox;
        private final Random random;

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
    }
}
