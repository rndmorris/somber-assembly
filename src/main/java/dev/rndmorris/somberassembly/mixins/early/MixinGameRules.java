package dev.rndmorris.somberassembly.mixins.early;

import dev.rndmorris.somberassembly.mixins.interfaces.IGameRules;
import dev.rndmorris.somberassembly.mixins.interfaces.IGameRulesValue;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.TreeMap;

@Mixin(GameRules.class)
@Implements(value = @Interface(iface = IGameRules.class, prefix = "somberassembly$"))
public abstract class MixinGameRules
{
    @Shadow
    private TreeMap theGameRules;

    @Unique
    public int somber_assembly$getGameRuleIntegerValue(String gameRule, int defaultValue)
    {
        if (theGameRules.get(gameRule) instanceof IGameRulesValue value) {
            return value.somber_assembly$getGameRuleIntegerValue();
        }
        return defaultValue;
    }

    @Unique
    public double somber_assembly$getGameRuleDoubleValue(String gameRule, double defaultValue)
    {
        if (theGameRules.get(gameRule) instanceof IGameRulesValue value) {
            return value.somber_assembly$getGameRuleDoubleValue();
        }
        return defaultValue;
    }
}
