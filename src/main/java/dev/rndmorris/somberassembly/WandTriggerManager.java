package dev.rndmorris.somberassembly;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.wandtriggers.MobAssemblyService;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;

public class WandTriggerManager implements IWandTriggerManager {

    public final static int ASSEMBLE_CREEPER_EVENT = 0;
    public final static int ASSEMBLE_SKELETON_EVENT = 1;
    public final static int ASSEMBLE_ZOMBIE_EVENT = 2;

    private final MobAssemblyService creeperAssemblyService;
    private final MobAssemblyService skeletonAssemblyService;
    private final MobAssemblyService zombieAssemblyService;

    public WandTriggerManager() {
        // Creeper assembly
        final var assembleCreeperBlock = Blocks.tnt;
        final var assembleCreeperMetadata = 0;
        creeperAssemblyService = new MobAssemblyService(assembleCreeperBlock, assembleCreeperMetadata, (args) -> {
            var creeper = new EntityCreeper(args.world());
            creeper.setCreeperState(1);
            NBTTagCompound tagCompound = new NBTTagCompound();
            creeper.writeEntityToNBT(tagCompound);

            tagCompound.setShort("Fuse", (short) 1);

            creeper.readEntityFromNBT(tagCompound);
            return creeper;
        }, SomberResearch.ASSEMBLE_ZOMBIE_PERFORMED, SomberRecipes.assembleCreeper.aspectCost());
        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_CREEPER_EVENT, assembleCreeperBlock, assembleCreeperMetadata);

        // Skeleton assembly
        final var assembleSkeletonBlock = SomberBlocks.boneBlock;
        final var assembleSkeletonMetadata = 0;
        skeletonAssemblyService = new MobAssemblyService(assembleSkeletonBlock, assembleSkeletonMetadata, (args) -> {
            var skeleton = new EntitySkeleton(args.world());
            for (var slot = 0; slot <= 4; ++slot) {
                skeleton.setCurrentItemOrArmor(slot, null);
            }
            skeleton.setCanPickUpLoot(true);
            return skeleton;
        }, SomberResearch.ASSEMBLE_ZOMBIE_PERFORMED, SomberRecipes.assembleSkeleton.aspectCost());
        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_SKELETON_EVENT, assembleSkeletonBlock, assembleSkeletonMetadata);

        // Zombie assembly
        final var assembleZombieBlock = SomberBlocks.Thaumcraft.fleshBlock();
        final var assembleZombieMetadata = SomberBlocks.Thaumcraft.fleshBlockDamage();
        zombieAssemblyService = new MobAssemblyService(assembleZombieBlock, assembleZombieMetadata, (args) -> {
            var zombie = new EntityZombie(args.world());
            for (var slot = 0; slot <= 4; ++slot) {
                zombie.setCurrentItemOrArmor(slot, null);
            }
            zombie.setCanPickUpLoot(true);
            return zombie;
        }, SomberResearch.ASSEMBLE_ZOMBIE, SomberRecipes.assembleZombie.aspectCost());
        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_ZOMBIE_EVENT, assembleZombieBlock, assembleZombieMetadata);
    }

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {
        return switch (event) {
            case ASSEMBLE_CREEPER_EVENT -> this.creeperAssemblyService
                .tryCreateEntity(world, wand, player, x, y, z, side, event);
            case ASSEMBLE_SKELETON_EVENT -> this.skeletonAssemblyService
                .tryCreateEntity(world, wand, player, x, y, z, side, event);
            case ASSEMBLE_ZOMBIE_EVENT -> this.zombieAssemblyService
                .tryCreateEntity(world, wand, player, x, y, z, side, event);
            default -> false;
        };
    }
}
