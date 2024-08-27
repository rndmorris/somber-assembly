package dev.rndmorris.somberassembly.recipes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.github.bsideup.jabel.Desugar;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.research.SomberResearch;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;

public class MobAssemblyService {

    private final MobAssemblyServiceConfig config;

    private MobAssemblyService(MobAssemblyServiceConfig config) {
        this.config = config;
    }

    public static MobAssemblyServiceConfig configure() {
        return new MobAssemblyServiceConfig();
    }

    public boolean tryCreateEntity(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        var args = new EventArgs(world, wand, player, x, y, z, side, event);
        if (checkPredicates(args)) {
            teachResearch(args);
            handleWarp(args);
            return spawnEntity(args);
        }
        return true;
    }

    private boolean checkPredicates(EventArgs args) {
        return checkResearch(args) && checkBlockStructure(args) && checkWand(args);
    }

    private boolean checkResearch(EventArgs args) {
        for (var research : this.config.requiredResearch) {
            final var found = ResearchManager.isResearchComplete(args.player.getCommandSenderName(), research);
            SomberAssembly.LOG.info("Found required research {}? {}", research, found);
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean checkBlockStructure(EventArgs args) {
        var world = args.world;
        int x = args.x, y = args.y, z = args.z;
        return checkBlock(world, x, y + 1, z) && checkBlock(world, x, y, z) && checkBlock(world, x, y - 1, z);
    }

    private boolean checkBlock(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var metadata = world.getBlockMetadata(x, y, z);
        return this.config.expectedBlockType.equals(block) && this.config.expectedBlockMetadata == metadata;
    }

    private boolean checkWand(EventArgs args) {
        if (!(args.wand.getItem() instanceof ItemWandCasting castingWand)) {
            return false;
        }
        if (config.wandPredicate != null && !config.wandPredicate.evalutate(args.wand, castingWand)) {
            return false;
        }
        return castingWand.consumeAllVisCrafting(args.wand, args.player, this.config.visCost, false);
    }

    private void teachResearch(EventArgs args) {
        var learnedSomething = false;
        for (String research : this.config.teachesResearch) {
            if (!ResearchManager.isResearchComplete(
                args.player()
                    .getCommandSenderName(),
                research)) {
                Thaumcraft.proxy.researchManager.completeResearch(args.player, research);
                learnedSomething = true;
            }
        }
        if (learnedSomething) {
            SomberAssembly.LOG.info("Learned something, play sounds!");
            SomberAssembly.proxy.playDiscoverySounds(args.player);
        }
    }

    private void handleWarp(EventArgs args) {
        if (this.config.givesWarpPerm > 0) {
            Thaumcraft.addWarpToPlayer(args.player, this.config.givesWarpPerm, false);
        }
        if (this.config.givesWarpSticky > 0) {
            Thaumcraft.addStickyWarpToPlayer(args.player, this.config.givesWarpSticky);
        }
        if (this.config.givesWarpTemp > 0) {
            Thaumcraft.addWarpToPlayer(args.player, this.config.givesWarpTemp, true);
        }
    }

    private boolean spawnEntity(EventArgs args) {
        var world = args.world;
        var player = args.player;
        int x = args.x, y = args.y, z = args.z;

        drainVis(args.wand, player);
        clearBlocks(world, x, y, z);
        doEffects(args);

        if (args.world.isRemote) {
            return false;
        }
        spawnMob(args);
        return true;
    }

    private void drainVis(ItemStack wand, EntityPlayer player) {
        var castingWand = (ItemWandCasting) wand.getItem();
        Objects.requireNonNull(castingWand)
            .consumeAllVisCrafting(wand, player, this.config.visCost, true);
    }

    private void clearBlocks(World world, int x, int y, int z) {
        for (var yy = -1; yy <= 1; ++yy) {
            world.setBlockToAir(x, y + yy, z);
        }
    }

    private void spawnMob(EventArgs args) {
        var newEntity = this.config.createEntityClosure.create(args);
        newEntity.setPosition(args.x + .5D, args.y - 1D, args.z + .5D);
        args.world.spawnEntityInWorld(newEntity);
    }

    private void doEffects(EventArgs args) {
        playShock(args);
        args.player.swingItem();
    }

    private void playShock(EventArgs args) {
        SomberAssembly.proxy.playSoundEffect(
            args.world,
            (double) args.x + 0.5D,
            (double) args.y + 0.5D,
            (double) args.z + 0.5D,
            "Thaumcraft:shock",
            1.0F,
            args.world.rand.nextFloat() * 0.4F + 0.8F);
    }

    @SuppressWarnings("unused")
    private void playSoundAtPlayer(EventArgs args, String soundName) {
        SomberAssembly.proxy.playSoundAtEntity(args.player, soundName, 1.0F, args.world.rand.nextFloat() * 0.4F + 0.8F);
    }

    @Desugar
    public record EventArgs(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {}

    public interface CreateEntity {

        EntityLiving create(EventArgs args);
    }

    public interface WandPredicate {

        boolean evalutate(ItemStack wandItemStack, ItemWandCasting wand);
    }

    public static final class MobAssemblyServiceConfig {

        private Block expectedBlockType;
        private int expectedBlockMetadata;
        private CreateEntity createEntityClosure;
        private final List<String> requiredResearch = new ArrayList<>();
        private final AspectList visCost = new AspectList();
        private final List<String> teachesResearch = new ArrayList<>();
        private int givesWarpPerm;
        private int givesWarpSticky;
        private int givesWarpTemp;
        private WandPredicate wandPredicate;

        private MobAssemblyServiceConfig() {}

        public MobAssemblyService build() {
            return new MobAssemblyService(this);
        }

        public MobAssemblyServiceConfig expectedBlockType(Block expectedBlockType) {
            this.expectedBlockType = expectedBlockType;
            return this;
        }

        public MobAssemblyServiceConfig expectedBlockMetadata(int expectedBlockMetadata) {
            this.expectedBlockMetadata = expectedBlockMetadata;
            return this;
        }

        public MobAssemblyServiceConfig createEntityClosure(CreateEntity createEntityClosure) {
            this.createEntityClosure = createEntityClosure;
            return this;
        }

        public MobAssemblyServiceConfig requiredResearch(SomberResearch.Research... requiredResearch) {
            return requiredResearch(SomberResearch.Research.toStrings(requiredResearch));
        }

        public MobAssemblyServiceConfig requiredResearch(String... requiredResearch) {
            Collections.addAll(this.requiredResearch, requiredResearch);
            return this;
        }

        public MobAssemblyServiceConfig visCost(AspectList visCost) {
            this.visCost.merge(visCost);
            return this;
        }

        public MobAssemblyServiceConfig teachesResearch(SomberResearch.Research... teachesResearch) {
            return this.teachesResearch(SomberResearch.Research.toStrings(teachesResearch));
        }

        public MobAssemblyServiceConfig teachesResearch(String... teachesResearch) {
            Collections.addAll(this.teachesResearch, teachesResearch);
            return this;
        }

        @SuppressWarnings("unused")
        public MobAssemblyServiceConfig givesWarpPerm(int givesWarpPerm) {
            this.givesWarpPerm = givesWarpPerm;
            return this;
        }

        public MobAssemblyServiceConfig givesWarpSticky(int givesWarpSticky) {
            this.givesWarpSticky = givesWarpSticky;
            return this;
        }

        public MobAssemblyServiceConfig givesWarpTemp(int givesWarpTemp) {
            this.givesWarpTemp = givesWarpTemp;
            return this;
        }

        public MobAssemblyServiceConfig wandPredicate(WandPredicate wandPredicate) {
            this.wandPredicate = wandPredicate;
            return this;
        }
    }
}
