package dev.rndmorris.somberassembly.research;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.blocks.SomberBlock;
import dev.rndmorris.somberassembly.recipes.SomberRecipes;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

public class SomberResearch {

    /**
     * Somber Assembly's research category
     */
    public final static String CATEGORY = SomberAssembly.prefixModid("root_category");

    // Icons
    private static ResourceLocation iconSinisterStone;
    private static ResourceLocation iconSkullCreeper;
    private static ResourceLocation iconSkullSkeleton;
    private static ResourceLocation iconSkullWither;
    private static ResourceLocation iconSkullZombie;

    // ItemStacks
    private static ItemStack itemFocusShock;

    // Research Keys
    public static class Research {

        private static String p(String key) {
            return SomberAssembly.prefixModid(key);
        }

        // Visible research
        public final static String INTRO_TO_SOMBER_ASSEMBLY = p("INTRO_TO_SOMBER_ASSEMBLY");
        public final static String BONE_BLOCKS = p("BONE_BLOCKS");

        // Mob assembly
        public final static String SHOCK_FOCUS_ASSEMBLY = p("SHOCK_FOCUS_ASSEMBLY");
        public final static String HOW_TO_ASSEMBLE_CREEPERS = p("HOW_TO_ASSEMBLE_CREEPERS");
        public final static String HOW_TO_ASSEMBLE_SKELETONS = p("HOW_TO_ASSEMBLE_SKELETONS");
        public final static String HOW_TO_ASSEMBLE_ZOMBIES = p("HOW_TO_ASSEMBLE_ZOMBIES");

        public final static String ROOT_BEHAVIOR = p("ROOT_BEHAVIOR");

        // Permission flags
        public final static String PERMISSION_ROOT = p("PERMISSION_ROOT");
        public final static String PERMISSION_ASSEMBLE_CREEPER = p("CAN_ASSEMBLE_CREEPER");
        public final static String PERMISSION_ASSEMBLE_SKELETON = p("CAN_ASSEMBLE_SKELETON");
        public final static String PERMISSION_ASSEMBLE_ZOMBIE = p("CAN_ASSEMBLE_ZOMBIE");

        // Performed action flags
        public final static String ASSEMBLED_A_CREEPER = p("ASSEMBLED_A_CREEPER");
        public final static String ASSEMBLED_A_SKELETON = p("ASSEMBLED_A_SKELETON");
        public final static String ASSEMBLED_A_ZOMBIE = p("ASSEMBLED_A_ZOMBIE");
    }

    public static class VanillaResearch {

        public final static String FOCUSSHOCK = "FOCUSSHOCK";
        public final static String GOLEMFLESH = "GOLEMFLESH";
    }

    public static void init() {
        initializeIconsAndItems();

        ResearchBuilder.setCategory(CATEGORY);
        registerCategory();
        registerStarterResearch();

        // Register wand mob assembly research
        registerEarlyMobAssembly();

        // Always add virtual research last to avoid false conflicts with other research
        registerPermissionResearch();
        registerBehaviorResearch();
    }

    private static void initializeIconsAndItems() {
        iconSinisterStone = new ResourceLocation("thaumcraft", "textures/items/sinister_stone.png");
        iconSkullCreeper = new ResourceLocation("minecraft", "textures/items/skull_creeper.png");
        iconSkullSkeleton = new ResourceLocation("minecraft", "textures/items/skull_skeleton.png");
        iconSkullWither = new ResourceLocation("minecraft", "textures/items/skull_wither.png");
        iconSkullZombie = new ResourceLocation("minecraft", "textures/items/skull_zombie.png");

        itemFocusShock = ItemApi.getItem("itemFocusShock", 0);
    }

    private static void registerCategory() {
        final var background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        ResearchCategories.registerCategory(CATEGORY, iconSkullWither, background);
    }

    private static void registerStarterResearch() {
        ResearchBuilder.forKey(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .setPosition(0, 0)
            .setImage(iconSinisterStone)
            .addTextPage(1, 2)
            .setAutoUnlock()
            .setSpecial()
            .register();

        ResearchBuilder.forKey(Research.BONE_BLOCKS)
            .setPosition(0, -2)
            .setImage(new ItemStack(SomberBlock.boneBlock))
            .addTextPage(1)
            .addRecipePage(SomberRecipes.boneBlockRecipe)
            .addRecipePage(SomberRecipes.boneBlockUncraftRecipe)
            .addSecretPage(Research.HOW_TO_ASSEMBLE_SKELETONS)
            .addParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .setRound()
            .setAutoUnlock()
            .register();
    }

    private static void registerEarlyMobAssembly() {
        ResearchBuilder.forKey(Research.SHOCK_FOCUS_ASSEMBLY)
            .setPosition(0, 3)
            .setImage(itemFocusShock)
            .addTextPage(1)
            .addParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .addParents(VanillaResearch.FOCUSSHOCK)
            .addItemTrigger(itemFocusShock)
            .addAspectCost(Aspect.WEATHER, 5)
            .addAspectCost(Aspect.CRAFT, 8)
            .addAspectCost(Aspect.ENERGY, 5)
            .addAspectCost(Aspect.TOOL, 8)
            .register();

        ResearchBuilder.forKey(Research.HOW_TO_ASSEMBLE_ZOMBIES)
            .setPosition(3, 1)
            .setImage(iconSkullZombie)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(VanillaResearch.GOLEMFLESH)
            .addSiblings(Research.PERMISSION_ASSEMBLE_ZOMBIE)
            .setHidden()
            .addEntityTrigger("Zombie")
            .addAspectCost(Aspect.UNDEAD, 8)
            .addAspectCost(Aspect.ENERGY, 8)
            .addAspectCost(Aspect.MAN, 8)
            .addAspectCost(Aspect.EXCHANGE, 8)
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleZombie)
            .addSecretPage(Research.ASSEMBLED_A_ZOMBIE)
            .register();

        ResearchBuilder.forKey(Research.HOW_TO_ASSEMBLE_SKELETONS)
            .setPosition(3, 3)
            .setImage(iconSkullSkeleton)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(Research.ASSEMBLED_A_SKELETON)
            .setHidden()
            .addEntityTrigger("Skeleton")
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleSkeleton)
            .register();

        ResearchBuilder.forKey(Research.HOW_TO_ASSEMBLE_CREEPERS)
            .setPosition(3, 5)
            .setImage(iconSkullCreeper)
            .addTextPage(1)
            .addParents(Research.SHOCK_FOCUS_ASSEMBLY)
            .addHiddenParents(Research.ASSEMBLED_A_CREEPER)
            .setHidden()
            .addEntityTrigger("Creeper")
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleCreeper)
            .register();
    }

    private static void registerPermissionResearch() {
        ResearchBuilder.forKey(Research.PERMISSION_ROOT)
            .addHiddenParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .setAutoUnlock()
            .register();
        ResearchBuilder.forKey(Research.PERMISSION_ASSEMBLE_ZOMBIE)
            .addHiddenParents(Research.PERMISSION_ROOT)
            .register();
        ResearchBuilder.forKey(Research.PERMISSION_ASSEMBLE_CREEPER)
            .addHiddenParents(Research.PERMISSION_ROOT)
            .register();
        ResearchBuilder.forKey(Research.PERMISSION_ASSEMBLE_SKELETON)
            .addHiddenParents(Research.PERMISSION_ROOT)
            .register();
        ResearchBuilder.forKey(Research.PERMISSION_ASSEMBLE_ZOMBIE)
            .addHiddenParents(Research.PERMISSION_ROOT)
            .register();
    }

    /**
     * Research unlocked by doing things.
     */
    private static void registerBehaviorResearch() {
        ResearchBuilder.forKey(Research.ROOT_BEHAVIOR)
            .setAutoUnlock()
            .addHiddenParents(Research.INTRO_TO_SOMBER_ASSEMBLY)
            .register();
        ResearchBuilder.forKey(Research.ASSEMBLED_A_ZOMBIE)
            .addSiblings(Research.PERMISSION_ASSEMBLE_CREEPER, Research.PERMISSION_ASSEMBLE_SKELETON)
            .register();
        ResearchBuilder.forKey(Research.ASSEMBLED_A_CREEPER)
            .addSiblings(Research.HOW_TO_ASSEMBLE_CREEPERS)
            .register();
        ResearchBuilder.forKey(Research.ASSEMBLED_A_SKELETON)
            .addSiblings(Research.HOW_TO_ASSEMBLE_SKELETONS)
            .register();
    }

    // Public methods
    public static void unlockResearch(EntityPlayer player, String... researchKeys) {
        final var playerName = player.getCommandSenderName();
        for (var key : researchKeys) {
            if (!ResearchManager.isResearchComplete(playerName, key)) {
                Thaumcraft.proxy.researchManager.completeResearch(player, key);
                SomberAssembly.proxy.playDiscoverySounds(player);
            }
        }
    }
}
