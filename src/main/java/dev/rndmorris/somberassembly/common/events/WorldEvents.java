package dev.rndmorris.somberassembly.common.events;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import static dev.rndmorris.somberassembly.common.configs.GameRules.DO_VIRAL_ICE_TICK;
import static dev.rndmorris.somberassembly.common.configs.GameRules.VIRAL_ICE_TICK_RATE;
import static dev.rndmorris.somberassembly.common.configs.GameRules.VIRAL_ICE_TICK_RATE_VARIATION;

public class WorldEvents
{
    public static WorldEvents INSTANCE;

    public static void preInit() {
        INSTANCE = new WorldEvents();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        final var gameRules = event.world.getGameRules();
        if (!gameRules.hasRule(DO_VIRAL_ICE_TICK)) {
            gameRules.setOrCreateGameRule(DO_VIRAL_ICE_TICK, "true");
        }
        if (!gameRules.hasRule(VIRAL_ICE_TICK_RATE)) {
            gameRules.setOrCreateGameRule(VIRAL_ICE_TICK_RATE, "30");
        }
        if (!gameRules.hasRule(VIRAL_ICE_TICK_RATE_VARIATION)) {
            gameRules.setOrCreateGameRule(VIRAL_ICE_TICK_RATE_VARIATION, "10");
        }
    }
}
