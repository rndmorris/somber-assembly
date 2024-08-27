package dev.rndmorris.somberassembly.wandtriggers;

import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.SomberBlocks;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;

public class SkeletonNecromancyTrigger implements IWandTriggerManager {

    private final WandNecromancyManager manager;

    public SkeletonNecromancyTrigger() {
        final int boneBlockMetadata = 0;
        var boneBlock = SomberBlocks.bone_block;
        WandNecromancyManager.CreateEntity createSkeleton = (world) -> {
            var skeleton = new EntitySkeleton(world);
            for (var slot = 0; slot <= 4; ++slot) {
                skeleton.setCurrentItemOrArmor(slot, null);
            }
            return skeleton;
        };
        this.manager = new WandNecromancyManager(boneBlock, boneBlockMetadata, createSkeleton, null, null);
        WandTriggerRegistry.registerWandBlockTrigger(this, 1, boneBlock, boneBlockMetadata);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        return manager.tryCreateEntity(world, wand, player, x, y, z, event);
    }
}
