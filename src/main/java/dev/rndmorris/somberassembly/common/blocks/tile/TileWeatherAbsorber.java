package dev.rndmorris.somberassembly.common.blocks.tile;

import java.util.Objects;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.util.ForgeDirection;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.service.IWeatherMonitor;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;

public class TileWeatherAbsorber extends TileThaumcraft implements IEssentiaTransport {

    private static final Aspect ASPECT = Aspect.WEATHER;
    private static final ForgeDirection FACING = ForgeDirection.DOWN;
    private static final String ESSENTIA = "storedEssentia";

    private static final int rainGenerationMax = 32;
    private static final int stormGenerationMax = 64;
    private int storedEssentia = 0;

    @Override
    public void updateEntity() {

        final var worldInfo = worldObj.getWorldInfo();
        if (!shouldGenerateEssentia(worldInfo)) {
            return;
        }
        final var weatherMonitor = Objects.requireNonNull(SomberAssembly.proxy.getService(IWeatherMonitor.class));

        final float generationPotential = worldInfo.isThundering() ? stormGenerationMax : rainGenerationMax;
        final var futureDuration = worldInfo.getRainTime();
        final var totalRainDuration = worldInfo.getWorldTotalTime() - weatherMonitor.getLastRainStartTime()
            + futureDuration;
        final var percentRainComplete = (float) (totalRainDuration - futureDuration) / (float) totalRainDuration;

        this.storedEssentia = (int) (generationPotential * percentRainComplete);

        worldInfo.setRaining(false);
        worldInfo.setRainTime(0);
        worldInfo.setThundering(false);
        worldInfo.setThunderTime(0);
    }

    private boolean shouldGenerateEssentia(WorldInfo worldInfo) {
        return storedEssentia == 0 && worldInfo.isRaining() && isRedstonePowered() && worldObj.canBlockSeeTheSky(xCoord, yCoord + 1, zCoord);
    }

    private boolean isRedstonePowered() {
        return worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord);
    }

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

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        if (nbtTagCompound.hasKey(ESSENTIA)) {
            this.storedEssentia = nbtTagCompound.getInteger(ESSENTIA);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbtTagCompound) {
        super.writeToNBT(nbtTagCompound);
        nbtTagCompound.setInteger(ESSENTIA, storedEssentia);
    }
}
