package dev.rndmorris.somberassembly.common.blocks;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.mixins.interfaces.IGameRules;
import dev.rndmorris.somberassembly.utils.CollectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.Random;

import static dev.rndmorris.somberassembly.common.configs.GameRules.DO_VIRAL_ICE_TICK;
import static dev.rndmorris.somberassembly.common.configs.GameRules.VIRAL_ICE_TICK_RATE;
import static dev.rndmorris.somberassembly.common.configs.GameRules.VIRAL_ICE_TICK_RATE_VARIATION;

public class BlockViralIce extends SomberBlock {

    public static final String NAME = "block_viral_ice";

    public BlockViralIce() {
        super(Material.packedIce);

        slipperiness = 0.98F;
        setHardness(0.5F);
        setResistance(10.0F);
        setStepSound(soundTypeGlass);
        setBlockNamePrefixed(NAME);
        setBlockTextureName("ice_packed");
        setTickRandomly(true);
    }

    @Override
    public int tickRate(World worldIn) {
        final var gameRules = (IGameRules) worldIn.getGameRules();
        return gameRules.somber_assembly$getGameRuleIntegerValue(VIRAL_ICE_TICK_RATE, 30);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        final var gameRules = (IGameRules) worldIn.getGameRules();
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn) + gameRules.somber_assembly$getGameRuleIntegerValue(VIRAL_ICE_TICK_RATE_VARIATION, 10));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.getGameRules().getGameRuleBooleanValue(DO_VIRAL_ICE_TICK)) {
            return;
        }
        if (!canSpread(worldIn, x, y, z)) {
            SomberAssembly.LOG.info("Viral Ice at ({}, {}, {}) can spread no further.", x, y, z);
            worldIn.setBlock(x, y, z, Blocks.ice);
            return;
        }

        final var dir = CollectionUtil.randomElement(random, ForgeDirection.VALID_DIRECTIONS);
        final int i = x + dir.offsetX, j = y + dir.offsetY, k = z + dir.offsetZ;
        final var spreadToBlock = worldIn.getBlock(i, j, k);

        SomberAssembly.LOG.info("Viral Ice at ({}, {}, {}) attempting to spread {} to ({}, {}, {}). Water found? {}", x, y, z, dir, i, j, k, spreadToBlock == Blocks.water);

        if (canSpreadTo(spreadToBlock)) {
            worldIn.setBlock(i, j, k, SomberBlock.viralIceBlock);
        }
    }

    private static boolean canSpread(World worldIn, int x, int y, int z) {
        for (var dir : ForgeDirection.values()) {
            final var blockAt = worldIn.getBlock(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (canSpreadTo(blockAt)) {
                return true;
            }
        }
        return false;
    }

    private static boolean canSpreadTo(Block block) {
        return Blocks.water.equals(block);
    }
}
