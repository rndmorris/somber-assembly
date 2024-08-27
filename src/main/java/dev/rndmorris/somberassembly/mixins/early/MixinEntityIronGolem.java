package dev.rndmorris.somberassembly.mixins.early;

import dev.rndmorris.somberassembly.potions.SomberPotion;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(EntityIronGolem.class)
public abstract class MixinEntityIronGolem
{

//    @Shadow
//    public EntityAITasks targetTasks;
//
//    @Inject(method = "<init>", at = @At(value = "RETURN"), locals = LocalCapture.PRINT)
//    public void oninit() {
//        targetTasks.addTask(3, new EntityAINearestAttackableTarget((EntityCreature) this, EntityLiving.class, 0, false, true, e -> e instanceof EntityLiving living && living.isPotionActive(SomberPotion.deathMask)));
//    }
}
