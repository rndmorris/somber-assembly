package dev.rndmorris.somberassembly.research;

import java.util.Arrays;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.blocks.SomberBlock;
import dev.rndmorris.somberassembly.recipes.SomberRecipes;
import joptsimple.internal.Objects;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

public class SomberResearch {

    /**
     * Somber Assembly's research category
     */
    public final static String CATEGORY = SomberAssembly.prefixModid("root");

    // Icons
    private static ResourceLocation iconSinisterStone;
    private static ResourceLocation iconSkullCreeper;
    private static ResourceLocation iconSkullSkeleton;
    private static ResourceLocation iconSkullWither;
    private static ResourceLocation iconSkullZombie;

    // ItemStacks
    private static ItemStack blockBone;
    private static ItemStack blockFlesh;
    private static ItemStack blockTnt;
    private static ItemStack itemFocusShock;

    // Research Keys
    public enum Research {

        // Visible research
        INTRO_TO_SOMBER_ASSEMBLY,
        BONE_BLOCKS,

        // Mob assembly
        SHOCK_FOCUS_ASSEMBLY,
        HOW_TO_ASSEMBLE_CREEPERS,
        HOW_TO_ASSEMBLE_SKELETONS,
        HOW_TO_ASSEMBLE_ZOMBIES,

        ROOT_EXPERIENCE,

        // Performed action flags
        ASSEMBLED_A_CREEPER,
        ASSEMBLED_A_SKELETON,
        ASSEMBLED_A_ZOMBIE,

        // Scanned flags
        ROOT_SCAN,

        // Item scan flags
        SCANNED_BLOCK_BONE,
        SCANNED_BLOCK_FLESH,
        SCANNED_BLOCK_TNT,
        SCANNED_ITEM_SHOCK_FOCUS,

        // Entity scan flags
        SCANNED_ENTITY_CREEPER,
        SCANNED_ENTITY_SKELETON,
        SCANNED_ENTITY_ZOMBIE,;

        public static String[] toStrings(Research... research) {
            return Arrays.stream(research)
                .map(Research::toString)
                .toArray(String[]::new);
        }
    }

    public enum VanillaResearch {
        FOCUSSHOCK,
        GOLEMFLESH,
    }

    public static void init() {
        initializeIconsAndItems();
        registerCategory();
        registerStarterResearch();

        // Register wand mob assembly research
        registerEarlyMobAssembly();

        // Always run this last to avoid false conflicts with other research
        registerExperienceResearch();
        registerScanningResearch();
    }

    private static void initializeIconsAndItems() {
        iconSinisterStone = new ResourceLocation("thaumcraft", "textures/items/sinister_stone.png");
        iconSkullCreeper = new ResourceLocation("minecraft", "textures/items/skull_creeper.png");
        iconSkullSkeleton = new ResourceLocation("minecraft", "textures/items/skull_skeleton.png");
        iconSkullWither = new ResourceLocation("minecraft", "textures/items/skull_wither.png");
        iconSkullZombie = new ResourceLocation("minecraft", "textures/items/skull_zombie.png");

        blockBone = new ItemStack(SomberBlock.boneBlock);
        blockFlesh = ItemApi.getBlock("blockTaint", 2);
        blockTnt = new ItemStack(Blocks.tnt);
        itemFocusShock = ItemApi.getItem("itemFocusShock", 0);
    }

    private static void registerCategory() {
        final var background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        ResearchCategories.registerCategory(CATEGORY, iconSkullWither, background);
    }

    private static void registerStarterResearch() {
        ResearchItemBuilder.forKey(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .position(0, 0)
            .display(iconSinisterStone)
            .addTextPage(1, 2)
            .makeAutoUnlock()
            .makeSpecial()
            .register();

        ResearchItemBuilder.forKey(Research.BONE_BLOCKS)
            .position(0, -2)
            .display(new ItemStack(SomberBlock.boneBlock))
            .addTextPage(1)
            .addRecipePage(SomberRecipes.boneBlockRecipe)
            .addRecipePage(SomberRecipes.boneBlockUncraftRecipe)
            .addSecretPage(Research.HOW_TO_ASSEMBLE_SKELETONS)
            .addParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .makeAutoUnlock()
            .register();
    }

    private static void registerEarlyMobAssembly() {
        ResearchItemBuilder.forKey(Research.SHOCK_FOCUS_ASSEMBLY)
            .position(0, 3)
            .display(itemFocusShock)
            .addTextPage(1)
            .addParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .addParents(VanillaResearch.FOCUSSHOCK.toString())
            .addHiddenParents(Research.SCANNED_ITEM_SHOCK_FOCUS)
            .addAspectCost(Aspect.WEATHER, 5)
            .addAspectCost(Aspect.CRAFT, 8)
            .addAspectCost(Aspect.ENERGY, 5)
            .addAspectCost(Aspect.TOOL, 8)
            .register();

        ResearchItemBuilder.forKey(Research.HOW_TO_ASSEMBLE_ZOMBIES)
            .position(3, 1)
            .display(iconSkullZombie)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(Research.SCANNED_ENTITY_ZOMBIE, Research.SCANNED_BLOCK_FLESH)
            .addHiddenParents(VanillaResearch.GOLEMFLESH.toString())
            .addAspectCost(Aspect.UNDEAD, 8)
            .addAspectCost(Aspect.ENERGY, 8)
            .addAspectCost(Aspect.MAN, 8)
            .addAspectCost(Aspect.EXCHANGE, 8)
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleZombie)
            .addSecretPage(Research.ASSEMBLED_A_ZOMBIE)
            .register();

        ResearchItemBuilder.forKey(Research.HOW_TO_ASSEMBLE_SKELETONS)
            .position(3, 3)
            .display(iconSkullSkeleton)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(
                Research.ASSEMBLED_A_SKELETON,
                Research.SCANNED_ENTITY_SKELETON,
                Research.SCANNED_BLOCK_BONE)
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleSkeleton)
            .makeLost()
            .register();

        ResearchItemBuilder.forKey(Research.HOW_TO_ASSEMBLE_CREEPERS)
            .position(3, 5)
            .display(iconSkullCreeper)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(Research.ASSEMBLED_A_CREEPER, Research.SCANNED_ENTITY_CREEPER, Research.SCANNED_BLOCK_TNT)
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleCreeper)
            .makeLost()
            .register();
    }

    /**
     * Research unlocked by experiencing things.
     */
    private static void registerExperienceResearch() {
        ResearchItemBuilder.forKey(Research.ROOT_EXPERIENCE)
            .makeVirtual()
            .makeAutoUnlock()
            .addHiddenParents(Research.INTRO_TO_SOMBER_ASSEMBLY);
        addBehaviorResearch(Research.ASSEMBLED_A_CREEPER, Research.ASSEMBLED_A_SKELETON, Research.ASSEMBLED_A_ZOMBIE);
    }

    /**
     * Research unlocked by scanning things
     */
    private static void registerScanningResearch() {
        ResearchItemBuilder.forKey(Research.ROOT_SCAN)
            .makeVirtual()
            .makeAutoUnlock()
            .addHiddenParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .register();
        addItemScanResearch(blockBone::isItemEqual, Research.SCANNED_BLOCK_BONE);
        addItemScanResearch(blockFlesh::isItemEqual, Research.SCANNED_BLOCK_FLESH);
        addItemScanResearch(blockTnt::isItemEqual, Research.SCANNED_BLOCK_TNT);
        addItemScanResearch(itemFocusShock::isItemEqual, Research.SCANNED_ITEM_SHOCK_FOCUS);
        addEntityScanResearch((e) -> e instanceof EntityCreeper, Research.SCANNED_ENTITY_CREEPER);
        addEntityScanResearch((e) -> e instanceof EntitySkeleton, Research.SCANNED_ENTITY_SKELETON);
        addEntityScanResearch((e) -> e instanceof EntityZombie, Research.SCANNED_ENTITY_ZOMBIE);
    }

    // Registration helpers

    private static void addEntityScanResearch(@Nonnull Predicate<Entity> predicate, Research... research) {
        for (var newResearch : research) {
            ResearchItemBuilder.forKey(newResearch)
                .addHiddenParents(Research.ROOT_SCAN)
                .makeVirtual()
                .register();
        }
        SomberAssembly.proxy.getScanEventManager()
            .registerHandler((event) -> {
                if (event.isEntityScan() && predicate.test(event.getScannedEntity())) {
                    unlockResearch(event.scanningPlayer(), research);
                }
            });
    }

    private static void addItemScanResearch(@Nonnull Predicate<ItemStack> predicate, Research... research) {
        Objects.ensureNotNull(research);
        Objects.ensureNotNull(predicate);

        for (var newResearch : research) {
            ResearchItemBuilder.forKey(newResearch)
                .addHiddenParents(Research.ROOT_SCAN)
                .makeVirtual()
                .register();
        }

        SomberAssembly.proxy.getScanEventManager()
            .registerHandler((event) -> {
                if (event.isObjectScan() && predicate.test(event.getScannedItem())) {
                    unlockResearch(event.scanningPlayer(), research);
                }
            });
    }

    private static void addBehaviorResearch(Research... research) {
        for (var newResearch : research) {
            ResearchItemBuilder.forKey(newResearch)
                .addHiddenParents(Research.ROOT_EXPERIENCE)
                .makeVirtual()
                .register();
        }
    }

    // Public methods

    public static void unlockResearch(EntityPlayer player, Research... research) {
        unlockResearch(player, Research.toStrings(research));
    }

    public static void unlockResearch(EntityPlayer player, String... researchKeys) {
        for (var key : researchKeys) {
            if (!ResearchManager.isResearchComplete(player.getCommandSenderName(), key)) {
                Thaumcraft.proxy.researchManager.completeResearch(player, key);
                SomberAssembly.proxy.playDiscoverySounds(player);
            }
        }
    }
}
