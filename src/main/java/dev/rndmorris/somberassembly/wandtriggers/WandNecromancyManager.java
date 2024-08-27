package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

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

    public boolean tryCreateEntity(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int event) {
        if (checkPredicates(world, wand, player, x, y, z, event)) {
            return spawnEntity(world, wand, player, x, y, z);
        }
        return true;
    }

    private boolean checkPredicates(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int event) {
        return event == 1 && checkResearch(player) && checkBlockStructure(world, x, y, z) && checkWand(wand, player);
    }

    private boolean checkResearch(EntityPlayer player) {
        if (this.requiredResearch == null) {
            return true;
        }
        return ResearchManager.isResearchComplete(player.getCommandSenderName(), this.requiredResearch);
    }

    private boolean checkBlockStructure(World world, int x, int y, int z) {
        return checkBlock(world, x, y, z) && checkBlock(world, x, y - 1, z);
    }

    private boolean checkBlock(World world, int x, int y, int z) {
        var block = world.getBlock(x, y, z);
        var metadata = world.getBlockMetadata(x, y, z);
        return this.expectedBlockType.equals(block) && this.expectedBlockMetadata == metadata;
    }

    private boolean checkWand(ItemStack wand, EntityPlayer player) {
        if (!(wand.getItem() instanceof ItemWandCasting castingWand)) {
            return false;
        }
        if (this.visCost == null) {
            return true;
        }
        return castingWand.consumeAllVisCrafting(wand, player, this.visCost, false);
    }

    private boolean spawnEntity(World world, ItemStack wand, EntityPlayer player, int x, int y, int z) {
        if (world.isRemote) {
            return false;
        }
        drainVis(wand, player);
        clearBlocks(world, x, y, z);
        spawnMob(world, x, y, z);
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

    private void spawnMob(World world, int x, int y, int z) {
        var newEntity = this.createEntityClosure.create(world);
        newEntity.setPosition(x + .5D, y - 1D, z + .5D);
        world.spawnEntityInWorld(newEntity);
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

    public interface CreateEntity {

        EntityLiving create(World world);
    }
}
