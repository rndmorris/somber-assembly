package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.common.blocks.tile.TileWeatherAbsorber;

public class BlockMachine extends SomberBlock {

    public static final String NAME = "machine";

    public static final int WEATHER_ABSORBER_METADATA = 0;

    protected BlockMachine() {
        super(Material.rock);
        setResistance(10.0F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        setTickRandomly(true);
    }

    @Override
    public String getPlainName() {
        return NAME;
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return switch (metadata) {
            case WEATHER_ABSORBER_METADATA -> new TileWeatherAbsorber();
            default -> null;
        };
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return switch (metadata) {
            case WEATHER_ABSORBER_METADATA -> true;
            default -> false;
        };
    }
}
