package dev.rndmorris.somberassembly.research;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;

import dev.rndmorris.somberassembly.recipes.CompoundRecipe;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;

public class ResearchBuilder {

    private static String configuredCategory = null;

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
    private boolean round = false;
    private boolean secondary = false;

    private final List<String> parents = new ArrayList<>();
    private final List<String> hiddenParents = new ArrayList<>();
    private final List<String> siblings = new ArrayList<>();
    private final List<ResearchPage> pages = new ArrayList<>();
    private final List<ItemStack> itemTriggers = new ArrayList<>();
    private final List<String> entityTriggers = new ArrayList<>();
    private final List<Aspect> aspectTriggers = new ArrayList<>();

    private ResearchBuilder(String key) {
        this.key = key;
        this.category = configuredCategory;
    }

    public static void setCategory(String category) {
        configuredCategory = category;
    }

    /**
     * Initialize a new research builder for the given key.
     *
     * @param key The unique key identifying this research.
     * @return The new research builder.
     */
    public static ResearchBuilder forKey(String key) {
        return new ResearchBuilder(key);
    }

    /**
     * Add research points required to buy the research. Also determines which aspects appear
     * on the research paper.
     *
     * @param aspect The aspect to add.
     * @param cost   How much it will cost.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addAspectCost(Aspect aspect, int cost) {
        aspectCosts.add(aspect, cost);
        return this;
    }

    /**
     * Set the image that should represent the research in the 'nomicon. An ItemStack will have precedence over this.
     *
     * @param displayIcon A ResourceLocation to the image to display.
     * @return The builder, for chaining.
     */
    public ResearchBuilder setImage(ResourceLocation displayIcon) {
        this.displayIcon = displayIcon;
        return this;
    }

    /**
     * Set the item that should represent the research in the 'nomicon. Has precedence over a ResourceLocation.
     *
     * @param display A stack containing the item to use.
     * @return The builder, for chaining.
     */
    public ResearchBuilder setImage(ItemStack display) {
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
    public ResearchBuilder setPosition(int displayColumn, int displayRow) {
        this.displayColumn = displayColumn;
        this.displayRow = displayRow;
        return this;
    }

    public ResearchBuilder setComplexity(int complexity) {
        this.complexity = complexity;
        return this;
    }

    /**
     * Give the research a starry shape in the 'nomicon.
     *
     * @return The builder, for chaining.
     */
    public ResearchBuilder setSpecial() {
        this.special = true;
        return this;
    }

    public ResearchBuilder setStub() {
        this.stub = true;
        return this;
    }

    /**
     * Prevent the research from showing up in the 'nomicon until conditions are met
     * (don't ask me what those are, I"m not using it for that)
     *
     * @return The builder, for chaining.
     */
    public ResearchBuilder setLost() {
        this.lost = true;
        return this;
    }

    public ResearchBuilder setConcealed() {
        this.concealed = true;
        return this;
    }

    public ResearchBuilder setHidden() {
        this.hidden = true;
        return this;
    }

    /**
     * The rearch is technical and won't be shown in the 'nomicon.
     * We're using this for our on-scan and on-assembly research flags.
     *
     * @return The builder, for chaining.
     */
    public ResearchBuilder setVirtual() {
        this.virtual = true;
        return this;
    }

    /**
     * This research will always be unlocked. Always. Even if you set parents (I think).
     *
     * @return The builder, for chaining.
     */
    public ResearchBuilder setAutoUnlock() {
        this.autoUnlock = true;
        return this;
    }

    /**
     * Makes the research appear round in a circle in the 'nomicon instead of a square.
     *
     * @return The builder, for chaining.
     */
    @SuppressWarnings("unused")
    public ResearchBuilder setRound() {
        this.round = true;
        return this;
    }

    /**
     * Make the research buyable with research points by default.
     *
     * @return The builder, for chaining.
     */
    @SuppressWarnings("unused")
    public ResearchBuilder setSecondary() {
        this.secondary = true;
        return this;
    }

    /**
     * Add visible prerequisite research.
     *
     * @param parents The prerequsite research.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addParents(String... parents) {
        Collections.addAll(this.parents, parents);
        return this;
    }

    /**
     * Add hidden prerequisite research.
     *
     * @param hiddenParents The prerequsite research.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addHiddenParents(String... hiddenParents) {
        Collections.addAll(this.hiddenParents, hiddenParents);
        return this;
    }

    /**
     * Add research that will be unlocked when this research is.
     *
     * @param siblings The sibling research keys.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addSiblings(String... siblings) {
        Collections.addAll(this.siblings, siblings);
        return this;
    }

    /**
     * Add a basic page of text.
     *
     * @param pageNumber The number of the page to add.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addTextPage(int pageNumber) {
        this.pages.add(new ResearchPage(unlocalizedTextKey(pageNumber)));
        return this;
    }

    /**
     * Add multiple basic pages of text.
     *
     * @param startPageNumber The number of the first page to add.
     * @param endPageNumber   The number of the last page to add
     * @return The builder, for chaining.
     */
    public ResearchBuilder addTextPage(int startPageNumber, int endPageNumber) {
        for (var pageNumber = startPageNumber; pageNumber <= endPageNumber; ++pageNumber) {
            this.pages.add(new ResearchPage(unlocalizedTextKey(pageNumber)));
        }
        return this;
    }

    /**
     * Add a basic page of text.
     *
     * @param pageName The name of the page to add.
     * @return The builder, for chaining.
     */
    @SuppressWarnings("unused")
    public ResearchBuilder addTextPage(String pageName) {
        this.pages.add(new ResearchPage(unlocalizedTextKey(pageName)));
        return this;
    }

    /**
     * Add a page of text that will appear only after the required secondary research is unlocked.
     *
     * @param requiredResearch The research the page depends on.
     * @param pageName         The name of the page to add.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addSecretPage(String requiredResearch, String pageName) {
        this.pages.add(new ResearchPage(requiredResearch, unlocalizedTextKey(pageName)));
        return this;
    }

    /**
     * Add a page of text that will appear only after the required secondary research is unlocked. The research name
     * will be used as the page name.
     *
     * @param requiredResearch The research the page depends on.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addSecretPage(String requiredResearch) {
        return addSecretPage(requiredResearch, requiredResearch);
    }

    /**
     * Add a page of text that will appear only after the required secondary research is unlocked.
     *
     * @param requiredResearch The research the page depends on.
     * @param pageNumber       The number of the page to add.
     * @return The builder, for chaining.
     */
    @SuppressWarnings("unused")
    public ResearchBuilder addSecretPage(String requiredResearch, int pageNumber) {
        this.pages.add(new ResearchPage(requiredResearch, unlocalizedTextKey(pageNumber)));
        return this;
    }

    /**
     * Add a page that will render a compound recipe (e.g. a multiblock structure).
     *
     * @param recipe The compound recipe to display.
     * @return The builder, for chaining.
     */
    public ResearchBuilder addCompoundRecipePage(CompoundRecipe recipe) {
        this.pages.add(new ResearchPage(recipe.toRecipeList()));
        return this;
    }

    public ResearchBuilder addRecipePage(IRecipe... recipe) {
        this.pages.add(new ResearchPage(recipe));
        return this;
    }

    public ResearchBuilder addRecipePage(CrucibleRecipe... recipe) {
        this.pages.add(new ResearchPage(recipe));
        return this;
    }

    @SuppressWarnings("unused")
    public ResearchBuilder addItemTrigger(ItemStack itemTrigger) {
        this.itemTriggers.add(itemTrigger);
        return this;
    }

    @SuppressWarnings("unused")
    public ResearchBuilder addEntityTrigger(String entityTrigger) {
        this.entityTriggers.add(entityTrigger);
        return this;
    }

    @SuppressWarnings("unused")
    public ResearchBuilder addAspectTrigger(Aspect aspectTrigger) {
        this.aspectTriggers.add(aspectTrigger);
        return this;
    }

    public void register() {
        final var researchItem = initializeResearchItem();

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
        if (this.round) {
            researchItem.setRound();
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
    }

    private ResearchItem initializeResearchItem() {
        if (this.displayItem != null) {
            return new ResearchItem(
                this.key,
                this.category,
                this.aspectCosts,
                this.displayColumn,
                this.displayRow,
                this.complexity,
                this.displayItem);
        }
        if (this.displayIcon != null) {
            return new ResearchItem(
                this.key,
                this.category,
                this.aspectCosts,
                this.displayColumn,
                this.displayRow,
                this.complexity,
                this.displayIcon);
        }
        return new ResearchItem(this.key, this.category);
    }

    // Internal utility

    private String unlocalizedTextKey(int page) {
        return unlocalizedTextKey(String.valueOf(page));
    }

    private String unlocalizedTextKey(String page) {
        return "tc.research_page." + this.key + "." + page;
    }
}
