package dev.rndmorris.somberassembly.mixins.early;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITarget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.potions.PotionDeathStench;
import dev.rndmorris.somberassembly.potions.SomberPotion;

@Mixin(EntityAITarget.class)
public abstract class MixinEntityAITarget extends EntityAIBase {

    @Shadow
    protected EntityCreature taskOwner;

    /**
     * Makes the Death Mask potion effect work
     */
    @Inject(method = "isSuitableTarget", at = @At(value = "HEAD"), cancellable = true, remap = false)
    private void onIsSuitableTarget(EntityLivingBase target, boolean idk, CallbackInfoReturnable<Boolean> cir) {
        if (target == null) {
            return;
        }
        final var targetDeathMask = target.getActivePotionEffect(SomberPotion.deathMask);
        if (PotionDeathStench.affectsEntity(taskOwner) && targetDeathMask != null) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
