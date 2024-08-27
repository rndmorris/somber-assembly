package dev.rndmorris.somberassembly;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

    // CreeperNecromancyTrigger creeperNecromancyTrigger;
    // SkeletonNecromancyTrigger skeletonNecromancyTrigger;
    // ZombieNecromancyTrigger zombieNecromancyTrigger;

    WandTriggerManager wandTriggerManager;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        SomberBlocks.init();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent ignoredEvent) {
        SomberRecipes.init();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent ignoredEvent) {
        SomberResearch.init();
        wandTriggerManager = new WandTriggerManager();
        // creeperNecromancyTrigger = new CreeperNecromancyTrigger();
        // skeletonNecromancyTrigger = new SkeletonNecromancyTrigger();
        // zombieNecromancyTrigger = new ZombieNecromancyTrigger();
    }
}
