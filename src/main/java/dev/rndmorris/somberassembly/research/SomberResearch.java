package dev.rndmorris.somberassembly.research;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.SomberAssembly;
import thaumcraft.api.ItemApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class SomberResearch {

    final static String CATEGORY = SomberAssembly.MODID.toUpperCase();

    // Owned research keys
    public final static String REANIMATE_ZOMBIE = "REANIMATE_ZOMBIE";

    // Referenced research keys
    final static String SHOCK_FOCUS = "FOCUSSHOCK";
    final static String FLESH_GOLEM = "GOLEMFLESH";

    public static void init() {
        registerCategory();
        // registerReanimateZombie();
    }

    private static void registerCategory() {
        final var background = new ResourceLocation("thaumcraft", "textures/gui/gui_researchbackeldritch.png");
        // final var icon = new ResourceLocation("thaumcraft", "textures/misc/r_eldritch.png");
        final var icon = new ResourceLocation("minecraft", "textures/items/skull_skeleton.png");
        ResearchCategories.registerCategory(CATEGORY, icon, background);
    }

    private static void registerReanimateZombie() {
        final var zombieResearch = new ResearchItem(
            REANIMATE_ZOMBIE,
            CATEGORY,
            new AspectList().add(Aspect.UNDEAD, 8)
                .add(Aspect.FLESH, 4)
                .add(Aspect.CRAFT, 8),
            0,
            0,
            0,
            new ItemStack(Blocks.skull, 1, 2));
        zombieResearch.setPages(new ResearchPage("tc.research_page.ASSEMBLE_ZOMBIE.1"));
        zombieResearch.setEntityTriggers("Zombie");
        zombieResearch.setItemTriggers(ItemApi.getItem("Thaumcraft:FocusShock", 0));
        zombieResearch.setParentsHidden(SHOCK_FOCUS);
        zombieResearch.setParents(FLESH_GOLEM);
        zombieResearch.registerResearchItem();;

    }
}
