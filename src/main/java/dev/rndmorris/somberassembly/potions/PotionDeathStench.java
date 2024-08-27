package dev.rndmorris.somberassembly.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Potion effect to prevent zombies and skeletons from attacking affected creatures on sight.
 */
public class PotionDeathStench extends SomberPotion {

    public static final String NAME = "death_stench";

    public PotionDeathStench(int potionId) {
        super(potionId, false, 0x008000);
        setPotionName(NAME);
        setEffectiveness(1D);
    }

    @Override
    public boolean isBadEffect() {
        return false;
    }

    public static boolean makesNeutral(Entity entity) {
        return entity instanceof EntityZombie;
    }

    public static boolean makesHostile(Entity entity) {
        return entity instanceof EntityIronGolem || entity instanceof EntitySnowman;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 100 == 0; // every five seconds
    }

    @Override
    public void performEffect(EntityLivingBase affected, int amplifier) {
        if (affected instanceof EntityPlayer player && player.capabilities.isCreativeMode) {
            return;
        }
        final var range = 8D;
        final var entitiesToAggro = affected.worldObj.selectEntitiesWithinAABB(
            EntityLivingBase.class,
            affected.boundingBox.expand(range, range, range),
            PotionDeathStench::makesHostile);
        for (final var entity : entitiesToAggro) {
            if (entity.getAITarget() == null) {
                entity.setRevengeTarget(affected);
            }
        }
    }
}
