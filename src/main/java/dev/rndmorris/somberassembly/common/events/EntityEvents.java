package dev.rndmorris.somberassembly.common.events;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import dev.rndmorris.somberassembly.SomberAssembly;
import dev.rndmorris.somberassembly.common.configs.Config;
import dev.rndmorris.somberassembly.common.items.SomberItem;
import dev.rndmorris.somberassembly.common.potions.SomberPotion;

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
        if (!(event.target instanceof EntityVillager && event.entityPlayer.isPotionActive(SomberPotion.deathStench)
            && event.isCancelable())) {
            return;
        }
        SomberAssembly.LOG.debug("Prevented villager interaction for {}.", event.entityPlayer.getCommandSenderName());
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) throws InterruptedException {
        zombieDeath(event);
    }

    private void zombieDeath(LivingDropsEvent event) {
        if (!(event.entityLiving instanceof EntityZombie && event.recentlyHit)) {
            return;
        }
        final var rolls = 1 + event.lootingLevel;
        final var flesh = SomberItem.decayingFlesh(1);
        for (var i = 0; i < rolls; ++i) {
            var rand = event.entityLiving.worldObj.rand.nextInt(Config.decayingFleshDropRate);
            if (rand == 0) {
                event.drops.add(
                    new EntityItem(
                        event.entityLiving.worldObj,
                        event.entityLiving.posX,
                        event.entityLiving.posY,
                        event.entityLiving.posZ,
                        flesh.copy()));
            }
        }
    }
}
