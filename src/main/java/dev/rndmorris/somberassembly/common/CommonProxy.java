package dev.rndmorris.somberassembly.common;

import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.rndmorris.somberassembly.common.blocks.SomberBlock;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.events.EntityEvents;
import dev.rndmorris.somberassembly.common.items.SomberItem;
import dev.rndmorris.somberassembly.common.potions.SomberPotion;
import dev.rndmorris.somberassembly.common.recipes.SomberRecipes;
import dev.rndmorris.somberassembly.common.recipes.WandTriggerManager;
import dev.rndmorris.somberassembly.common.research.SomberResearch;

public class CommonProxy {

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        SomberBlock.preInit();
        SomberItem.preInit();
        EntityEvents.preInit();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent ignoredEvent) {
        SomberRecipes.init();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent ignoredEvent) {
        SomberPotion.postInit();
        WandTriggerManager.postInit();
        SomberResearch.postInit();
        SomberItem.postInit();
        LootGeneration.init();
    }

    public void playSoundEffect(World world, double x, double y, double z, String soundName, float volume,
        float pitch) {
        world.playSoundEffect(x, y, z, soundName, volume, pitch);
    }
}
