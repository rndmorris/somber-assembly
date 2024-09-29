package dev.rndmorris.somberassembly.common.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.common.blocks.tile.TileWeatherAbsorber;

public class BlockMachine extends SomberBlock {

    public static final int WEATHER_ABSORBER_METADATA = 0;

    protected BlockMachine() {
        super(Material.rock);
        this.setResistance(10.0F);
        this.setHardness(2.0F);
        this.setStepSound(soundTypeStone);
        this.setTickRandomly(true);
    }

    @Override
    public TileEntity createTileEntity(World world, int metadata) {
        return switch (metadata) {
            case WEATHER_ABSORBER_METADATA -> new TileWeatherAbsorber();
            default -> null;
        };
    }
}
