package dev.rndmorris.somberassembly.common.events;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.service.IWeatherMonitor;

public class ServerTickEvents implements IWeatherMonitor {

    public static void preInit() {
        final var instance = new ServerTickEvents();
        SomberAssembly.proxy.registerService(IWeatherMonitor.class, instance);
        FMLCommonHandler.instance()
            .bus()
            .register(instance);
    }

    private boolean wasRainingLastTick = false;
    private boolean wasThunderingLastTick = false;
    private long lastRainStartTime = -1;
    private long lastStormStartTime = -1;

    @SubscribeEvent
    public void onWorldTick(WorldTickEvent worldTickEvent) {
        if (worldTickEvent.phase == TickEvent.Phase.END) {
            monitorWeather(worldTickEvent);
        }
    }

    public void monitorWeather(WorldTickEvent worldTickEvent) {
        final var worldInfo = worldTickEvent.world.getWorldInfo();
        final var worldTotalTime = worldInfo.getWorldTotalTime();

        // if it started raining this tick, log the time
        if (watchForRainStart() && worldInfo.isRaining()) {
            lastRainStartTime = worldTotalTime;
            rainStarted();
        }

        // if it stopped raining this tick, start watching for it to start
        if (watchForRainStop() && !worldInfo.isRaining()) {
            rainStopped();
        }

        // if it started thundering this tick, log the time
        if (watchForThunderStart() && worldInfo.isThundering()) {
            lastStormStartTime = worldTotalTime;
            thunderStarted();
        }

        // if it stopped thundering this tick, start watching for it to start
        if (watchForThunderStop() && !worldInfo.isThundering()) {
            thunderStopped();
        }
    }

    private boolean watchForRainStart() {
        return !wasRainingLastTick;
    }

    private boolean watchForRainStop() {
        return wasRainingLastTick;
    }

    private boolean watchForThunderStart() {
        return !wasThunderingLastTick;
    }

    private boolean watchForThunderStop() {
        return wasThunderingLastTick;
    }

    private void rainStarted() {
        wasRainingLastTick = true;
    }

    private void rainStopped() {
        wasRainingLastTick = false;
    }

    private void thunderStarted() {
        wasThunderingLastTick = true;
    }

    private void thunderStopped() {
        wasThunderingLastTick = false;
    }

    @Override
    public long getLastRainStartTime() {
        return lastRainStartTime;
    }

    @Override
    public long getLastStormStartTime() {
        return lastStormStartTime;
    }
}
