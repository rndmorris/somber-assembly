package dev.rndmorris.somberassembly.common.recipes;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import dev.rndmorris.somberassembly.common.blocks.BlockHelper;
import dev.rndmorris.somberassembly.common.blocks.SomberBlock;
import dev.rndmorris.somberassembly.common.research.SomberResearch.Research;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.api.wands.WandTriggerRegistry;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.foci.ItemFocusShock;

/**
 * Handles wand-based crafting for the addon.
 */
public class WandTriggerManager implements IWandTriggerManager {

    private static WandTriggerManager INSTANCE;

    @SuppressWarnings("unused")
    public static WandTriggerManager getInstance() {
        return INSTANCE;
    }

    private final static int ASSEMBLE_CREEPER_EVENT = 0;
    private final static int ASSEMBLE_SKELETON_EVENT = 1;
    private final static int ASSEMBLE_ZOMBIE_EVENT = 2;

    private final MobAssemblyService creeperAssemblyService;
    private final MobAssemblyService skeletonAssemblyService;
    private final MobAssemblyService zombieAssemblyService;

    public static void postInit() {
        INSTANCE = new WandTriggerManager();
    }

    private WandTriggerManager() {
        creeperAssemblyService = buildCreeperService();
        skeletonAssemblyService = buildSkeletonService();
        zombieAssemblyService = buildZombieService();
    }

    private static boolean wandHasShockFocus(ItemStack wandItemStack, ItemWandCasting wand) {
        var focus = wand.getFocus(wandItemStack);
        return focus instanceof ItemFocusShock;
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
            .requiredResearch(Research.PERMISSION_ASSEMBLE_CREEPER)
            .visCost(SomberRecipes.assembleCreeper.aspectCost())
            .teachesResearch(Research.ACTION_ASSEMBLED_A_CREEPER)
            .wandPredicate(WandTriggerManager::wandHasShockFocus)
            .build();
    }

    private MobAssemblyService buildSkeletonService() {
        final var assembleSkeletonBlock = SomberBlock.boneBlock;
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
            .requiredResearch(Research.PERMISSION_ASSEMBLE_SKELETON)
            .visCost(SomberRecipes.assembleSkeleton.aspectCost())
            .teachesResearch(Research.ACTION_ASSEMBLED_A_SKELETON)
            .wandPredicate(WandTriggerManager::wandHasShockFocus)
            .build();
    }

    private MobAssemblyService buildZombieService() {
        final var flesh = BlockHelper.Thaumcraft.flesh();
        final var fleshBlock = Block.getBlockFromItem(flesh.getItem());
        final var fleshMetadata = flesh.getItemDamage();

        WandTriggerRegistry.registerWandBlockTrigger(this, ASSEMBLE_ZOMBIE_EVENT, fleshBlock, fleshMetadata);

        return MobAssemblyService.configure()
            .expectedBlockType(fleshBlock)
            .expectedBlockMetadata(fleshMetadata)
            .createEntityClosure((args) -> {
                var zombie = new EntityZombie(args.world());
                for (var slot = 0; slot <= 4; ++slot) {
                    zombie.setCurrentItemOrArmor(slot, null);
                }
                zombie.setCanPickUpLoot(true);
                return zombie;
            })
            .requiredResearch(Research.PERMISSION_ASSEMBLE_ZOMBIE)
            .visCost(SomberRecipes.assembleZombie.aspectCost())
            .teachesResearch(Research.ACTION_ASSEMBLED_A_ZOMBIE)
            .givesWarpSticky(1)
            .givesWarpTemp(2)
            .wandPredicate(WandTriggerManager::wandHasShockFocus)
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
