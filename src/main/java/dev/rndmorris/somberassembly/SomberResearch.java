package dev.rndmorris.somberassembly;

import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.util.ResearchItemBuilder;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.research.ResearchCategories;

public class SomberResearch {

    /**
     * Somber Assembly's research category
     */
    public final static String CATEGORY = SomberAssembly.MODID.toUpperCase();

    // Research keys
    public final static String NECROMANCY_INTRO = "NECROMANCY_INTRO";
    public final static String ASSEMBLE_ZOMBIE = "ASSEMBLE_ZOMBIE";
    public final static String ASSEMBLE_ZOMBIE_PERFORMED = "ASSEMBLE_ZOMBIE_PERFORMED";

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

    private static void registerNecromancyIntro() {
        ResearchItemBuilder.forKeyAndCategory(NECROMANCY_INTRO, CATEGORY)
            .position(0, 0)
            .displayIcon(iconSinisterStone)
            .textPage("tc.research_page.NECROMANCY_INTRO.1")
            .textPage("tc.research_page.NECROMANCY_INTRO.2")
            .autoUnlock(true)
            .build()
            .registerResearchItem();
    }

    private static void registerAssembleZombie() {
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_ZOMBIE, CATEGORY)
            .position(0, 1)
            .displayIcon(iconSkullZombie)
            .aspect(Aspect.FLESH, 4)
            .aspect(Aspect.UNDEAD, 8)
            .aspect(Aspect.CRAFT, 8)
            .textPage("tc.research_page.ASSEMBLE_ZOMBIE.1")
            .compoundRecipePage(SomberRecipes.assembleZombie)
            .entityTrigger("Zombie")
            .itemTrigger(ItemApi.getItem("FocusShock", 0))
            .parent(NECROMANCY_INTRO)
            .hiddenParent(FLESH_GOLEM)
            .hiddenParent(SHOCK_FOCUS)
            .build()
            .registerResearchItem();
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_ZOMBIE_PERFORMED, CATEGORY)
            .hidden(true)
            .parent(ASSEMBLE_ZOMBIE);
    }
}
