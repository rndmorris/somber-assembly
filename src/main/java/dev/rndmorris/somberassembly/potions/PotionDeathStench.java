package dev.rndmorris.somberassembly.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityZombie;

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
        return entity instanceof EntityIronGolem;
    }
}
