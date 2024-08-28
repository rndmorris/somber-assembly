package dev.rndmorris.somberassembly.common.research;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.blocks.SomberBlock;
import dev.rndmorris.somberassembly.common.items.SomberItem;
import dev.rndmorris.somberassembly.common.recipes.SomberRecipes;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.research.ResearchManager;

public class SomberResearch {

    /**
     * Somber Assembly's research category
     */
    public final static String CATEGORY_BASIC = SomberAssembly.prefixModid("category_basic");

    // Research Keys
    public static class Research {

        private static String p(String key) {
            return SomberAssembly.prefixModid(key);
        }

        /**
         * Research with visible pages
         */

        // Default research
        public final static String INFO_INTRODUCTION = p("INFO_INTRODUCTION");
        public final static String INFO_BONE_BLOCKS = p("INFO_BONE_BLOCKS");

        // Mob assembly
        public final static String INFO_SHOCK_FOCUS = p("INFO_SHOCK_FOCUS");
        public final static String INFO_CREEPER_ASSEMBLY = p("INFO_CREEPER_ASSEMBLY");
        public final static String INFO_SKELETON_ASSEMBLY = p("INFO_SKELETON_ASSEMBLY");
        public final static String INFO_ZOMBIE_ASSEMBLY = p("INFO_ZOMBIE_ASSEMBLY");

        // Items
        public final static String INFO_DECAYING_FLESH = p("INFO_DECAYING_FLESH");

        /**
         * Virtual research
         */

        // Permission flags
        public final static String PERMISSION_ROOT = p("PERMISSION_ROOT");
        public final static String PERMISSION_ASSEMBLE_CREEPER = p("PERMISSION_ASSEMBLE_CREEPER");
        public final static String PERMISSION_ASSEMBLE_SKELETON = p("PERMISSION_ASSEMBLE_SKELETON");
        public final static String PERMISSION_ASSEMBLE_ZOMBIE = p("PERMISSION_ASSEMBLE_ZOMBIE");
        public final static String PERMISSION_CRAFT_DECAYING_FLESH = p("PERMISSION_CRAFT_DECAYING_FLESH");

        // Performed action flags
        public final static String ACTION_ROOT = p("ACTION_ROOT");
        public final static String ACTION_ASSEMBLED_A_CREEPER = p("ACTION_ASSEMBLED_A_CREEPER");
        public final static String ACTION_ASSEMBLED_A_SKELETON = p("ACTION_ASSEMBLED_A_SKELETON");
        public final static String ACTION_ASSEMBLED_A_ZOMBIE = p("ACTION_ASSEMBLED_A_ZOMBIE");
    }

    public static class VanillaResearch {

        public final static String FOCUSSHOCK = "FOCUSSHOCK";
        public final static String GOLEMFLESH = "GOLEMFLESH";
    }

    public static void postInit() {
        ResearchBuilder.setCategory(CATEGORY_BASIC);
        registerCategory();
        automaticResearch();
        earlyMobAssembly();
        itemResearch();
        permissionResearch();
        triggeredResearch();
    }

    private static void registerCategory() {
        final var background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        final var witherSkull = new ResourceLocation("minecraft", "textures/items/skull_wither.png");
        ResearchCategories.registerCategory(CATEGORY_BASIC, witherSkull, background);
    }

    /**
     * Research unlocked automatically (intro, bone blocks, etc)
     */
    private static void automaticResearch() {
        ResearchBuilder.forKey(Research.INFO_INTRODUCTION)
            .setPosition(0, 0)
            .setImage(ItemApi.getItem("itemCompassStone", 0))
            .addTextPage(1, 2)
            .setAutoUnlock()
            .setSpecial()
            .register();

        ResearchBuilder.forKey(Research.INFO_BONE_BLOCKS)
            .setPosition(0, -2)
            .setImage(new ItemStack(SomberBlock.boneBlock))
            .addTextPage(1)
            .addRecipePage(SomberRecipes.boneBlockRecipe)
            .addRecipePage(SomberRecipes.boneBlockUncraftRecipe)
            .addSecretPage(Research.INFO_SKELETON_ASSEMBLY)
            .addParents(Research.INFO_INTRODUCTION)
            .setRound()
            .setAutoUnlock()
            .register();
    }

    /**
     * Research for building mobs with a shock focus
     */
    private static void earlyMobAssembly() {
        final var itemFocusShock = ItemApi.getItem("itemFocusShock", 0);
        ResearchBuilder.forKey(Research.INFO_SHOCK_FOCUS)
            .setPosition(0, 3)
            .setImage(itemFocusShock)
            .addTextPage(1)
            .addParents(Research.INFO_INTRODUCTION)
            .addParents(VanillaResearch.FOCUSSHOCK)
            .addItemTrigger(itemFocusShock)
            .addAspectCost(Aspect.WEATHER, 5)
            .addAspectCost(Aspect.CRAFT, 8)
            .addAspectCost(Aspect.ENERGY, 5)
            .addAspectCost(Aspect.TOOL, 8)
            .register();

        ResearchBuilder.forKey(Research.INFO_ZOMBIE_ASSEMBLY)
            .setPosition(3, 1)
            .setImage(new ItemStack(Items.skull, 0, 2))
            .addTextPage(1)
            .addParents(Research.INFO_SHOCK_FOCUS)
            .addHiddenParents(VanillaResearch.GOLEMFLESH)
            .addSiblings(Research.PERMISSION_ASSEMBLE_ZOMBIE)
            .setConcealed()
            .addEntityTrigger("Zombie")
            .addAspectCost(Aspect.UNDEAD, 8)
            .addAspectCost(Aspect.ENERGY, 8)
            .addAspectCost(Aspect.MAN, 8)
            .addAspectCost(Aspect.EXCHANGE, 8)
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleZombie)
            .addSecretPage(Research.ACTION_ASSEMBLED_A_ZOMBIE)
            .register();

        ResearchBuilder.forKey(Research.INFO_SKELETON_ASSEMBLY)
            .setPosition(3, 3)
            .setImage(new ItemStack(Items.skull, 0, 0))
            .addTextPage(1)
            .addParents(Research.INFO_SHOCK_FOCUS)
            .addHiddenParents(Research.ACTION_ASSEMBLED_A_SKELETON)
            .setConcealed()
            .addEntityTrigger("Skeleton")
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleSkeleton)
            .register();

        ResearchBuilder.forKey(Research.INFO_CREEPER_ASSEMBLY)
            .setPosition(3, 5)
            .setImage(new ItemStack(Items.skull, 0, 4))
            .addTextPage(1)
            .addParents(Research.INFO_SHOCK_FOCUS)
            .addHiddenParents(Research.ACTION_ASSEMBLED_A_CREEPER)
            .setHidden()
            .addEntityTrigger("Creeper")
            .setComplexity(2)
            .addCompoundRecipePage(SomberRecipes.assembleCreeper)
            .register();
    }

    /**
     * Informational research about crafting items
     */
    private static void itemResearch() {
        ResearchBuilder.forKey(Research.INFO_DECAYING_FLESH)
            .addParents(Research.INFO_INTRODUCTION)
            .setHidden()
            .setPosition(-1, -2)
            .setImage(SomberItem.decayingFlesh(0))
            .addItemTrigger(SomberItem.decayingFlesh(0))
            .addAspectCost(Aspect.DEATH, 2)
            .addAspectCost(Aspect.SENSES, 4)
            .addAspectCost(Aspect.FLESH, 4)
            .addAspectCost(Aspect.ENTROPY, 2)
            .addTextPage(1)
            .addRecipePage(SomberRecipes.decayingFleshRecipe)
            .addSiblings(Research.PERMISSION_CRAFT_DECAYING_FLESH)
            .register();
    }

    /**
     * Research used to determine whether an action or recipe can be performed
     */
    private static void permissionResearch() {
        ResearchBuilder.forKey(Research.PERMISSION_ROOT)
            .addHiddenParents(Research.INFO_INTRODUCTION)
            .setAutoUnlock()
            .register();
        registerPermission(
            Research.PERMISSION_ASSEMBLE_CREEPER,
            Research.PERMISSION_ASSEMBLE_SKELETON,
            Research.PERMISSION_ASSEMBLE_ZOMBIE,
            Research.PERMISSION_CRAFT_DECAYING_FLESH);
    }

    private static void registerPermission(String... researchKeys) {
        for (var key : researchKeys) {
            ResearchBuilder.forKey(key)
                .addHiddenParents(Research.PERMISSION_ROOT)
                .register();
        }
    }

    /**
     * Research given upon performing some action
     */
    private static void triggeredResearch() {
        ResearchBuilder.forKey(Research.ACTION_ROOT)
            .setAutoUnlock()
            .addHiddenParents(Research.INFO_INTRODUCTION)
            .register();
        ResearchBuilder.forKey(Research.ACTION_ASSEMBLED_A_ZOMBIE)
            .addSiblings(Research.PERMISSION_ASSEMBLE_CREEPER, Research.PERMISSION_ASSEMBLE_SKELETON)
            .register();
        ResearchBuilder.forKey(Research.ACTION_ASSEMBLED_A_CREEPER)
            .addSiblings(Research.INFO_CREEPER_ASSEMBLY)
            .register();
        ResearchBuilder.forKey(Research.ACTION_ASSEMBLED_A_SKELETON)
            .addSiblings(Research.INFO_SKELETON_ASSEMBLY)
            .register();
    }

    // Public methods
    public static void unlockResearch(EntityPlayer player, String... researchKeys) {
        unlockResearch(player, Arrays.asList(researchKeys));
    }

    public static void unlockResearch(EntityPlayer player, Iterable<String> researchKeys) {
        final var playerName = player.getCommandSenderName();
        for (var key : researchKeys) {
            if (!ResearchManager.isResearchComplete(playerName, key)) {
                Thaumcraft.proxy.researchManager.completeResearch(player, key);
            }
        }
    }
}
