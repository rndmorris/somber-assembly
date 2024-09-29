package dev.rndmorris.somberassembly.common.blocks.tile;

import net.minecraftforge.common.util.ForgeDirection;

import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileWeatherAbsorber extends TileThaumcraft implements IEssentiaTransport {

    static final Aspect ASPECT = Aspect.WEATHER;
    static final ForgeDirection FACING = ForgeDirection.DOWN;

    private int storedEssentia = 0;

    @Override
    public boolean isConnectable(ForgeDirection direction) {
        return direction == FACING;
    }

    @Override
    public boolean canInputFrom(ForgeDirection direction) {
        return false;
    }

    @Override
    public boolean canOutputTo(ForgeDirection direction) {
        return direction == FACING;
    }

    @Override
    public void setSuction(Aspect var1, int var2) {}

    @Override
    public Aspect getSuctionType(ForgeDirection direction) {
        return null;
    }

    @Override
    public int getSuctionAmount(ForgeDirection direction) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, ForgeDirection direction) {
        if (aspect == ASPECT) {
            final var take = Math.min(this.storedEssentia, amount);
            this.storedEssentia -= take;
            this.markDirty();
            return take;
        }
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, ForgeDirection direction) {
        return 0;
    }

    @Override
    public Aspect getEssentiaType(ForgeDirection direction) {
        return direction == FACING ? ASPECT : null;
    }

    @Override
    public int getEssentiaAmount(ForgeDirection direction) {
        if (direction == FACING) {
            return storedEssentia;
        }
        return 0;
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public boolean renderExtendedTube() {
        return false;
    }
}
