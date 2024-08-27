package dev.rndmorris.somberassembly;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dev.rndmorris.somberassembly.messages.EntityScannedEvent;
import dev.rndmorris.somberassembly.messages.IEventListenerRegistrar;
import dev.rndmorris.somberassembly.messages.IEventRegistrarManager;
import dev.rndmorris.somberassembly.messages.ItemScannedEvent;
import dev.rndmorris.somberassembly.messages.ThaumcraftEventSnooper;
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

    public void playSound(World world, double x, double y, double z, String soundName, float volume, float pitch,
        boolean distanceDelay) {
        world.playSound(x, y, z, soundName, volume, pitch, distanceDelay);
    }

    public void playSoundEffect(World world, double x, double y, double z, String soundName, float volume,
        float pitch) {
        world.playSoundEffect(x, y, z, soundName, volume, pitch);
    }

    public void playSoundAtEntity(Entity entity, String soundName, float volume, float pitch) {
        entity.worldObj.playSoundAtEntity(entity, soundName, volume, pitch);
    }

    public void playDiscoverySounds(EntityPlayer atPlayer) {
        final var world = atPlayer.worldObj;
        if (world.isRemote) {
            playSoundAtEntity(atPlayer, "Thaumcraft:write", 2.0f, 1F + (world.rand.nextFloat() * 0.5F));
            playSoundAtEntity(atPlayer, "Thaumcraft:learn", 2.0f, 1F + (world.rand.nextFloat() * 0.5F));
        }
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
