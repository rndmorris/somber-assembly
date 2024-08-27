package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.Utils;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;

public class ZombieNecromancyTrigger implements IWandTriggerManager {

    private final MobAssemblyService manager;

    public ZombieNecromancyTrigger() {
        final int fleshBlockMetadata = 2;
        var fleshBlock = Utils.getThaumcraftBlock("blockTaint", fleshBlockMetadata);
        MobAssemblyService.CreateEntity createZombie = (args) -> {
            var zombie = new EntityZombie(args.world());
            for (var slot = 0; slot <= 4; ++slot) {
                zombie.setCurrentItemOrArmor(slot, null);
            }
            zombie.setCanPickUpLoot(true);
            return zombie;
        };
        this.manager = new MobAssemblyService(
            fleshBlock,
            fleshBlockMetadata,
            createZombie,
            null,
            new AspectList().add(Aspect.AIR, 1500)
                .add(Aspect.ENTROPY, 1500));
        WandTriggerRegistry.registerWandBlockTrigger(this, 1, fleshBlock, fleshBlockMetadata);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        return manager.tryCreateEntity(world, wand, player, x, y, z, side, event);
    }
}
