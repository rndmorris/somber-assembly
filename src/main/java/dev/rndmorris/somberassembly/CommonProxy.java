package dev.rndmorris.somberassembly;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.rndmorris.somberassembly.messages.*;
import dev.rndmorris.somberassembly.messages.registrars.EventListenerRegistrar;

public class CommonProxy implements IEventRegistrarManager {

    private WandTriggerManager wandTriggerManager;

    private final IEventListenerRegistrar<EntityScannedEvent> entityScannedEventRegistrar = new EventListenerRegistrar<>();
    private final IEventListenerRegistrar<ItemScannedEvent> itemScannedEventIEventListenerRegistrar = new EventListenerRegistrar<>();

    public WandTriggerManager wandTriggerManager() {
        return this.wandTriggerManager;
    }

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
        ThaumcraftEventSnooper.init();
        wandTriggerManager = new WandTriggerManager();

        SomberResearch.init();
    }

    @Override
    public IEventListenerRegistrar<EntityScannedEvent> entityScannedEventRegistrar() {
        return this.entityScannedEventRegistrar;
    }

    @Override
    public IEventListenerRegistrar<ItemScannedEvent> itemScannedEventRegistrar() {
        return this.itemScannedEventIEventListenerRegistrar;
    }
}
