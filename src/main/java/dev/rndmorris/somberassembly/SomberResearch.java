package dev.rndmorris.somberassembly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.util.ResearchItemBuilder;
import joptsimple.internal.Objects;
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
    public final static String SCANNED_FOCUS_SHOCK = "SCANNED_FOCUS_SHOCK";
    public final static String SCANNED_CREEPER = "SCANNED_CREEPER";
    public final static String SCANNED_SKELETON = "SCANNED_SKELETON";
    public final static String SCANNED_ZOMBIE = "SCANNED_ZOMBIE";

    // Research keys
    public final static String NECROMANCY_INTRO = "NECROMANCY_INTRO";
    public final static String ASSEMBLE_ZOMBIE = "ASSEMBLE_ZOMBIE";
    public final static String ASSEMBLE_CREEPER_PERFORMED = "ASSEMBLE_CREEPER_PERFORMED";
    public final static String ASSEMBLE_SKELETON_PERFORMED = "ASSEMBLE_SKELETON_PERFORMED";
    public final static String ASSEMBLE_ZOMBIE_PERFORMED = "ASSEMBLE_ZOMBIE_PERFORMED";

    // External research keys
    final static String SHOCK_FOCUS = "FOCUSSHOCK";
    final static String FLESH_GOLEM = "GOLEMFLESH";

    // Icons
    private static ResourceLocation iconSinisterStone;
    private static ResourceLocation iconSkullCreeper;
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
        registerMobAssemblyResearch();
        registerVirutalResearch();
    }

    private static void registerIcons() {
        iconSinisterStone = new ResourceLocation("thaumcraft", "textures/items/sinister_stone.png");
        iconSkullCreeper = new ResourceLocation("minecraft", "textures/items/skull_creeper.png");
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
        addEntityScanResearch(SCANNED_CREEPER, EntityCreeper.class, null);
        addEntityScanResearch(SCANNED_SKELETON, EntitySkeleton.class, null);
        addEntityScanResearch(SCANNED_ZOMBIE, EntityZombie.class, null);
        addItemScanResearch(SCANNED_FOCUS_SHOCK, ItemApi.getItem("itemFocusShock", 0), true);

        // Behavior flags
        ResearchItemBuilder.forKey(ASSEMBLE_ZOMBIE_PERFORMED)
            .virtual()
            .register();
    }

    private static void addEntityScanResearch(String researchKey, Class entityClass, EntityPredicate predicate) {
        Objects.ensureNotNull(researchKey);
        Objects.ensureNotNull(entityClass);

        ResearchItemBuilder.forKey(researchKey)
            .virtual()
            .register();
        SomberAssembly.proxy.entityScannedEventRegistrar()
            .registerEventListener((event) -> {
                final var entity = event.scannedObject();
                if (entityClass.isInstance(entity) && (predicate == null || predicate.evaluate(entity))) {
                    unlockResearch(event.byPlayer(), researchKey);
                    SomberAssembly.LOG.debug("Unlocked {}!", researchKey);
                }
            });
    }

    private interface EntityPredicate {

        boolean evaluate(Entity entity);
    }

    private static void addItemScanResearch(String researchKey, ItemStack item, boolean ignoreDamage) {
        Objects.ensureNotNull(researchKey);
        Objects.ensureNotNull(item);

        ResearchItemBuilder.forKey(researchKey)
            .virtual()
            .register();
        SomberAssembly.proxy.itemScannedEventRegistrar()
            .registerEventListener((event) -> {
                if ((ignoreDamage && item.getItem()
                    .equals(
                        event.scannedObject()
                            .getItem()))
                    || item.isItemEqual(event.scannedObject())) {
                    unlockResearch(event.byPlayer(), researchKey);
                    SomberAssembly.LOG.debug("Unlocked {}!", researchKey);
                }
            });
    }

    private static void registerNecromancyIntro() {
        ResearchItemBuilder.forKey(NECROMANCY_INTRO)
            .position(0, 0)
            .display(iconSinisterStone)
            .textPage("tc.research_page.NECROMANCY_INTRO.1")
            .textPage("tc.research_page.NECROMANCY_INTRO.2")
            .autoUnlock()
            .register();
    }

    private static void registerMobAssemblyResearch() {
        ResearchItemBuilder.forKey(ASSEMBLE_ZOMBIE)
            .position(0, 2)
            .display(iconSkullZombie)
            .aspect(Aspect.FLESH, 4)
            .aspect(Aspect.UNDEAD, 8)
            .aspect(Aspect.CRAFT, 8)
            .textPage(1)
            .compoundRecipePage(SomberRecipes.assembleZombie)
            .secretPage(ASSEMBLE_ZOMBIE_PERFORMED, ASSEMBLE_ZOMBIE_PERFORMED)
            .parent(NECROMANCY_INTRO)
            .hiddenParent(FLESH_GOLEM, SHOCK_FOCUS, SCANNED_FOCUS_SHOCK, SCANNED_ZOMBIE)
            .register()
            .registerResearchItem();
        ResearchItemBuilder.forKey(ASSEMBLE_SKELETON_PERFORMED)
            .position(2, 1)
            .display(iconSkullSkeleton)
            .textPage(1)
            .compoundRecipePage(SomberRecipes.assembleSkeleton)
            .parent(ASSEMBLE_ZOMBIE)
            .hiddenParent(ASSEMBLE_ZOMBIE_PERFORMED, SCANNED_SKELETON)
            .lost()
            .register();
        ResearchItemBuilder.forKey(ASSEMBLE_CREEPER_PERFORMED)
            .position(-2, 1)
            .display(iconSkullCreeper)
            .textPage(1)
            .compoundRecipePage(SomberRecipes.assembleCreeper)
            .parent(ASSEMBLE_ZOMBIE)
            .hiddenParent(ASSEMBLE_ZOMBIE_PERFORMED, SCANNED_CREEPER)
            .lost()
            .register();
    }
}
