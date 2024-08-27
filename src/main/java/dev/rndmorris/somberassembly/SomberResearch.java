package dev.rndmorris.somberassembly;

import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
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

    public static void unlockResearch(EntityPlayer player, String researchKey) {
        Thaumcraft.proxy.researchManager.completeResearch(player, researchKey);
    }

    public static void init() {
        registerIcons();
        registerCategory();
        registerNecromancyIntro();
        registerAssembleZombie();
        // registerAssembleSkeleton();
        registerVirutalResearch();
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
        // Scan flags
        ResearchItemBuilder.forKeyAndCategory(SCANNED_ZOMBIE, CATEGORY)
            .virtual(true)
            .entityTrigger("Zombie")
            .display(iconSkullZombie)
            .register();
        SomberAssembly.proxy.entityScannedEventRegistrar()
            .registerEventListener((event) -> {
                if (event.scannedObject() instanceof EntityZombie) {
                    SomberAssembly.LOG.info("Scanned a zombie!");
                    unlockResearch(event.byPlayer(), SCANNED_ZOMBIE);
                }
            });

        final var itemFocusShock = ItemApi.getItem("itemFocusShock", 0);
        ResearchItemBuilder.forKeyAndCategory(SCANNED_SHOCK_FOCUS, CATEGORY)
            .virtual(true)
            .display(itemFocusShock)
            .itemTrigger(itemFocusShock)
            .register();
        SomberAssembly.proxy.itemScannedEventRegistrar()
            .registerEventListener((event) -> {
                if (itemFocusShock.getItem()
                    .equals(
                        event.scannedObject()
                            .getItem())) {
                    SomberAssembly.LOG.info("Scanned a shock focus!");
                    unlockResearch(event.byPlayer(), SCANNED_SHOCK_FOCUS);
                }
            });

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
            .textPage(1)
            .compoundRecipePage(SomberRecipes.assembleZombie)
            .secretPage(ASSEMBLE_ZOMBIE_PERFORMED, ASSEMBLE_ZOMBIE_PERFORMED)
            .parent(NECROMANCY_INTRO)
            .hiddenParent(FLESH_GOLEM, SHOCK_FOCUS, SCANNED_SHOCK_FOCUS, SCANNED_ZOMBIE)
            .register()
            .registerResearchItem();
    }

    private static void registerAssembleSkeleton() {
        ResearchItemBuilder.forKeyAndCategory(ASSEMBLE_SKELETON, CATEGORY)
            .position(1, 1)
            .display(iconSkullSkeleton)
            .textPage(1)
            .compoundRecipePage(SomberRecipes.assembleSkeleton)
            .entityTrigger("Skeleton")
            .hiddenParent(ASSEMBLE_ZOMBIE_PERFORMED)
            .lost(true)
            .register();
    }

}
