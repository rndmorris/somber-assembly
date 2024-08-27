package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import com.github.bsideup.jabel.Desugar;

import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;

public class WandNecromancyManager {

    private final Block expectedBlockType;
    private final int expectedBlockMetadata;
    private final CreateEntity createEntityClosure;
    private final String requiredResearch;
    private final AspectList visCost;

    public WandNecromancyManager(Block expectedBlockType, int expectedBlockMetadata, CreateEntity createEntityClosure,
        String requiredResearch, AspectList visCost) {
        this.expectedBlockType = expectedBlockType;
        this.expectedBlockMetadata = expectedBlockMetadata;
        this.createEntityClosure = createEntityClosure;
        this.requiredResearch = requiredResearch;
        this.visCost = visCost;
    }

    public boolean tryCreateEntity(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        var args = new WandNecromancyManager.TriggerEventArgs(world, wand, player, x, y, z, side, event);
        if (checkPredicates(args)) {
            return spawnEntity(args);
        }
        return true;
    }

    private boolean checkPredicates(TriggerEventArgs args) {
        return checkResearch(args) && checkBlockStructure(args) && checkWand(args);
    }

    private boolean checkResearch(TriggerEventArgs args) {
        if (this.requiredResearch == null) {
            return true;
        }
        return ResearchManager.isResearchComplete(args.player.getCommandSenderName(), this.requiredResearch);
    }

    private boolean checkBlockStructure(TriggerEventArgs args) {
        var world = args.world;
        int x = args.x, y = args.y, z = args.z;
        return checkBlock(world, x, y, z) && checkBlock(world, x, y - 1, z);
    }

    private boolean checkBlock(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var metadata = world.getBlockMetadata(x, y, z);
        return this.expectedBlockType.equals(block) && this.expectedBlockMetadata == metadata;
    }

    private boolean checkWand(TriggerEventArgs args) {
        if (!(args.wand.getItem() instanceof ItemWandCasting castingWand)) {
            return false;
        }
        if (this.visCost == null) {
            return true;
        }
        return castingWand.consumeAllVisCrafting(args.wand, args.player, this.visCost, false);
    }

    private boolean spawnEntity(TriggerEventArgs args) {
        var world = args.world;
        var player = args.player;
        int x = args.x, y = args.y, z = args.z;

        if (args.world.isRemote) {
            return false;
        }

        drainVis(args.wand, player);
        clearBlocks(world, x, y, z);
        spawnMob(args);
        doEffects(world, player, x, y, z);
        return true;
    }

    private void drainVis(ItemStack wand, EntityPlayer player) {
        if (this.visCost != null) {
            assert wand.getItem() != null;
            var castingWand = (ItemWandCasting) wand.getItem();
            castingWand.consumeAllVisCrafting(wand, player, this.visCost, true);
        }
    }

    private void clearBlocks(World world, int x, int y, int z) {
        world.setBlockToAir(x, y, z);
        world.setBlockToAir(x, y - 1, z);
    }

    private void spawnMob(TriggerEventArgs args) {
        var newEntity = this.createEntityClosure.create(args);
        newEntity.setPosition(args.x + .5D, args.y - 1D, args.z + .5D);
        args.world.spawnEntityInWorld(newEntity);
    }

    private void doEffects(World world, EntityPlayer player, int x, int y, int z) {
        world.playSoundEffect(
            (double) x + 0.5D,
            (double) y + 0.5D,
            (double) z + 0.5D,
            "Thaumcraft:shock",
            1.0F,
            world.rand.nextFloat() * 0.4F + 0.8F);
        player.swingItem();
    }

    @Desugar
    public record TriggerEventArgs(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {}

    public interface CreateEntity {

        EntityLiving create(TriggerEventArgs args);
    }
}
