package dev.rndmorris.somberassembly;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.rndmorris.somberassembly.potions.SomberPotion;

public class EntityEvents {

    public static EntityEvents INSTANCE;

    public static void init() {
        INSTANCE = new EntityEvents();
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

    @SubscribeEvent
    public void onInteract(EntityInteractEvent event) {
        villagerInteraction(event);
    }

    private void villagerInteraction(EntityInteractEvent event) {
        final var player = event.entityPlayer;
        if (event.target instanceof EntityVillager && player.isPotionActive(SomberPotion.deathStench)
            && event.isCancelable()) {
            SomberAssembly.LOG.debug("Prevented villager interaction for {}.", player.getCommandSenderName());
            event.setCanceled(true);
        }
    }
}
