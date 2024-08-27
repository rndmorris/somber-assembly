package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;

public class CreeperNecromancyTrigger implements IWandTriggerManager {

    private final WandNecromancyManager manager;

    public CreeperNecromancyTrigger() {
        final int blockMetadata = 0;
        var tntBlock = Blocks.tnt;
        WandNecromancyManager.CreateEntity createCreeper = (args) -> {
            var creeper = new EntityCreeper(args.world());
            creeper.setCreeperState(1);
            NBTTagCompound tagCompound = new NBTTagCompound();
            creeper.writeEntityToNBT(tagCompound);

            tagCompound.setShort("Fuse", (short) 1);

            creeper.readEntityFromNBT(tagCompound);
            return creeper;
        };
        this.manager = new WandNecromancyManager(tntBlock, blockMetadata, createCreeper, null, null);
        WandTriggerRegistry.registerWandBlockTrigger(this, 1, tntBlock, blockMetadata);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        return manager.tryCreateEntity(world, wand, player, x, y, z, side, event);
    }
}
