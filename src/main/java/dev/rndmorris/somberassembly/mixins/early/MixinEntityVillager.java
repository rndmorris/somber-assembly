package dev.rndmorris.somberassembly.mixins.early;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.potions.SomberPotion;

@Mixin(EntityVillager.class)
public abstract class MixinEntityVillager extends EntityAgeable {

    public MixinEntityVillager(World p_i1578_1_) {
        super(p_i1578_1_);
    }

    @Inject(
        id = "EntityVillager.interact for PotionDeathStench",
        target = @Desc(value = "interact", args = EntityPlayer.class, ret = boolean.class),
        at = @At(value = "HEAD"),
        cancellable = true)
    public void onInteract(EntityPlayer player, CallbackInfoReturnable<Boolean> cir) {
        if (player.isPotionActive(SomberPotion.deathMask)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
