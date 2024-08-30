package dev.rndmorris.somberassembly.common.world;

import java.util.List;
import java.util.Random;

import dev.rndmorris.somberassembly.common.world.structure.VillageGraveyardLarge;
import net.minecraft.util.MathHelper;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import cpw.mods.fml.common.registry.VillagerRegistry;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.world.structure.VillageGraveyardSmall;

public class VillageGraveyardHandler {

    public static class SmallGraveyard implements VillagerRegistry.IVillageCreationHandler {
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new StructureVillagePieces.PieceWeight(
                VillageGraveyardSmall.class,
                Config.graveyardSmallWeight,
                MathHelper.getRandomIntegerInRange(random, 0, Config.graveyardSmallLimit));
        }

        @Override
        public Class<?> getComponentClass() {
            return VillageGraveyardSmall.class;
        }

        @Override
        public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, Start startPiece, List pieces,
                                     Random random, int x, int y, int z, int coordBaseMode, int componentType) {
            return VillageGraveyardSmall.build(startPiece, pieces, random, x, y, z, coordBaseMode, componentType);
        }
    }

    public static class LargeGraveyard implements VillagerRegistry.IVillageCreationHandler {
        @Override
        public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
            return new StructureVillagePieces.PieceWeight(
                VillageGraveyardLarge.class,
                Config.graveyardLargeWeight,
                MathHelper.getRandomIntegerInRange(random, 0, Config.graveyardLargeLimit));
        }

        @Override
        public Class<?> getComponentClass() {
            return VillageGraveyardLarge.class;
        }

        @Override
        public Object buildComponent(StructureVillagePieces.PieceWeight villagePiece, Start startPiece, List pieces,
                                     Random random, int x, int y, int z, int coordBaseMode, int componentType) {
            return VillageGraveyardLarge.build(startPiece, pieces, random, x, y, z, coordBaseMode, componentType);
        }
    }
}
