package dev.rndmorris.somberassembly.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import dev.rndmorris.somberassembly.SomberAssembly;

@Mixin(value = World.class)
public abstract class MixinWorld {

    @Inject(at = @At(value = "HEAD"), method = "getClosestVulnerablePlayerToEntity")
    private void getClosestVulnerablePlayerToEntity(Entity entityIn, double distance,
        CallbackInfoReturnable<EntityPlayer> ci) {
        SomberAssembly.LOG.info("Yo");
    }

}
