package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.Utils;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;

public class ZombieNecromancyTrigger implements IWandTriggerManager {

    private final WandNecromancyManager manager;

    public ZombieNecromancyTrigger() {
        final int fleshBlockMetadata = 2;
        var fleshBlock = Utils.getThaumcraftBlock("blockTaint", fleshBlockMetadata);
        this.manager = new WandNecromancyManager(fleshBlock, fleshBlockMetadata, EntityZombie::new, null, null);
        WandTriggerRegistry.registerWandBlockTrigger(this, 1, fleshBlock, fleshBlockMetadata);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        return manager.tryCreateEntity(world, wand, player, x, y, z, event);
    }
}
