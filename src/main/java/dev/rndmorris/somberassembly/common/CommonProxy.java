package dev.rndmorris.somberassembly.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.VillagerRegistry;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.blocks.SomberBlock;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.events.EntityEvents;
import dev.rndmorris.somberassembly.common.events.ServerTickEvents;
import dev.rndmorris.somberassembly.common.items.SomberItem;
import dev.rndmorris.somberassembly.common.potions.SomberPotion;
import dev.rndmorris.somberassembly.common.recipes.SomberRecipes;
import dev.rndmorris.somberassembly.common.recipes.WandTriggerManager;
import dev.rndmorris.somberassembly.common.research.SomberResearch;
import dev.rndmorris.somberassembly.common.world.LootGeneration;
import dev.rndmorris.somberassembly.common.world.VillageGraveyardHandler;
import dev.rndmorris.somberassembly.common.world.structure.VillageGraveyardLarge;
import dev.rndmorris.somberassembly.common.world.structure.VillageGraveyardSmall;

public class CommonProxy {

    private final Map<Class<Object>, Object> SERVICES = new HashMap<>();

    public <I> I getService(Class<I> serviceType) {
        final var service = SERVICES.get(serviceType);
        if (service != null && serviceType.isAssignableFrom(service.getClass())) {
            // noinspection unchecked
            return (I) service;
        }
        return null;
    }

    public <I, C extends I> void registerService(Class<I> serviceType, C service) {
        Objects.requireNonNull(serviceType);
        Objects.requireNonNull(service);
        if (!serviceType.isAssignableFrom(service.getClass())) {
            throw new IllegalArgumentException(
                String.format(
                    "Argument 'service' (%s) is not assignable from argument 'serviceType' (%s)",
                    service.getClass()
                        .getName(),
                    serviceType.getName()));
        }
        // noinspection unchecked
        SERVICES.put((Class<Object>) serviceType, service);
    }

    // preInit "Run before anything else. Read your config, create blocks, items, etc, and register them with the
    // GameRegistry."
    public void preInit(FMLPreInitializationEvent event) {
        Config.synchronizeConfiguration(event.getSuggestedConfigurationFile());
        SomberBlock.preInit();
        SomberItem.preInit();
        EntityEvents.preInit();
        ServerTickEvents.preInit();
        try {
            MapGenStructureIO.func_143031_a(VillageGraveyardSmall.class, "SAGraveyardSmall");
            MapGenStructureIO.func_143031_a(VillageGraveyardLarge.class, "SAGraveyardLarge");
            SomberAssembly.LOG.info("Registered village graveyards.");
        } catch (Throwable err) {
            SomberAssembly.LOG.error("Could not register village graveyards.", err);
        }
    }

    // load "Do your mod setup. Build whatever data structures you care about. Register recipes."
    public void init(FMLInitializationEvent ignoredEvent) {
        SomberRecipes.init();
        VillagerRegistry.instance()
            .registerVillageCreationHandler(new VillageGraveyardHandler.SmallGraveyard());
        VillagerRegistry.instance()
            .registerVillageCreationHandler(new VillageGraveyardHandler.LargeGraveyard());
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
