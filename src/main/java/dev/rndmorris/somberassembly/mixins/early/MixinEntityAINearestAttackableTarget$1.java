package dev.rndmorris.somberassembly.mixins.early;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.common.potions.PotionDeathStench;
import dev.rndmorris.somberassembly.common.potions.SomberPotion;

@Mixin(targets = "net/minecraft/entity/ai/EntityAINearestAttackableTarget$1")
public abstract class MixinEntityAINearestAttackableTarget$1 implements IEntitySelector {

    @Unique
    private EntityAINearestAttackableTarget this$0;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    public void onInit(EntityAINearestAttackableTarget this$0, IEntitySelector instance, CallbackInfo ci) {
        this.this$0 = this$0;
    }

    @SuppressWarnings("ReferenceToMixin")
    @Inject(method = "isEntityApplicable", at = @At(value = "HEAD"), cancellable = true)
    public void onIsEntityApplicable(Entity target, CallbackInfoReturnable<Boolean> cir) {
        final var taskOwner = ((ITaskOwner) this.this$0).getTaskOwner();
        if (!PotionDeathStench.makesNeutral(taskOwner)) {
            return;
        }
        final var hasStench = target instanceof EntityLivingBase living
            && living.isPotionActive(SomberPotion.deathStench.id);
        if (hasStench) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
