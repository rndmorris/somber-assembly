package dev.rndmorris.somberassembly.mixins.interfaces;

public interface IGameRules
{
    int somber_assembly$getGameRuleIntegerValue(String gameRule, int defaultValue);

    double somber_assembly$getGameRuleDoubleValue(String gameRule, double defaultValue);
}
