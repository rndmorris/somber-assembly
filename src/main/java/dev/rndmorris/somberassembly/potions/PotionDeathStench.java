package dev.rndmorris.somberassembly.potions;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.potion.Potion;

/**
 * Potion effect to prevent zombies and skeletons from attacking affected creatures on sight.
 */
public class PotionDeathStench extends Potion {

    public static final String NAME = "potion.deathmask";

    public PotionDeathStench(int potionId) {
        super(potionId, false, 0x008000);
        setPotionName(NAME);
        setEffectiveness(1D);
    }

    @Override
    public boolean isBadEffect() {
        return false;
    }

    public static boolean affectsEntity(Entity entity) {
        return entity instanceof EntityZombie || entity instanceof EntitySkeleton;
    }
}
