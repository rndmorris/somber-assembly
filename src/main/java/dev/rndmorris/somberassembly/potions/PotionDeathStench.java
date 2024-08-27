package dev.rndmorris.somberassembly.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

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
        return entity instanceof EntityIronGolem || entity instanceof EntityVillager;
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 100 == 0; // every five seconds
    }

    @Override
    public void performEffect(EntityLivingBase affected, int amplifier) {
        final var entitiesToHarm = affected.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, affected.boundingBox.expand(10, 10, 10), PotionDeathStench::makesHostile);
        final var damageSource = affected instanceof EntityPlayer player ? DamageSource.causePlayerDamage(player) : DamageSource.causeMobDamage(affected);
        for (final var entity : entitiesToHarm) {
            entity.attackEntityFrom(damageSource, 0);
        }
    }
}
