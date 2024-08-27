package dev.rndmorris.somberassembly;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.rndmorris.somberassembly.wandtriggers.SkeletonNecromancyTrigger;
import dev.rndmorris.somberassembly.wandtriggers.ZombieNecromancyTrigger;

public class CommonProxy {

    ZombieNecromancyTrigger zombieNecromancyTrigger;
    SkeletonNecromancyTrigger skeletonNecromancyTrigger;

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        SomberBlocks.init();
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent ignoredEvent) {
        SomberRecipes.preInit();
    }

    // postInit "Handle interaction with other mods, complete your setup based on this."
    public void postInit(FMLPostInitializationEvent ignoredEvent) {
        zombieNecromancyTrigger = new ZombieNecromancyTrigger();
        skeletonNecromancyTrigger = new SkeletonNecromancyTrigger();
    }
}
