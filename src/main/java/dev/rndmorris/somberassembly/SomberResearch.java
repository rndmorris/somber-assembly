package dev.rndmorris.somberassembly;

import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.util.ResearchItemBuilder;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.Thaumcraft;

public class SomberResearch {

    /**
     * Somber Assembly's research category
     */
    public final static String CATEGORY = SomberAssembly.MODID.toUpperCase();

    // Scanned flags
    public final static String SCANNED_ZOMBIE = "SCANNED_ZOMBIE";
    public final static String SCANNED_SHOCK_FOCUS = "SCANNED_SHOCK_FOCUS";

    // Research keys
    public final static String NECROMANCY_INTRO = "NECROMANCY_INTRO";
    public final static String ASSEMBLE_ZOMBIE = "ASSEMBLE_ZOMBIE";
    public final static String ASSEMBLE_ZOMBIE_PERFORMED = "ASSEMBLE_ZOMBIE_PERFORMED";
    public final static String ASSEMBLE_SKELETON = "ASSEMBLE_SKELETON";
    public final static String ASSEMBLE_CREEPER = "ASSEMBLE_CREEPER";

    // External research keys
    final static String SHOCK_FOCUS = "FOCUSSHOCK";
    final static String FLESH_GOLEM = "GOLEMFLESH";

    // Icons
    private static ResourceLocation iconSinisterStone;
    private static ResourceLocation iconSkullSkeleton;
    private static ResourceLocation iconSkullWither;
    private static ResourceLocation iconSkullZombie;

    public static void init() {
        registerIcons();
        registerCategory();
        registerNecromancyIntro();
        registerAssembleZombie();
        // registerAssembleSkeleton();
        registerVirutalResearch();
        Thaumcraft.
    }

    private static void registerIcons() {
        iconSinisterStone = new ResourceLocation("thaumcraft", "textures/items/sinister_stone.png");
        iconSkullSkeleton = new ResourceLocation("minecraft", "textures/items/skull_skeleton.png");
        iconSkullWither = new ResourceLocation("minecraft", "textures/items/skull_wither.png");
        iconSkullZombie = new ResourceLocation("minecraft", "textures/items/skull_zombie.png");
    }

    private static void registerCategory() {
        final var background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        ResearchCategories.registerCategory(CATEGORY, iconSkullWither, background);
    }

    private static void registerVirutalResearch() {
        final boolean FLAGS_VIRTUAL = false;
        final boolean FLAGS_HIDDEN = false;
        // Scan flags
        ResearchItemBuilder.forKeyAndCategory(SCANNED_ZOMBIE, CATEGORY)
            .virtual(FLAGS_VIRTUAL)
            .position(0, 5)
            .hidden(FLAGS_HIDDEN)
            .entityTrigger("Zombie")
            .display(iconSkullZombie)
            .register();
        final var itemFocusShock = ItemApi.getItem("itemFocusShock", 0);
        ResearchItemBuilder.forKeyAndCategory(SCANNED_SHOCK_FOCUS, CATEGORY)
            .virtual(FLAGS_VIRTUAL)
            .position(0, 6)
            .hidden(FLAGS_HIDDEN)
            .display(itemFocusShock)
            .itemTrigger(itemFocusShock)
            .register();

        // Behavior flags
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_ZOMBIE_PERFORMED, CATEGORY)
            .virtual(true)
            .register();
    }

    private static void registerNecromancyIntro() {
        ResearchItemBuilder.forKeyAndCategory(NECROMANCY_INTRO, CATEGORY)
            .position(0, 0)
            .display(iconSinisterStone)
            .textPage("tc.research_page.NECROMANCY_INTRO.1")
            .textPage("tc.research_page.NECROMANCY_INTRO.2")
            .autoUnlock(true)
            .register();
    }

    private static void registerAssembleZombie() {
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_ZOMBIE, CATEGORY)
            .position(0, 1)
            .display(iconSkullZombie)
            .aspect(Aspect.FLESH, 4)
            .aspect(Aspect.UNDEAD, 8)
            .aspect(Aspect.CRAFT, 8)
            .textPage(page(ASSEMBLE_ZOMBIE, 1))
            .compoundRecipePage(SomberRecipes.assembleZombie)
            .secretPage(ASSEMBLE_ZOMBIE_PERFORMED, page(ASSEMBLE_ZOMBIE, ASSEMBLE_ZOMBIE_PERFORMED))
            .entityTrigger("Zombie")
            .parent(NECROMANCY_INTRO)
            .hiddenParent(FLESH_GOLEM)
            .hiddenParent(SHOCK_FOCUS)
            .hiddenParent(SCANNED_ZOMBIE)
            .hiddenParent(SCANNED_SHOCK_FOCUS)
            .hidden(true)
            .register()
            .registerResearchItem();
    }

    private static void registerAssembleSkeleton() {
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_SKELETON, CATEGORY)
            .position(1, 1)
            .display(iconSkullSkeleton)
            .textPage(page(ASSEMBLE_SKELETON, 1))
            .compoundRecipePage(SomberRecipes.assembleSkeleton)
            .entityTrigger("Skeleton")
            .parent(ASSEMBLE_ZOMBIE_PERFORMED)
            .lost(true)
            .register();
    }

    private static String page(String research, int page) {
        return "tc.research_page." + research + "." + page;
    }

    private static String page(String research, String page) {
        return "tc.research_page." + research + "." + page;
    }
}
