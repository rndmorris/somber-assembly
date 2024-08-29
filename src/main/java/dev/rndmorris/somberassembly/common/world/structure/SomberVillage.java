package dev.rndmorris.somberassembly.common.world.structure;

import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;

import java.util.Random;

public abstract class SomberVillage extends Village
{
    protected int averageGroundLevel = -1;

    public SomberVillage(StructureVillagePieces.Start start, int componentType, Random random, StructureBoundingBox boundingBox, int coordBaseMode) {
        super(start, componentType);
        this.coordBaseMode = coordBaseMode;
        this.boundingBox = boundingBox;
    }

    /**
     * Check that the structure can be with the given average ground level
     * @param world The world in which the structure will be built
     * @param functionBoundingBox The bounding box passed into the `addComponentParts` method, *not* `this.boundingBox`
     * @param structureHeight The height of the structure
     * @param structureHeightShift How far up or down the structure should be shifted.
     * @return True if the average level is workable, false otherwise.
     */
    protected boolean canWorkWithAverageGroundLevel(World world, StructureBoundingBox functionBoundingBox, int structureHeight, int structureHeightShift) {
        if (this.averageGroundLevel < 0) {
            this.averageGroundLevel = this.getAverageGroundLevel(world, functionBoundingBox);
            if (this.averageGroundLevel < 0) {
                return false;
            }
        }

        this.boundingBox.offset(0, this.averageGroundLevel - this.boundingBox.maxY + structureHeight - 1 + structureHeightShift, 0);
        return true;
    }

    protected class Painter {
        private final World world;
        private final StructureBoundingBox boundingBox;

        public Painter(World world, StructureBoundingBox boundingBox) {
            this.world = world;
            this.boundingBox = boundingBox;
        }

        public Painter set(int x, int y, int z, Block block) {
            return set(x, y, z, block, 0);
        }
        public Painter set(int x, int y, int z, Block block, int md) {
            placeBlockAtCurrentPosition(world, block, md, x, y, z, boundingBox);
            return this;
        }

        public Painter fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, Block block) {
            return fill(x, y, z, deltaX, deltaY, deltaZ, block, 0);
        }

        public Painter fill(int x, int y, int z, int deltaX, int deltaY, int deltaZ, Block block, int md) {
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
            return this;
        }
    }
}
