package dev.rndmorris.somberassembly;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.util.MobAssemblyService;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.items.wands.foci.ItemFocusShock;

/**
 * Handles wand-based crafting for the addon.
 */
public class WandTriggerManager implements IWandTriggerManager {

    public final static int ASSEMBLE_CREEPER_EVENT = 0;
    public final static int ASSEMBLE_SKELETON_EVENT = 1;
    public final static int ASSEMBLE_ZOMBIE_EVENT = 2;

    private final MobAssemblyService creeperAssemblyService;
    private final MobAssemblyService skeletonAssemblyService;
    private final MobAssemblyService zombieAssemblyService;

    private final MobAssemblyService.WandPredicate wandHasShockFocus = (wandItemStack, wand) -> {
        var focus = wand.getFocus(wandItemStack);
        return focus instanceof ItemFocusShock;
    };

    public WandTriggerManager() {
        creeperAssemblyService = buildCreeperService();
        skeletonAssemblyService = buildSkeletonService();
        zombieAssemblyService = buildZombieService();
    }

    private MobAssemblyService buildCreeperService() {
        final var assembleCreeperBlock = Blocks.tnt;
        final var assembleCreeperMetadata = 0;

        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_CREEPER_EVENT, assembleCreeperBlock, assembleCreeperMetadata);

        return MobAssemblyService.configure()
            .expectedBlockType(assembleCreeperBlock)
            .expectedBlockMetadata(assembleCreeperMetadata)
            .createEntityClosure((args) -> {
                var creeper = new EntityCreeper(args.world());
                creeper.setCreeperState(1);
                NBTTagCompound tagCompound = new NBTTagCompound();
                creeper.writeEntityToNBT(tagCompound);

                tagCompound.setShort("Fuse", (short) 1);

                creeper.readEntityFromNBT(tagCompound);
                return creeper;
            })
            .requiredResearch(SomberResearch.ASSEMBLE_ZOMBIE_PERFORMED, SomberResearch.SCANNED_CREEPER)
            .visCost(SomberRecipes.assembleCreeper.aspectCost())
            .teachesResearch(SomberResearch.ASSEMBLE_CREEPER_PERFORMED)
            .wandPredicate(wandHasShockFocus)
            .build();
    }

    private MobAssemblyService buildSkeletonService() {
        final var assembleSkeletonBlock = SomberBlocks.boneBlock;
        final var assembleSkeletonMetadata = 0;

        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_SKELETON_EVENT, assembleSkeletonBlock, assembleSkeletonMetadata);

        return MobAssemblyService.configure()
            .expectedBlockType(assembleSkeletonBlock)
            .expectedBlockMetadata(assembleSkeletonMetadata)
            .createEntityClosure((args) -> {
                var skeleton = new EntitySkeleton(args.world());
                for (var slot = 0; slot <= 4; ++slot) {
                    skeleton.setCurrentItemOrArmor(slot, null);
                }
                skeleton.setCanPickUpLoot(true);
                return skeleton;
            })
            .requiredResearch(SomberResearch.ASSEMBLE_ZOMBIE_PERFORMED, SomberResearch.SCANNED_SKELETON)
            .visCost(SomberRecipes.assembleSkeleton.aspectCost())
            .teachesResearch(SomberResearch.ASSEMBLE_SKELETON_PERFORMED)
            .wandPredicate(wandHasShockFocus)
            .build();
    }

    private MobAssemblyService buildZombieService() {
        final var assembleZombieBlock = SomberBlocks.Thaumcraft.fleshBlock();
        final var assembleZombieMetadata = SomberBlocks.Thaumcraft.fleshBlockDamage();

        WandTriggerRegistry
            .registerWandBlockTrigger(this, ASSEMBLE_ZOMBIE_EVENT, assembleZombieBlock, assembleZombieMetadata);

        return MobAssemblyService.configure()
            .expectedBlockType(assembleZombieBlock)
            .expectedBlockMetadata(assembleZombieMetadata)
            .createEntityClosure((args) -> {
                var zombie = new EntityZombie(args.world());
                for (var slot = 0; slot <= 4; ++slot) {
                    zombie.setCurrentItemOrArmor(slot, null);
                }
                zombie.setCanPickUpLoot(true);
                return zombie;
            })
            .requiredResearch(SomberResearch.ASSEMBLE_ZOMBIE, SomberResearch.SCANNED_ZOMBIE)
            .visCost(SomberRecipes.assembleZombie.aspectCost())
            .teachesResearch(SomberResearch.ASSEMBLE_ZOMBIE_PERFORMED)
            .givesWarpSticky(1)
            .givesWarpTemp(2)
            .wandPredicate(wandHasShockFocus)
            .build();
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
