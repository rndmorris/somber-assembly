package dev.rndmorris.somberassembly.mixins.early;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import dev.rndmorris.somberassembly.mixins.interfaces.IGameRulesValue;

@Mixin(targets = "net/minecraft/world/GameRules$1", remap = false)
@Implements(@Interface(iface = IGameRulesValue.class, prefix = "somberassembly$"))
public abstract class MixinGameRulesValue {

    @Shadow
    private int valueInteger;

    @Shadow
    private double valueDouble;

    @Unique
    public int somber_assembly$getGameRuleIntegerValue() {
        return valueInteger;
    }

    @Unique
    public double somber_assembly$getGameRuleDoubleValue() {
        return valueDouble;
    }
}
