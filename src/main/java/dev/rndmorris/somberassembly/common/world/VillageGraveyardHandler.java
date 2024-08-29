package dev.rndmorris.somberassembly.common.world;

import java.util.List;
import java.util.Random;

import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

import cpw.mods.fml.common.registry.VillagerRegistry;
import dev.rndmorris.somberassembly.common.world.structure.VillageGraveyardSmall;

public class VillageGraveyardHandler implements VillagerRegistry.IVillageCreationHandler {

    @Override
    public StructureVillagePieces.PieceWeight getVillagePieceWeight(Random random, int i) {
        return new StructureVillagePieces.PieceWeight(VillageGraveyardSmall.class, 100, 4);
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
