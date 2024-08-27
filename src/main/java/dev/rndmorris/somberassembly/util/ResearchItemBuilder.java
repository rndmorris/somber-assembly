package dev.rndmorris.somberassembly.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ResearchItemBuilder {

    private final String key;
    private final String category;
    private final AspectList aspectCosts = new AspectList();

    private int displayColumn = 0;
    private int displayRow = 0;
    private ResourceLocation displayIcon;
    private ItemStack displayItem;
    private int complexity = 1;

    private boolean special = false;
    private boolean stub = false;
    private boolean lost = false;
    private boolean concealed = false;
    private boolean hidden = false;
    private boolean virtual = false;
    private boolean autoUnlock = false;
    private boolean secondary = false;

    private final List<String> parents = new ArrayList<>();
    private final List<String> hiddenParents = new ArrayList<>();
    private final List<String> siblings = new ArrayList<>();
    private final List<ResearchPage> pages = new ArrayList<>();
    private final List<ItemStack> itemTriggers = new ArrayList<>();
    private final List<String> entityTriggers = new ArrayList<>();
    private final List<Aspect> aspectTriggers = new ArrayList<>();

    private ResearchItemBuilder(String key, String category) {
        this.key = key;
        this.category = category;
    }

    /**
     * Initialize a new research builder for the given key and category.
     *
     * @param key      The unique key identifying this research.
     * @param category The tab of the Thaumonomicon to which this research belongs.
     * @return The new research builder.
     */
    public static ResearchItemBuilder forKeyAndCategory(String key, String category) {
        return new ResearchItemBuilder(key, category);
    }

    public ResearchItemBuilder aspect(Aspect aspect, int cost) {
        aspectCosts.add(aspect, cost);
        return this;
    }

    public ResearchItemBuilder display(ResourceLocation displayIcon) {
        this.displayIcon = displayIcon;
        return this;
    }

    public ResearchItemBuilder display(ItemStack display) {
        this.displayItem = display;
        return this;
    }

    /**
     * Set the coordinates at which the research will appear in the Thaumonomicon.
     *
     * @param displayColumn The column in which the research will appear.
     * @param displayRow    The row in which the research will appear.
     * @return The builder, for chaining.
     */
    public ResearchItemBuilder position(int displayColumn, int displayRow) {
        this.displayColumn = displayColumn;
        this.displayRow = displayRow;
        return this;
    }

    public ResearchItemBuilder complexity(int complexity) {
        this.complexity = complexity;
        return this;
    }

    public ResearchItemBuilder special(boolean special) {
        this.special = special;
        return this;
    }

    public ResearchItemBuilder stub(boolean stub) {
        this.stub = stub;
        return this;
    }

    public ResearchItemBuilder lost(boolean lost) {
        this.lost = lost;
        return this;
    }

    public ResearchItemBuilder concealed(boolean concealed) {
        this.concealed = concealed;
        return this;
    }

    public ResearchItemBuilder hidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ResearchItemBuilder virtual(boolean virtual) {
        this.virtual = virtual;
        return this;
    }

    public ResearchItemBuilder autoUnlock(boolean autoUnlock) {
        this.autoUnlock = autoUnlock;
        return this;
    }

    public ResearchItemBuilder secondary(boolean secondary) {
        this.secondary = secondary;
        return this;
    }

    public ResearchItemBuilder parent(String parent) {
        this.parents.add(parent);
        return this;
    }

    public ResearchItemBuilder hiddenParent(String hiddenParent) {
        this.hiddenParents.add(hiddenParent);
        return this;
    }

    public ResearchItemBuilder sibling(String sibling) {
        this.siblings.add(sibling);
        return this;
    }

    /**
     * Add a basic page of text.
     *
     * @param text The langfile key of the page's text.
     * @return The builder, for chaining.
     */
    public ResearchItemBuilder textPage(String text) {
        this.pages.add(new ResearchPage(text));
        return this;
    }

    /**
     * Add a page of text that will appear only after the required secondary research is unlocked.
     *
     * @param requiredResearch The research the page depends on.
     * @param text             The langfile key of the page's text
     * @return The builder, for chaining.
     */
    public ResearchItemBuilder secretPage(String requiredResearch, String text) {
        this.pages.add(new ResearchPage(requiredResearch, text));
        return this;
    }

    /**
     * Add a page that will render a compound recipe (e.g. a multiblock structure).
     *
     * @param recipe The compound recipe to display.
     * @return The builder, for chaining.
     */
    public ResearchItemBuilder compoundRecipePage(CompoundRecipe recipe) {
        this.pages.add(new ResearchPage(recipe.toRecipeList()));
        return this;
    }

    public ResearchItemBuilder itemTrigger(ItemStack itemTrigger) {
        this.itemTriggers.add(itemTrigger);
        return this;
    }

    public ResearchItemBuilder entityTrigger(String entityTrigger) {
        this.entityTriggers.add(entityTrigger);
        return this;
    }

    public ResearchItemBuilder aspectTrigger(Aspect aspectTrigger) {
        this.aspectTriggers.add(aspectTrigger);
        return this;
    }

    public ResearchItem register() {
        final var researchItem = this.displayItem != null
            ? new ResearchItem(
                this.key,
                this.category,
                this.aspectCosts,
                this.displayColumn,
                this.displayRow,
                this.complexity,
                this.displayItem)
            : new ResearchItem(
                this.key,
                this.category,
                this.aspectCosts,
                this.displayColumn,
                this.displayRow,
                this.complexity,
                this.displayIcon);

        if (this.special) {
            researchItem.setSpecial();
        }
        if (this.stub) {
            researchItem.setStub();
        }
        if (this.lost) {
            researchItem.setLost();
        }
        if (this.concealed) {
            researchItem.setConcealed();
        }
        if (this.hidden) {
            researchItem.setHidden();
        }
        if (this.virtual) {
            researchItem.setVirtual();
        }
        if (this.autoUnlock) {
            researchItem.setAutoUnlock();
        }
        if (this.secondary) {
            researchItem.setSecondary();
        }
        if (!this.parents.isEmpty()) {
            researchItem.setParents(this.parents.toArray(new String[0]));
        }
        if (!this.hiddenParents.isEmpty()) {
            researchItem.setParentsHidden(this.hiddenParents.toArray(new String[0]));
        }
        if (!this.siblings.isEmpty()) {
            researchItem.setSiblings(this.siblings.toArray(new String[0]));
        }
        if (!this.pages.isEmpty()) {
            researchItem.setPages(this.pages.toArray(new ResearchPage[0]));
        }
        if (!this.itemTriggers.isEmpty()) {
            researchItem.setItemTriggers(this.itemTriggers.toArray(new ItemStack[0]));
        }
        if (!this.entityTriggers.isEmpty()) {
            researchItem.setEntityTriggers(this.entityTriggers.toArray(new String[0]));
        }
        if (!this.aspectTriggers.isEmpty()) {
            researchItem.setAspectTriggers(this.aspectTriggers.toArray(new Aspect[0]));
        }

        researchItem.registerResearchItem();

        return researchItem;
    }
}
