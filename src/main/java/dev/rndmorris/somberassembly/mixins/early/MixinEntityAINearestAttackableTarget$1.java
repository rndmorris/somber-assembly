package dev.rndmorris.somberassembly.mixins.early;

import java.lang.reflect.Field;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITarget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.lib.Reflections;
import dev.rndmorris.somberassembly.potions.PotionDeathStench;
import dev.rndmorris.somberassembly.potions.SomberPotion;

@Mixin(targets = "net/minecraft/entity/ai/EntityAINearestAttackableTarget$1")
public abstract class MixinEntityAINearestAttackableTarget$1 implements IEntitySelector {

    // Attempts to expose taskOwner via Mixins did not go well (weird errors about not found classes).
    // For now, we use Reflection instead.
    @Unique
    private static final Field taskOwnerField = Reflections
        .setAccessible(Reflections.findField(EntityAITarget.class, "field_75299_d", "taskOwner"));

    @Unique
    private EntityAINearestAttackableTarget this$0;

    @Unique
    private EntityCreature taskOwner() {
        try {
            return (EntityCreature) taskOwnerField.get(this$0);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onInit(EntityAINearestAttackableTarget this$0, IEntitySelector instance, CallbackInfo ci) {
        this.this$0 = this$0;
    }

    @Inject(method = "isEntityApplicable", at = @At(value = "HEAD"), cancellable = true)
    public void onIsEntityApplicable(Entity target, CallbackInfoReturnable<Boolean> cir) {
        final var taskOwner = this.taskOwner();
        final var hasStench = target instanceof EntityLivingBase living
            && living.isPotionActive(SomberPotion.deathMask.id);
        if (!hasStench) {
            return;
        }
        if (PotionDeathStench.makesNeutral(taskOwner)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    // /**
    // * Makes the Death Mask potion effect work
    // */
    // @Inject(method = "isSuitableTarget", at = @At(value = "HEAD"), cancellable = true, remap = false)
    // private void onIsSuitableTarget(EntityLivingBase target, boolean idk, CallbackInfoReturnable<Boolean> cir) {
    // final var lastAttacker = taskOwner.getLastAttacker();
    // final var attackingPlayer = taskOwner.getattackin
    // if (lastAttacker != null) {
    // SomberAssembly.LOG.info(
    // "Last attacker: {}",
    // lastAttacker.getClass()
    // .getName());
    // }
    // if (target == null) {
    // return;
    // }
    //
    // final var targetDeathMask = target.getActivePotionEffect(SomberPotion.deathMask);
    // if (PotionDeathStench.affectsEntity(taskOwner) && targetDeathMask != null) {
    // cir.setReturnValue(false);
    // cir.cancel();
    // }
    // }
}
