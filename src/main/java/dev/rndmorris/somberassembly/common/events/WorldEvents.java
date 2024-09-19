package dev.rndmorris.somberassembly.common.events;

import dev.rndmorris.somberassembly.common.configs.SomberGameRules;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class WorldEvents {

    public static WorldEvents INSTANCE;

    public static void preInit() {
        INSTANCE = new WorldEvents();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        SomberGameRules.onWorldLoad(event.world.getGameRules());
    }
}
